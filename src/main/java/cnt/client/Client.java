package src.main.java.cnt.client;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

import src.main.java.cnt.protocol.*;
import src.main.java.cnt.server.Peer;
import src.main.java.cnt.server.Server;

import static src.main.java.cnt.protocol.Log.log;

public class Client {
    // From common.cfg
    Peer selfInfo;
    boolean hasDownloadStarted;
    ClientState state;

    List<Integer> missing = new ArrayList<>();

    public Client(String id) {
        log(Config.getString(), id);

        state = new ClientState();
        state.loadPeerInfo();

        //search peer list to find self by id
        for(int i = 0; i < state.getPeers().size(); i++) {
            if (state.getPeers().get(i).getPeerID().equals(id)) {
                this.selfInfo = state.getPeers().get(i);
                state.getPeers().remove(i); //remove self from peer list
                break;
            }
        }

        state.setSelfInfo(this.selfInfo);

        //if the file has already been downloaded (all bits in the bitfield are non-zero)
        if(this.selfInfo.getBitfield()[0] != 0) {
            this.hasDownloadStarted = true;
            state.loadFile();
        }
    }

    void run() {
        // Attempt to connect to other peers
        for (Peer peer : state.getPeers()) {
            for(int i = 0; i < Config.MAX_CONNECT_ATTEMPTS; i++) {
                try {
                    // TODO change "localhost" to real hostname
                    int peerPort = Config.PORT_OFFSET + Integer.parseInt(peer.getPeerID());
                    Socket socket = new Socket("localhost", peerPort);
                    ObjectOutputStream peerOut = new ObjectOutputStream(socket.getOutputStream()); //stream write to the socket
                    peerOut.flush();
                    ObjectInputStream peerIn = new ObjectInputStream(socket.getInputStream()); //stream read from the socket

                    peer.setSocket(socket, peerIn, peerOut);
                    new PeerHandler(peer, state).start();
                    Log.log(selfInfo.getPeerID() + " successfully connected to peer " + peer.getPeerID(),
                            selfInfo.getPeerID());
                    break;
                } catch (IOException e) {
                    Log.log(selfInfo.getPeerID() + " failed to connect to peer " + peer.getPeerID(),
                            selfInfo.getPeerID());
                }
            }
        }

        // Listen for peers
        int port = Config.PORT_OFFSET + Integer.parseInt(selfInfo.getPeerID());
        Log.log("Listening for peers on port " + port, selfInfo.getPeerID());
        while(true) {
            try (ServerSocket listener = new ServerSocket(port)) {
                new PeerHandler(listener.accept(), state).start();
                Log.log(selfInfo.getPeerID() + " received new connection attempt ",
                        selfInfo.getPeerID());
            } catch (IOException e) {
                Log.log("Connection with peer terminated", selfInfo.getPeerID());
                throw new RuntimeException(e);
            }
        }
    }

    //main method
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Must be supplied ID as argument.");
            System.exit(1);
        } else if (args[0].length() != 4) {
            System.err.println("ID must be 4 characters long.");
            System.exit(1);
        }

        Config.loadCommon();

        System.out.println("Running the client: " + args[0]);
        Client client = new Client(args[0]);
        client.run();

    }

}
