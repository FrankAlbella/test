**P2P File Sharing Software**

This project emulates a software similar to BitTorrent for a Peer to Peer file distribution using Java. 

**Set Up**
Intellij -> Edit Configuration -> Application: <br/>
        Server - Build and Run (src.main.java.cnt.server.server) <br/>
        Client 1001 - Build and Run (src.main.java.cnt.client.Client) <br/>
        Client 1002 - Build and Run (src.main.java.cnt.client.Client) <br/>

**Common.cfg**
Configuration files that reads and sets up corresponding peers behaviors. 
Parameters: <br/>
        NumberOfPreferredNeighbors <br/>
        UnchokingInterval <br/>
        OptimisticUnchokingInterval <br/>
        FileName <br/>
        FileSize <br/>
        PieceSize <br/>

**PeerInfo.cfg**
Configuration file that specifies the peer information. 
Parameters:
        peerID
        hostName
        listeningPort
        hasFile or not

**Logs** <br/>
TCP Connection: Whenever a peer makes a connection to other peers, created log for connection established. <br/>
change of preferred neighbors: Whenever a peer changes its preferred neighbors, generates a log neighbor list. <br/>
change of optimistically unchoked neighbor: Whenever a peer changes its optimistically unchoked neighbors and logs the peer and optimistically neighbor IDs. <br/>
unchoking: Whenever a peer is unchoked by a neighbor, create log and show which peers are unchoked. <br/>
choking: Whenever a peer is choked by a nieghbor, create log and represent which peers are choked. <br/>
receiving 'have' message: Whenever a peer receives a 'have' message, generate log with the 'have' message from peerID with piece index. <br/>
receiving 'interest' message: Whenever a peer receives an 'interested' message, generate log 'interested' message from peerID1 to peerID2. <br/> 
receiving 'not interested' message: Whenever a peer receives a 'not interested' message, generate log 'not interested' from peer1 to peer2. <br/>
downloading a piece: Whenever a peer finishes downloading a piece, generate log with piece index and number of pieces. <br/>
completion of download: Whenever a peer downloads the complete file, log the id with downloaded message. <br/>

**Current Status:**

**Group Members** <br/>
Gabriel Rodriguez Torres: Protocols, Server, Client <br/>
Tito Ruiz: Protocols, Server, Client <br/>
Frank Albella: Protocols, Server, Client <br/>

**Video Link**
URL: 
