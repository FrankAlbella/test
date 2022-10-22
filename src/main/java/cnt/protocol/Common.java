package src.main.java.cnt.protocol;

public class Common {
    private int preferredNeighbors;
    private int unchokingInterval;
    private int optimisticUnchokingInterval;
    private String fileName;
    private Double fileSize;
    private Double pieceSize;

    private int bitFieldLength;

    public void setPreferredNeighbors(int neighbors){ preferredNeighbors = neighbors; }
    public int getPreferredNeighbors(){ return preferredNeighbors; }

    public void setUnchokingInterval(int unInterval){ unchokingInterval = unInterval; }
    public int getUnchokingInterval(){ return unchokingInterval; }

    public void setOptimisticUnchokingInterval(int optimistic){ optimisticUnchokingInterval = optimistic; }
    public int getOptimisticUnchokingInterval(){ return optimisticUnchokingInterval; }

    public void setFileName(String name){ fileName = name; }
    public String getFileName(){ return fileName; }

    public void setFileSize(Double size){ fileSize = size; }
    public Double getFileSize(){ return fileSize; }

    public void setPieceSize(Double size){ pieceSize = size; }
    public Double getPieceSize(){ return pieceSize; }

    public void setBitFieldLength(int length){ bitFieldLength = length; }
    public int getBitFieldLength(){ return bitFieldLength; }
}
