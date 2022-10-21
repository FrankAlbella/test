package src.main.java.cnt.client;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import src.main.java.cnt.protocol.Handshake;
import src.main.java.cnt.protocol.Message;

public class Client {
    Socket requestSocket;           //socket connect to the server
    ObjectOutputStream out;         //stream write to the socket
    ObjectInputStream in;          //stream read from the socket
    String message;                //message send to the server
    String MESSAGE;                //capitalized message read from the server
    String id;

    Handshake handshakeMessage = new Handshake();

    final String HANDSHAKE_HEADER = "P2PFILESHARINGPROJ";
    private ArrayList<String> clientList = new ArrayList<String>();

    public Client(String id) {
        this.id = id;
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
            message = handshakeMessage.getHandshake();
            sendMessage(message);

            // Get handshake from server
            MESSAGE = (String) in.readObject();
            //show the message to the user
            if(MESSAGE.equals(message)) {
                System.out.println("HANDSHAKES MATCH: " + MESSAGE);
            }
            else {
                System.out.println("HANDSHAKES DO NOT MATCH:");
                System.out.println("\tExpected: " + message);
                System.out.println("\tReceived: " + MESSAGE);
                throw new Exception("Handshake received from server does not match.");
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

    //send a message to the output stream
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

    //main method
    public static void main(String args[]) {
        if(args.length == 0) {
            System.out.println("Must be supplied ID as argument.");
        }
        else if (args[0].length() != 4) {
            System.out.println("ID must be 4 characters long.");
        }
        else {
            System.out.println("Running the client: " + args[0]);
            Client client = new Client(args[0]);
            client.run();
        }
    }

}
