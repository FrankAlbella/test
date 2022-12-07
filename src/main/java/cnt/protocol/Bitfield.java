package src.main.java.cnt.protocol;
import src.main.java.cnt.protocol.Config;
import java.io.*;
public class Bitfield {
    private byte[] bitfield;

    public Bitfield(int hasFile){
        createBitfield(hasFile);
    }
    public void createBitfield(int hasFile){
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
    public void updateBitfield(byte[] updatedBitfield){ bitfield = updatedBitfield; }

    public void updateBitfieldPiece(int index){ bitfield[index] = 1; }


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
