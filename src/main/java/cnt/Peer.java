package src.main.java.cnt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    protected void readConf() throws FileNotFoundException {

        File conf = new File("Common.cfg");
        List<String> variables = new ArrayList<>();
        Scanner scn = new Scanner(conf);

        try {
            while (scn.hasNextLine()) {
                String text = scn.nextLine();
                String[] info = text.split(" ");
                variables.add(info[1]);
            }

            numOfPreferredNeighbors = Integer.parseInt(variables.get(0));
            unchokingInterval = Integer.parseInt(variables.get(1));
            optimisticUnchokingInterval = Integer.parseInt(variables.get(2));
            fileSize = Integer.parseInt(variables.get(3));
            pieceSize = Integer.parseInt(variables.get(4));
        }

        catch (Exception e){
            e.printStackTrace();
        }scn.close();

    }



//    public Peer (int numOfPreferredNeighbors, int unchokingInterval, int optimisticUnchokingInterval, String fileName, int fileSize, int pieceSize) throws FileNotFoundException {
//        this.numOfPreferredNeighbors = numOfPreferredNeighbors;
//        this.unchokingInterval = unchokingInterval;
//        this.optimisticUnchokingInterval = optimisticUnchokingInterval;
//        this.fileName = fileName;
//        this.fileSize = fileSize;
//        this.pieceSize = pieceSize;
//        readConf();
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
