package src.main.java.cnt.client;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.GZIPInputStream;

import src.main.java.cnt.protocol.Config;
import src.main.java.cnt.protocol.Handshake;
import src.main.java.cnt.protocol.Message;
import src.main.java.cnt.server.Peer;
import sun.security.util.ArrayUtil;

public class Client {
    Socket requestSocket;           //socket connect to the server
    ObjectOutputStream out;         //stream write to the socket
    ObjectInputStream in;          //stream read from the socket
    String id;

    // From common.cfg
    Peer selfInfo;
    boolean hasDownloadStarted;
    byte[] fileContents;

    Handshake handshakeMessage = new Handshake();

    final String HANDSHAKE_HEADER = "P2PFILESHARINGPROJ";
    private ArrayList<String> clientList = new ArrayList<String>();
    List<Integer> missing = new ArrayList<>();

    final byte BYTES_PIECE_SIZE = 4;

    public Client(String id) {
        this.id = id;
        fileContents = new byte[Config.getFileSize()];

        log(Config.getString());

        for(int i = 0; i < Config.getPeers().size(); i++) {
            if (Config.getPeers().get(i).getPeerID().equals(this.id))
                this.selfInfo = Config.getPeers().get(i);
        }

        this.hasDownloadStarted = this.selfInfo.getBitfield()[0] != 0;
    }

    void run() {
        try {
            //create a socket to connect to the server
            requestSocket = new Socket("localhost", 8000);
            System.out.println("Connected to localhost in port 8000");
            //initialize inputStream and outputStream
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());

            //get Input from standard input
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

            // create handshake message and send to server
            handshakeMessage.createHandshakeMessage(id);
            String message = handshakeMessage.getHandshake();
            sendMessage(message);

            // Get handshake from server (or peer), and validate it
            String MESSAGE = (String) in.readObject();
            handshakeMessage.validateHandshake(MESSAGE, message);
            log(MESSAGE);

            // Send bitfield message if it has any
            if (hasDownloadStarted) {
                sendMessage(new Message(selfInfo.getBitfield().length, Message.Type.BITFIELD, selfInfo.getBitfield()));
                log(((Message)in.readObject()).toString());
            }

            boolean shouldExit = false;

            while(!shouldExit) {
                Message msgObj = (Message) in.readObject();

                switch(msgObj.getType()) {
                    case CHOKE:
                        break; //TODO CHOKE
                    case UNCHOKE:
                        break; //TODO UNCHOKE
                    case INTERESTED:
                        break; //TODO INTERESTED
                    case NOT_INTERESTED:
                        break; //TODO NOT INTERESTED
                    case HAVE:
                        break; //TODO HAVE
                    case BITFIELD:
                        break; //TODO BITFIELD
                    case REQUEST: {
                        byte[] piece = new byte[Config.getPieceSize() + BYTES_PIECE_SIZE];

                        ByteBuffer wrapped = ByteBuffer.wrap(msgObj.getPayload()); // big-endian by default
                        int index = wrapped.getInt();
                        int offset = index * Config.getPieceSize();

                        // Make the first 4 bytes the index
                        for(int i = 0; i < BYTES_PIECE_SIZE; i++)
                            piece[i] = msgObj.getPayload()[i];

                        // Populate rest of payload with byte information
                        for (int i = 0; (i < Config.getPieceSize()) && (i + offset < fileContents.length); i++)
                            piece[i+BYTES_PIECE_SIZE] = fileContents[i + offset];

                        sendMessage(new Message(Config.getPieceSize() + BYTES_PIECE_SIZE, Message.Type.PIECE, piece));
                        log("Sending piece #" + index);
                        break;
                    }
                    case PIECE: {
                        hasDownloadStarted = true;

                        ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(msgObj.getPayload(), 0, BYTES_PIECE_SIZE));
                        int index = wrapped.getInt();


                        int offset = index * Config.getPieceSize();
                        for (int i = 0; (i < msgObj.getPayload().length - BYTES_PIECE_SIZE) && (i+offset < fileContents.length); i++) {
                            fileContents[i+offset] = msgObj.getPayload()[i+BYTES_PIECE_SIZE];
                        }

                        missing.remove(Integer.valueOf(index));
                    }

                    default:
                        log("Received unsupported message! Exiting...");
                        shouldExit = true;
                }
            }

            log("TRANSFER FINISHED");

        } catch (ConnectException e) {
            System.err.println("Connection refused. You need to initiate a server first.");
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found");
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            //Close connections
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    //send a message to the output stream, used for handshake
    void sendMessage(String msg) {
        try {
            //stream write the message
            out.writeObject(msg);
            out.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    //send a message using the message object
    void sendMessage(Message msg) {
        try {
            //stream write the message
            out.writeObject(msg);
            out.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    //print and log message to file
    void log(String msg) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy MM dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        msg = dtf.format(now) + ": " + msg;
        try (FileWriter fw = new FileWriter("log_peer_" + id + ".log", true)) {
            fw.write(msg + '\n');
            System.out.println(msg);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.exit(2);
        }
    }

    //load full file into memory to share
    void loadFile() {
        File dir = new File(id);
        String filePath = id + "/" + Objects.requireNonNull(dir.list())[0];
        try (FileInputStream fs = new FileInputStream(filePath)){
            fs.read(fileContents);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //main method
    public static void main(String args[]) {
        if (args.length == 0) {
            System.err.println("Must be supplied ID as argument.");
            System.exit(1);
        } else if (args[0].length() != 4) {
            System.err.println("ID must be 4 characters long.");
            System.exit(1);
        }

        Config.loadCommon();
        Config.loadPeerInfo();

        System.out.println("Running the client: " + args[0]);
        Client client = new Client(args[0]);
        client.run();

    }

}
