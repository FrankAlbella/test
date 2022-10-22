package BitTorrent.src.main.java.cnt.protocol;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private String peerLogDir;
    private FileOutputStream logWriter;
    private File peerLog;
    private SimpleDateFormat dateFormat;

    public Log(){
        System.out.println("Creating log ");
        //peerLogDir = logDir;
    }

    // creates file if logWriter is null
    public void createFile(String peerID1) throws Exception {
        if(logWriter == null){
            peerLogDir = "log_peer_"+ peerID1 + ".log";
            peerLog = new File(peerLogDir);
            peerLog.delete();
            logWriter = new FileOutputStream(peerLog, true);
        }
    }

    // log message into peerLog; MIGHT NOT NEED TO BE USED
    public void writeLog(String message, String peerID1) throws Exception {
        createFile(peerID1);
        Date date = new Date();
        dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String log = "[" + dateFormat.format(date) + "]: Peer " + peerID1 + " " + message + ".\n";
        logWriter.write(log.getBytes());
        System.out.println("Logging Message: " + log);
    }

    // TODO: figure how to delete upon closing, if necessary
    // tries to delete file and close logWriter
    public void deleteLog() throws Exception {
        System.out.println("Delete log: " + peerLogDir);
        peerLog.delete();
        logWriter.close();
    }

    // writes messages in log
    public void writeLog(String message, String peerID1, String peerID2) throws Exception {
        createFile(peerID1);
        Date date = new Date();
        dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String log = "[" + dateFormat.format(date) + "]: Peer " + peerID1 + " " + message + peerID2 + ".\n";
        logWriter.write(log.getBytes());
        System.out.println("Logging Message: " + log);
    }
}
