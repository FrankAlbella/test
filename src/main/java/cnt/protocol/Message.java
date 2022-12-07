package src.main.java.cnt.protocol;

import java.util.Arrays;

public class Message implements java.io.Serializable{
    public enum Type {
        CHOKE,
        UNCHOKE,
        INTERESTED,
        NOT_INTERESTED,
        HAVE,
        BITFIELD,
        REQUEST,
        PIECE
    }
    int length;
    Type type;
    byte[] payload;

    public Message(int length, Type type, byte[] payload) {
        this.length = length;
        this.type = type;
        this.payload = payload;
    }

    public int getLength() {
        return length;
    }

    public Type getType() {
        return type;
    }

    public int getTypeValue() {
        return type.ordinal();
    }

    public byte[] getPayload() {
        return payload;
    }

    public void print() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "Message{" +
                "length=" + getLength() +
                ", type=" + getTypeValue() +
                ", payload=" + Arrays.toString(getPayload()) +
                "}";
    }
}
