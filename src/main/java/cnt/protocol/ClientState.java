package src.main.java.cnt.protocol;

import src.main.java.cnt.server.Peer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ClientState {
    private final byte[] fileContents;
    Peer selfInfo;
    private List<Peer> peers;

    public ClientState(Peer peer) {
        fileContents = new byte[Config.getFileSize()];
        selfInfo = peer;
    }

    public byte[] getFileContents() {
        synchronized (fileContents) {
            return fileContents;
        }
    }

    //load full file into memory to share
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
            System.err.println("PeerInfo.cfg not found！");
            System.exit(2);
        }
    }

    public Peer getSelfInfo() { return selfInfo; }

    public List<Peer> getPeers() { return peers; }
}
