package src.main.java.cnt.server;

import src.main.java.cnt.protocol.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Peer {

    private String peerID;
    private String hostName;
    private int portNumber;

    private boolean hasFile;

    private int includeFile;
    private int numOfPreferredNeighbors;
    private int unchokingInterval;
    private int optimisticUnchokingInterval;
    private String fileName;
    private long fileSize;
    private int pieceSize;
    byte[] bitfield;

    public String getPeerID() { return peerID; }
    public String getHostName() { return hostName; }
    public int getPortNumber() { return portNumber; }
    public byte[] getBitfield() { return bitfield; }

    public int getNumOfPreferredNeighbors(){
        return numOfPreferredNeighbors;
    }

    public void setNumOfPreferredNeighbors(int numOfPreferredNeighbors){
        this.numOfPreferredNeighbors = numOfPreferredNeighbors;
    }

    public int getUnchokingInterval(){
        return unchokingInterval;
    }

    public void setUnchokingInterval(int unchokingInterval){
        this.unchokingInterval = unchokingInterval;
    }

    public int getOptimisticUnchokingInterval(){
        return optimisticUnchokingInterval;
    }

    public void setOptimisticUnchokingInterval(int optimisticUnchokingInterval){
        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
    }

    public String getFileName(){
        return fileName;
    }

    public void setFileName (String fileName){
        this.fileName = fileName;
    }

    public long getFileSize(){
        return fileSize;
    }

    public void setFileSize(int fileSize){
        this.fileSize = fileSize;
    }

    public int getPieceSize(){
        return pieceSize;
    }

    public void setPieceSize(int pieceSize){
        this.pieceSize = pieceSize;
    }

    public boolean peerData(String peerID, String hostName, int portNumber, int hasFile) throws FileNotFoundException {
        this.peerID = peerID;
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.bitfield = new byte[Config.getBitfieldLength()];

        if(hasFile == 1)
            for (int i = 0; i < Config.getBitfieldLength(); i++)
                bitfield[i] = 127;

        return true;
    }

    @Override
    public String toString() {
        return "Peer{peerID=" + getPeerID() +
                ", hostName=" + getHostName() +
                ", portNumber=" + getPortNumber() +
                "}";
    }
}
