package src.main.java.cnt.protocol;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {
    double fileSize;
    double pieceSize;

    int bitfieldLength;

    int numNeighbors;
    int unchokingInterval;
    int optimisticUnchoke;

    public void loadCommon() {
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

        fileSize = Double.parseDouble(prop.getProperty("FileSize"));
        pieceSize = Double.parseDouble(prop.getProperty("PieceSize"));

        bitfieldLength = (int)Math.ceil(fileSize / (pieceSize));

        numNeighbors = Integer.parseInt(prop.getProperty("NumberOfPreferredNeighbors"));
        unchokingInterval = Integer.parseInt(prop.getProperty("UnchokingInterval"));
        optimisticUnchoke = Integer.parseInt(prop.getProperty("OptimisticUnchokingInterval"));
    }

    public double getFileSize() { return  fileSize; }
    public double getPieceSize() { return  pieceSize; }
    public int getBitfieldLength() { return  bitfieldLength; }
    public int getNumNeighbors() { return  numNeighbors; }
    public int getUnchokingInterval() { return  unchokingInterval; }
    public int getOptimisticUnchoke() { return  optimisticUnchoke; }
}
