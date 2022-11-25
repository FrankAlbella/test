package src.main.java.cnt.server;

import src.main.java.cnt.protocol.Message;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Server {

    private static final int sPort = 8000;   //The server will be listening on this port number

    private static ArrayList<Handler> handlerPeers;
    private static ArrayList<String> peerIDList;

    public static void main(String[] args) throws Exception {
        System.out.println("The server is running.");
        ServerSocket listener = new ServerSocket(sPort);
        int clientNum = 1;
        try {
            while (true) {
                //handlerPeers.add(new Handler(listener.accept(), clientNum));
                new Handler(listener.accept(), clientNum).start();
                //handlerPeers.get(clientNum - 1).start();
                System.out.println("src.main.java.cnt.client.Client " + clientNum + " is connected!");
                clientNum++;
            }
        } finally {
            listener.close();
        }

    }

    /**
     * A handler thread class.  Handlers are spawned from the listening
     * loop and are responsible for dealing with a single client's requests.
     */
    private static class Handler extends Thread {
        private Socket connection;
        private ObjectInputStream in;    //stream read from the socket
        private ObjectOutputStream out;    //stream write to the socket
        private int no;        //The index number of the client
        private String handshake = "P2PFILESHARINGPROJ";
        private ArrayList<String> clientList = new ArrayList<String>();

        public Handler(Socket connection, int no) {
            this.connection = connection;
            this.no = no;
        }

        public void run() {
            try {
                //initialize Input and Output streams
                out = new ObjectOutputStream(connection.getOutputStream());
                out.flush();
                in = new ObjectInputStream(connection.getInputStream());
                try {
                    //receive the message sent from the client, check if handshake
                    serverReceiveHandshake((String) in.readObject()); // receive handshake once
                    while (true) {
                        //receieve message from client
                        //then send the same message back (debugging)
                        Message message = (Message) in.readObject();
                        message.print();
                        sendMessage(message);
                        // TODO: send peers to server

                        // create other if-else statements depending on what client sent

                    }
                } catch (ClassNotFoundException classnot) {
                    System.err.println("Data received in unknown format");
                }
            } catch (IOException ioException) {
                System.out.println("Disconnect with src.main.java.cnt.client.Client " + no);
            } finally {
                //Close connections
                try {
                    in.close();
                    out.close();
                    connection.close();
                } catch (IOException ioException) {
                    System.out.println("Disconnect with src.main.java.cnt.client.Client " + no);
                }
            }
        }

        //send a message to the output stream
        public void sendMessage(String msg) {
            try {
                out.writeObject(msg);
                out.flush();
                System.out.println("Send message: " + msg + " to src.main.java.cnt.client.Client " + no);
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
                System.out.println("Send message: " + msg + " to src.main.java.cnt.client.Client " + no);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        // do handshake, removes or adds peers to network, tracks them
        public void serverReceiveHandshake(String message){
            String isHandshake = message.substring(0,18);
            // if client sent handshake
            if(isHandshake.compareTo(handshake) == 0){
                // get the peerID number from client and add to arrayList
                String peerIDNum = message.substring(28,32);
                int index = -1;
                //check if in clientList, track index
                for(int i = 0; i < clientList.size(); i++){
                    // if already in the clientList
                    if(peerIDNum.compareTo(clientList.get(i)) == 0){
                        index = i;
                    }
                }
                // if already connected, remove
                if(index != -1){
                    clientList.remove(index);
                }
                else{
                    clientList.add(peerIDNum);
                }
                sendMessage(message);
            }
            else{
                String error = "Error: Can't handle request";
                sendMessage(error);
            }
        }

    }
}
