package src.main.java.cnt.client;

import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import src.main.java.cnt.protocol.Handshake;
import src.main.java.cnt.protocol.Message;

public class Client {
    Socket requestSocket;           //socket connect to the server
    ObjectOutputStream out;         //stream write to the socket
    ObjectInputStream in;          //stream read from the socket
    String id;

    // From common.cfg
    int numPreferredNeighbors;
    int unchokingInterval;
    int optimisticUnchokingInterval;
    byte[] bitfield;

    boolean hasFile;
    boolean hasDownloadStarted;

    Handshake handshakeMessage = new Handshake();

    final String HANDSHAKE_HEADER = "P2PFILESHARINGPROJ";
    private ArrayList<String> clientList = new ArrayList<String>();

    public Client(String id, int numPreferredNeighbors, int unchokingInterval, int optimisticUnchokingInterval, int bitfieldLength, boolean hasFile) {
        this.id = id;
        this.numPreferredNeighbors = numPreferredNeighbors;
        this.unchokingInterval = unchokingInterval;
        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
        this.hasFile = hasFile;
        this.hasDownloadStarted = hasFile;
        bitfield = new byte[bitfieldLength];


        if(hasFile)
            for (int i = 0; i < bitfieldLength; i++)
                bitfield[i] = 1;
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

            // Various tests
            sendMessage(new Message(1, Message.Type.CHOKE, null));
            log(((Message)in.readObject()).toString());

            sendMessage(new Message(1, Message.Type.UNCHOKE, null));
            log(((Message)in.readObject()).toString());

            sendMessage(new Message(1, Message.Type.INTERESTED, null));
            log(((Message)in.readObject()).toString());

            sendMessage(new Message(1, Message.Type.NOT_INTERESTED, null));
            log(((Message)in.readObject()).toString());

            sendMessage(new Message(5, Message.Type.HAVE, new byte[]{0, 2, 3, 4}));
            log(((Message)in.readObject()).toString());

            // Send bitfield message if it has any
            if (hasDownloadStarted) {
                sendMessage(new Message(bitfield.length, Message.Type.BITFIELD, bitfield));
            }
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

    //main method
    public static void main(String args[]) {
        if (args.length == 0) {
            System.err.println("Must be supplied ID as argument.");
            System.exit(1);
        } else if (args[0].length() != 4) {
            System.err.println("ID must be 4 characters long.");
            System.exit(1);
        }

        boolean hasFile = false;
        if (args.length > 1 && Objects.equals(args[1], "1")) {
            hasFile = true;
        }

        // read the common.cfg file and set variables
        System.out.println("Reading Common.cfg...");
        Properties prop = new Properties();

        try (FileInputStream fs = new FileInputStream("Common.cfg")) {
            prop.load(fs);
        } catch (FileNotFoundException ex) {
            System.err.println("Common.cfg not found！");
            System.exit(2);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.exit(2);
        }

        int bitfieldLength = 0;

        double fileSize = Double.parseDouble(prop.getProperty("FileSize"));
        double pieceSize = Double.parseDouble(prop.getProperty("PieceSize"));

        bitfieldLength = (int)Math.ceil(fileSize / (pieceSize));

        int numNeighbors = Integer.parseInt(prop.getProperty("NumberOfPreferredNeighbors"));
        int unchokingInterval = Integer.parseInt(prop.getProperty("UnchokingInterval"));
        int optimisticUnchoke = Integer.parseInt(prop.getProperty("OptimisticUnchokingInterval"));

        System.out.println("Running the client: " + args[0]);
        Client client = new Client(args[0],
                numNeighbors,
                unchokingInterval,
                optimisticUnchoke,
                bitfieldLength,
                hasFile);
        client.run();

    }

}
