package src.main.java.cnt.client;

import src.main.java.cnt.protocol.*;
import src.main.java.cnt.server.Peer;
import src.main.java.cnt.protocol.Config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class PeerHandler extends Thread {
    Socket requestSocket;           //socket connect to the server
    ObjectOutputStream out;         //stream write to the socket
    ObjectInputStream in;          //stream read from the socket
    Handshake handshakeMessage = new Handshake();
    Peer remoteInfo = null;
    ClientState state;

    public PeerHandler(Peer peer, ClientState state) {
        this.requestSocket = peer.getSocket();
        this.out = peer.getOutStream();
        this.in = peer.getInStream();
        this.remoteInfo = peer;
        this.state = state;
    }
    public PeerHandler(Socket socket, ClientState state) {
        try {
            this.requestSocket = socket;
            out = new ObjectOutputStream(socket.getOutputStream()); //stream write to the socket
            out.flush();
            in = new ObjectInputStream(socket.getInputStream()); //stream read from the socket
        } catch (IOException e) {
            System.out.println("ERROR MAKING PEER HANDLER SOCKET");
        }
        this.state = state;
    }

    public void run() {
        try {
            // create handshake message and send to server
            handshakeMessage.createHandshakeMessage(state.getSelfInfo().getPeerID());
            String message = handshakeMessage.getHandshake();
            sendMessage(message);
            //state.log(state.getSelfInfo().getPeerID() + " sent handshake", state.getSelfInfo().getPeerID());

            // Get handshake from server (or peer), no need to validate
            String MESSAGE = (String) in.readObject();
            //handshakeMessage.validateHandshake(MESSAGE, message);
            //state.log(MESSAGE, state.getSelfInfo().getPeerID());

            // Remote peer is null because they connected to us, rather than us connect to them
            if(remoteInfo == null) {
                // Last 4 char of header should be peerID
                String peerID = MESSAGE.substring(MESSAGE.length() - 4);

                for (Peer peer : state.getPeers()) {
                    if (peer.getPeerID().equals(peerID))
                        remoteInfo = peer;
                }

                if(remoteInfo == null)
                    throw new RuntimeException("New Peer not in peer list");
            }

            state.log(String.format("Peer %s makes a connection to peer %s", state.getSelfInfo().getPeerID(), remoteInfo.getPeerID()));

            // Send bitfield message if it has any
            if (state.hasDownloadStarted()) {
                sendMessage(new Message(remoteInfo.getBitfield().length, Message.Type.BITFIELD, remoteInfo.getBitfield()));
                state.log(in.readObject().toString());
            }

            // if it has the file, send bitfield
            if(state.getSelfInfo().getHasFile() == 1){
                sendMessage(new Message(remoteInfo.getBitfield().length, Message.Type.BITFIELD, state.getBitfield().getBitfield()));
            }

            boolean shouldExit = false;

            // Infinitely loop until client want to terminate (not interested?)
            while(!shouldExit) {
                Message msgObj = (Message) in.readObject();

                switch(msgObj.getType()) {
                    case CHOKE:
                        break; //TODO CHOKE
                    case UNCHOKE:
                        state.log("Peer " + state.getSelfInfo().getPeerID() + " is unchoked by " + remoteInfo.getPeerID());
                        // get random missing index and turn into byte[]
                        int random = getRandomPieceIndex();
                        byte[] requestIndex = intToByteArray(random);
                        // send request message if unchoked by peer
                        sendMessage(new Message(4, Message.Type.REQUEST, requestIndex));
                        break;
                    case INTERESTED:
                        state.log("Peer " + state.getSelfInfo().getPeerID() + " received the 'interested' message from " + remoteInfo.getPeerID());
                        // will receive interested and send have -1
                        sendMessage(new Message(4, Message.Type.UNCHOKE, null));
                        break;
                    case NOT_INTERESTED:
                        state.log("Peer " + state.getSelfInfo().getPeerID() + " received the 'not interested' message from " + remoteInfo.getPeerID());
                        // TODO: End condition: means it has file, should wait for all peers to download all pieces, shuts down connection
                        break; //TODO NOT INTERESTED
                    case HAVE:
                        state.log("Peer " + state.getSelfInfo().getPeerID() + " received the 'have' message from " + remoteInfo.getPeerID());
                        break; //TODO HAVE
                    case BITFIELD:
                        state.log("Peer " + state.getSelfInfo().getPeerID() + " received the 'bitfield' message from " + remoteInfo.getPeerID());

                        // update the bitfield of the peer who sent it
                        if(remoteInfo.getBitfieldObj().compareBitfields(msgObj.getPayload())){
                            // update in peer list and to current connected peer
                            state.updatePeerBitfield(remoteInfo.getPeerID(), msgObj.getPayload());
                            remoteInfo.getBitfieldObj().updateBitfield(msgObj.getPayload());
                        }

                        // if the bitfield received is the same, not interested msg is sent
                        if(state.getBitfield().compareBitfields(msgObj.getPayload())){
                            sendMessage(new Message(3, Message.Type.NOT_INTERESTED, null));
                        }
                        else{
                            sendMessage(new Message(2, Message.Type.INTERESTED, null));
                        }
                        break;
                    case REQUEST: {
                        state.log("Peer " + state.getSelfInfo().getPeerID() + " received the 'request' message from " + remoteInfo.getPeerID());
                        ByteBuffer bb = ByteBuffer.wrap(msgObj.getPayload());
                        int receivedIndex = bb.getInt();

                        ArrayList<Integer> indexArray = getPieceIndices(receivedIndex);

                        //TODO: use indexArr = [lower, upper, length] to get info about piece and send piece message containing file piece

                        // TODO: SEND PIECE BACK TO CLIENT THAT REQUESTED
                        /*byte[] piece = new byte[Config.getPieceSize() + Config.BYTES_PIECE_SIZE];

                        ByteBuffer wrapped = ByteBuffer.wrap(msgObj.getPayload()); // big-endian by default
                        int index = wrapped.getInt();
                        int offset = index * Config.getPieceSize();

                        // Make the first 4 bytes the index
                        for(int i = 0; i < Config.BYTES_PIECE_SIZE; i++)
                            piece[i] = msgObj.getPayload()[i];

                        // Populate rest of payload with byte information
                        for (int i = 0; (i < Config.getPieceSize()) && (i + offset < state.getFileContents().length); i++)
                            piece[i+Config.BYTES_PIECE_SIZE] = state.getFileContents()[i + offset];

                        sendMessage(new Message(Config.getPieceSize() + Config.BYTES_PIECE_SIZE, Message.Type.PIECE, piece));
                        state.log("Sending piece #" + index);*/
                        break;
                    }
                    case PIECE: {
                        // TODO: GET PIECE AND UPDATE BITFIELD: not done
                        state.setHasDownloadStarted(true);

                        ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(msgObj.getPayload(), 0, Config.BYTES_PIECE_SIZE));
                        int index = wrapped.getInt();


                        int offset = index * Config.getPieceSize();
                        for (int i = 0; (i < msgObj.getPayload().length - Config.BYTES_PIECE_SIZE) && (i+offset < state.getFileContents().length); i++) {
                            state.getFileContents()[i+offset] = msgObj.getPayload()[i+Config.BYTES_PIECE_SIZE];
                        }

                        // TODO update bitfield

                        //missing.remove(Integer.valueOf(index));
                    }

                    default:
                        state.log("Received unsupported message! Exiting...");
                        shouldExit = true;
                }
            }

            state.log("Transfer finished with peer ");

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

    // send handshake
    void sendHandshake(Handshake handshake) {
        try {
            //stream write the message
            out.writeObject(handshake);
            out.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    int getRandomPieceIndex(){
        boolean missing = false;
        int missingPieceIndex = -1;
        while(!missing){
            //Generate random int value from 0 to (bitfield length - 1)
            Random rand = new Random();
            missingPieceIndex = rand.nextInt(Config.getBitfieldLength());
            // if piece is not present in bitfield, return
            if(state.getSelfInfo().getBitfieldObj().getBitfield()[missingPieceIndex] == 0){
                missing = true;
            }
        }

        if(missing){
            return missingPieceIndex;
        }
        return -1;
    }

    byte[] intToByteArray(int number){
        byte[] result = new byte[4];
        result[0] = (byte) (number >> 24);
        result[1] = (byte) (number >> 16);
        result[2] = (byte) (number >> 8);
        result[3] = (byte) (number);
        return result;
    }

    // gets lower and upper indices for File contents, Array = [lower, upper, length]
    // used to get the location of the piece to send
    ArrayList<Integer> getPieceIndices(int requestIndex){
        ArrayList<Integer> indices = new ArrayList<Integer>();
        int lowerIndex = requestIndex*Config.getPieceSize();
        int upperIndex = lowerIndex + Config.getPieceSize() - 1;
        // last piece
        if(requestIndex == (Config.getBitfieldLength() -1)){
            lowerIndex = requestIndex*Config.getPieceSize();
            upperIndex = Config.getFileSize() - 1;
        }
        indices.add(lowerIndex);
        indices.add(upperIndex);
        indices.add(upperIndex - lowerIndex + 1);
        return indices;
    }

    void createBitfield(){

    }
}
