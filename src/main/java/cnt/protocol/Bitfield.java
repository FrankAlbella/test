package src.main.java.cnt.protocol;
import src.main.java.cnt.protocol.Config;
import java.io.*;
public class Bitfield {
    private byte[] bitfield;
    private String bitfieldString;

    Bitfield(String peerID, int hasFile){
        System.out.println("bitfield constructor");
        createBitfield(peerID, hasFile);
    }
    public void createBitfield(String peerID, int hasFile){
        System.out.println("creating the bitfield");
        bitfield = new byte[Config.getBitfieldLength()];
        // create the bitfield
        for(int i = 0; i < Config.getBitfieldLength(); i++){
            if(hasFile == 0) {
                bitfield[i] = 0;
            }
            else{
                bitfield[i] = 1;
            }
        }
    }

    public void updateBitfield(int index){
        bitfield[index] = 1;
    }

    public byte[] getBitfield(){return bitfield;}

    // will be used to figure out if it is interested or not
    public Boolean compareBitfields(byte[] peerBitfield){
        for(int i = 0; i < Config.getBitfieldLength(); i++){
            if(bitfield[i] != peerBitfield[i]) {
                return false;
            }
        }
        return true;
    }
}
