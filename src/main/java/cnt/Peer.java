package src.main.java.cnt;

public class Peer {

    public int peerID;
    public String hostName;
    public int portNumber;

    public boolean hasFile;

    public int includeFile;
    public int numOfPreferredNeighbors;
    public int unchokingInterval;
    public int optimisticUnchokingInterval;
    public String fileName;
    public long fileSize;
    public int pieceSize;

//    public Peer (int numOfPreferredNeighbors, int unchokingInterval, int optimisticUnchokingInterval, String fileName, int fileSize, int pieceSize) {
//        this.numOfPreferredNeighbors = numOfPreferredNeighbors;
//        this.unchokingInterval = unchokingInterval;
//        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
//        this.fileName = fileName;
//        this.fileSize = fileSize;
//        this.pieceSize = pieceSize;
//    }


    public boolean peerData(int peerID, String hostName, int portNumber, int hasFile){
        this.peerID = peerID;
        this.hostName = hostName;
        this.portNumber = portNumber;

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
