package src.main.java.cnt.server;

import src.main.java.cnt.protocol.Config;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Peer {

    private String peerID;
    private String hostName;
    private int hasFile;
    private int portNumber;

    byte[] bitfield;

    Socket socket;
    ObjectOutputStream out; //stream write to the socket
    ObjectInputStream in; //stream read from the socket


    public String getPeerID() { return peerID; }
    public String getHostName() { return hostName; }
    public int getHasFile() { return hasFile; }
    public int getPortNumber() { return portNumber; }
    public byte[] getBitfield() { return bitfield; }
    public Socket getSocket() { return socket; }
    public ObjectOutputStream getOutStream() { return out; }
    public ObjectInputStream getInStream() { return in; }

    public void peerData(String peerID, String hostName, int portNumber, int hasFile) {
        this.peerID = peerID;
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.bitfield = new byte[Config.getBitfieldLength()];
        this.hasFile = hasFile;

        if(hasFile == 1)
            for (int i = 0; i < Config.getBitfieldLength(); i++)
                bitfield[i] = 127;
    }

    public void setSocket(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    @Override
    public String toString() {
        return "Peer{peerID=" + getPeerID() +
                ", hostName=" + getHostName() +
                ", portNumber=" + getPortNumber() +
                "}";
    }
}
