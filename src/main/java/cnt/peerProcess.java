package src.main.java.cnt;

import com.sun.nio.sctp.PeerAddressChangeNotification;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class peerProcess {


    public static void main (String args[]) throws FileNotFoundException {

        File configFile = new File("PeerInfo.cfg");

        Hashtable <Integer, Peer> peers = new Hashtable<Integer, Peer>();

        Scanner scanner = new Scanner(configFile);

        while (scanner.hasNextLine()){
            String text = scanner.nextLine();
            String [] peerVars = text.split(" ");

            Peer peer = new Peer();

            peer.peerData(Integer.parseInt(peerVars[0]), peerVars[1], Integer.parseInt(peerVars[2]),Integer.parseInt(peerVars[3]));
            peers.put(peer.peerID, peer);

        }

        scanner.close();

    }


}
