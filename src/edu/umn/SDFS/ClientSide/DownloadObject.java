package edu.umn.SDFS.ClientSide;

/**
 * Created by mouba005 on 4/17/18.
 */
public class DownloadObject {
    public long checkSum;
    public byte [] file;

    public DownloadObject(long checkSum, byte[] file) {
        this.checkSum = checkSum;
        this.file = file;
    }
}
