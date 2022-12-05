package src.main.java.cnt.client;

import src.main.java.cnt.protocol.*;
import src.main.java.cnt.server.Peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import static src.main.java.cnt.protocol.Log.log;

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

            //peer.setSocket(socket, peerIn, peerOut);
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
            log(state.getSelfInfo().getPeerID() + " sent handshake", state.getSelfInfo().getPeerID());

            // Get handshake from server (or peer), no need to validate
            String MESSAGE = (String) in.readObject();
            //handshakeMessage.validateHandshake(MESSAGE, message);
            log(MESSAGE, state.getSelfInfo().getPeerID());

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

            // Send bitfield message if it has any
            if (state.hasDownloadStarted()) {
                sendMessage(new Message(remoteInfo.getBitfield().length, Message.Type.BITFIELD, remoteInfo.getBitfield()));
                log(in.readObject().toString(), state.getSelfInfo().getPeerID());
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
                        byte[] piece = new byte[Config.getPieceSize() + Config.BYTES_PIECE_SIZE];

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
                        log("Sending piece #" + index, state.getSelfInfo().getPeerID());
                        break;
                    }
                    case PIECE: {
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
                        log("Received unsupported message! Exiting...", state.getSelfInfo().getPeerID());
                        shouldExit = true;
                }
            }

            log("Transfer finished with peer " + remoteInfo.getPeerID(), state.getSelfInfo().getPeerID());

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
        System.out.println("Sending a message!");
        try {
            //stream write the message
            out.writeObject(msg);
            out.flush();
            log("Successful sent str message to peer ", state.getSelfInfo().getPeerID());
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
            log("Successful sent message object to peer ", state.getSelfInfo().getPeerID());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
