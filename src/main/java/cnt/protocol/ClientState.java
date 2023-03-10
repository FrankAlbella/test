package src.main.java.cnt.protocol;

import src.main.java.cnt.server.Peer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ClientState {
    private final byte[] fileContents;
    Peer selfInfo;
    private List<Peer> peers;

    private boolean hasDownloadStarted = false;

    private final Log log;
    public ClientState() {
        fileContents = new byte[Config.getFileSize()];
        log = new Log();
    }

    public byte[] getFileContents() {
        synchronized (fileContents) {
            return fileContents;
        }
    }

    // Load full file into memory to share
    // Should only be used at the start if the client is set to have the full file
    public void loadFile() {
        File dir = new File(selfInfo.getPeerID());
        String filePath = selfInfo.getPeerID() + "/" + Objects.requireNonNull(dir.list())[0];
        try (FileInputStream fs = new FileInputStream(filePath)){
            //noinspection ResultOfMethodCallIgnored
            fs.read(getFileContents());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Saves a file to the local directory corresponding to the given peerID
    public boolean saveFile() {
        String path = selfInfo.getPeerID() + "/" + Config.getFileName();
        try (FileOutputStream outputStream = new FileOutputStream(path)) {
            this.log("File saved to " + path);
            outputStream.write(fileContents);
        } catch (IOException e) {
            this.log("Failed to save file " + path + ": " + e.getMessage());
            return false;
        }

        return true;
    }

    // Reads and parses the PeerInfo.cfg file, saving it to the local peers accessible via getPeers()
    public void loadPeerInfo() {
        System.out.println("Reading PeerInfo.cfg...");

        File configFile = new File("PeerInfo.cfg");

        try (Scanner scanner = new Scanner(configFile)){
            peers = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String text = scanner.nextLine();
                String[] peerVars = text.split("\\s+");

                Peer peer = new Peer();

                peer.peerData(peerVars[0], peerVars[1], Integer.parseInt(peerVars[2]), Integer.parseInt(peerVars[3]));
                peers.add(peer);
            }

        }  catch (FileNotFoundException ex) {
            System.err.println("PeerInfo.cfg not found???");
            System.exit(2);
        }
    }

    // Calls log with the peerID filled out
    public void log(String msg) {
        log.log(msg, getSelfInfo().getPeerID());
    }

    public Peer getSelfInfo() { return selfInfo; }
    public void setSelfInfo(Peer selfInfo) {
        this.selfInfo = selfInfo;
    }

    public List<Peer> getPeers() { return peers; }

    public boolean hasDownloadStarted() { return hasDownloadStarted; }

    public void setHasDownloadStarted(boolean hasDownloadStarted) { this.hasDownloadStarted = hasDownloadStarted; }
    public Bitfield getBitfield(){return selfInfo.getBitfieldObj();}
    // update the bitfield for the peer
    public void updatePeerBitfield(String peerID, byte[] peerBitfield){
        for(Peer peer : peers){
            if(peer.getPeerID().compareTo(peerID) == 0){
                peer.updateBitfieldObj(peerBitfield);
            }
        }
    }

    public byte[] getPeerBitfield(String peerID){
        System.out.println(selfInfo.getPeerID() + ": getting peers " + peerID + " bitfield");
        for(Peer peer : peers){
            if(peer.getPeerID().compareTo(peerID) == 0){
                return peer.getBitfieldObj().getBitfield();
            }
        }
        return null;
    }
}