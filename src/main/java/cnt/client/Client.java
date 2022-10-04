package src.main.java.cnt.client;

import java.net.*;
import java.io.*;

public class Client {
    Socket requestSocket;           //socket connect to the server
    ObjectOutputStream out;         //stream write to the socket
    ObjectInputStream in;          //stream read from the socket
    String message;                //message send to the server
    String MESSAGE;                //capitalized message read from the server
    String id;

    final String HANDSHAKE_HEADER = "P2PFILESHARINGPROJ";

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

            // Create the handshake message with a char array
            char handshake[] = new char[HANDSHAKE_HEADER.length() + 10 + id.length()];

            int offset = 0; // keep track of offset in further loops
            for (int i = 0; i < HANDSHAKE_HEADER.length(); i++) {
                handshake[i] = HANDSHAKE_HEADER.charAt(i);
            }
            offset += HANDSHAKE_HEADER.length();

            // Pad handshake message with 10 0 bytes
            for(int i = 0; i < 10; i++) {
                handshake[offset+i] = 0;
            }
            offset += 10;

            // Put ID at end of handshake
            for(int i = 0; i < id.length(); i++) {
                handshake[offset+i] = id.charAt(i);
            }

            message = new String(handshake);
            // Send server handshake
            sendMessage(message);
            // Ger handshake from server
            MESSAGE = (String) in.readObject();
            //show the message to the user
            if(MESSAGE.equals(message)) {
                System.out.println("HANDSHAKES MATCH: " + MESSAGE);
            }
            else {
                System.out.println("HANDSHAKES DO NOT MATCH:");
                System.out.println("\tExpected: " + message);
                System.out.println("\tReceived: " + MESSAGE);
            }
        } catch (ConnectException e) {
            System.err.println("Connection refused. You need to initiate a server first.");
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found");
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
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

    //main method
    public static void main(String args[]) {
        if(args.length == 0) {
            System.out.println("Must be supplied ID as argument.");
        }
        else if (args[0].length() != 4) {
            System.out.println("ID must be 4 characters long.");
        }
        else {
            Client client = new Client(args[0]);
            client.run();
        }
    }

}
