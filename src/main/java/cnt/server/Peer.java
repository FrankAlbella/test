package src.main.java.cnt.server;

import src.main.java.cnt.protocol.Config;

public class Peer {

    private String peerID;
    private String hostName;
    private int portNumber;

    byte[] bitfield;

    public String getPeerID() { return peerID; }
    public String getHostName() { return hostName; }
    public int getPortNumber() { return portNumber; }
    public byte[] getBitfield() { return bitfield; }

    public void peerData(String peerID, String hostName, int portNumber, int hasFile) {
        this.peerID = peerID;
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.bitfield = new byte[Config.getBitfieldLength()];

        if(hasFile == 1)
            for (int i = 0; i < Config.getBitfieldLength(); i++)
                bitfield[i] = 127;
    }

    @Override
    public String toString() {
        return "Peer{peerID=" + getPeerID() +
                ", hostName=" + getHostName() +
                ", portNumber=" + getPortNumber() +
                "}";
    }
}
