package src.main.java.cnt.protocol;

public class Message {
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

    Message(int length, Type type, byte[] payload) {
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
}
