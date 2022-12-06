package BitTorrent.src.main.java.cnt.protocol;

public class Handshake {
    final String HANDSHAKE_HEADER = "P2PFILESHARINGPROJ";
    String handshakeMessage;

    public String getHandshake(){
        return handshakeMessage;
    }

    // creates the handshake message for the client
    public void createHandshakeMessage(String id){
        // Create the handshake message with a char array
        char[] handshake = new char[HANDSHAKE_HEADER.length() + 10 + id.length()];

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
        handshakeMessage = new String(handshake);
    }

    // validates the handshake from the server
    public void validateHandshake(String expectedMessage, String receivedMessage) throws Exception {
        if(expectedMessage.equals(receivedMessage)) {
            System.out.println("HANDSHAKES MATCH: " + expectedMessage);
        }
        else {
            System.out.println("HANDSHAKES DO NOT MATCH:");
            System.out.println("\tExpected: " + expectedMessage);
            System.out.println("\tReceived: " + receivedMessage);
            throw new Exception("Handshake received from server does not match.");
        }
    }
}
