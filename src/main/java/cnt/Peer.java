package src.main.java.cnt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Peer {

    public int peerID;
    public String hostName;
    public int portNumber;
    public int includeFile;

    public boolean hasFile;

    public int numOfPreferredNeighbors;
    public int unchokingInterval;
    public int optimisticUnchokingInterval;
    public String fileName;
    public long fileSize;
    public int pieceSize;
    public int countPieces;


    public Peer () throws FileNotFoundException{
        this.numOfPreferredNeighbors = 0;
        this.unchokingInterval = 0;
        this.optimisticUnchokingInterval = 0;
        this.fileName = "";
        this.fileSize = 0;
        this.pieceSize = 0;
        this.countPieces = 0;
    }


    public boolean peerData(int peerID, String hostName, int portNumber, int includeFile){
        this.peerID = peerID;
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.includeFile = includeFile;

        if (includeFile == 1){
            //Complete Field, Set all the bits of bitfield to 1
            this.hasFile = true;
        } else{
            //Field is 0, sets all bits of bitfield to 0
            this.hasFile = false;
        }

        return true;
    }
}
