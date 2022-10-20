package src.main.java.cnt.protocol;

import java.net.*;
import java.io.*;

public class Handshake {
    final String HANDSHAKE_HEADER = "P2PFILESHARINGPROJ";
    String handshakeMesage;
    // create the message
    public Handshake(){
        System.out.println("Creating Handshake");
    }

    public String getHandshake(){
        return handshakeMesage;
    }

    // creates the handshake message for the client
    public void createHandshakeMessage(String id){
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
        handshakeMesage = new String(handshake);
    }

}
