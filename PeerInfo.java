public class PeerInfo implements Cloneable{

    private int peerID;
    private String hostName;
    private int portNumber;
    private boolean hasFile;

    public PeerInfo (int ID, String hostName, int portNumber, boolean hasFile){
        this.peerID = ID;
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.hasFile = hasFile;

    }

    public int getPeerID(){
        return peerID;
    }

    public void setPeerID(int peerID){
        this.peerID = peerID;
    }

    public String getHostName(){
        return hostName;
    }

    public void setHostName(String hostName){
        this.hostName = hostName;
    }

    public int getPortNumber(){
        return portNumber;
    }

    public void setPortNumber(int portNumber){
        this.portNumber = portNumber;
    }

    public boolean getHasFile(){
        return hasFile;
    }

    public void setHasFile(boolean hasFile){
        this.hasFile = hasFile;
    }
}
