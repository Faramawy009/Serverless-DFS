package edu.umn.SDFS.ClientSide;

import java.io.Serializable;

/**
 * Created by elfar009 on 4/17/18.
 */
//A wrapper class to send both the file data and the checksum in the same TCP request.
public class DownloadObject implements Serializable {
    public String checkSum;
    public byte [] file;

    public DownloadObject(String checkSum, byte[] file) {
        this.checkSum = checkSum;
        this.file = file;
    }
}
