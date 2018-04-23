package edu.umn.SDFS.ClientSide;

import java.io.Serializable;

/**
 * Created by mouba005 on 4/17/18.
 */
public class DownloadObject implements Serializable {
    public String checkSum;
    public byte [] file;

    public DownloadObject(String checkSum, byte[] file) {
        this.checkSum = checkSum;
        this.file = file;
    }
}
