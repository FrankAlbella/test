package src.main.java.cnt.protocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import src.main.java.cnt.server.Peer;

public class Config {
    private static String fileName;
    private static double fileSize;
    private static double pieceSize;

    private static int bitfieldLength;

    private static int numNeighbors;
    private static int unchokingInterval;
    private static int optimisticUnchoke;

    public static final byte BYTES_PIECE_SIZE = 4;
    public static final byte MAX_CONNECT_ATTEMPTS = 10;
    public static final int PORT_OFFSET = 35565;

    public static void loadCommon() {
        // read the common.cfg file and set variables
        System.out.println("Reading Common.cfg...");
        Properties prop = new Properties();

        try (FileInputStream fs = new FileInputStream("Common.cfg")) {
            prop.load(fs);
        } catch (FileNotFoundException ex) {
            System.err.println("Common.cfg not found！");
            System.exit(2);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            System.exit(2);
        }

        fileName = prop.getProperty("FileName");

        fileSize = Double.parseDouble(prop.getProperty("FileSize"));
        pieceSize = Double.parseDouble(prop.getProperty("PieceSize"));

        bitfieldLength = (int) Math.ceil(fileSize / (pieceSize));

        numNeighbors = Integer.parseInt(prop.getProperty("NumberOfPreferredNeighbors"));
        unchokingInterval = Integer.parseInt(prop.getProperty("UnchokingInterval"));
        optimisticUnchoke = Integer.parseInt(prop.getProperty("OptimisticUnchokingInterval"));
    }

    public static String getFileName() { return fileName; }
    public static int getFileSize() { return (int)fileSize; }
    public static int getPieceSize() { return (int)pieceSize; }
    public static int getBitfieldLength() { return bitfieldLength; }
    public static int getNumNeighbors() { return numNeighbors; }
    public static int getUnchokingInterval() { return unchokingInterval; }
    public static int getOptimisticUnchoke() { return optimisticUnchoke; }

    // toString cannot be static, so getString is used instead
    public static String getString() {
        return "Config{fileName=" + getFileName() +
                ", fileSize=" + getFileSize() +
                ", pieceSize=" + getPieceSize() +
                ", bitfieldLength=" + getBitfieldLength() +
                ", numNeighbors=" + getNumNeighbors() +
                ", unchokingInterval=" + getUnchokingInterval() +
                ", optimisticInterval=" + getOptimisticUnchoke() +
                "}";

    }
}
