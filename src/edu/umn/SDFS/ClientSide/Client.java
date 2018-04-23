package edu.umn.SDFS.ClientSide;

/**
 * Created by mouba005 on 4/16/18.
 */

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by mouba005 on 4/16/18.
 */
public class Client implements Serializable{
    private String ip;
    private int sendFilePort;
    private int getLoadPort;
    private int registerPort;

    public Client(String ip, int sendFilePort, int getLoadPort, int registerPort) {
        this.ip = ip;
        this.sendFilePort = sendFilePort;
        this.getLoadPort = getLoadPort;
        this.registerPort = registerPort;
    }
    public Client(Client other) {
        this.ip = other.ip;
        this.sendFilePort = other.sendFilePort;
        this.getLoadPort = other.getLoadPort;
        this.registerPort = other.registerPort;
    }

    public String getIp() {
        return ip;
    }
    public int getsendFilePort() {
        return sendFilePort;
    }

    @Override
    public String toString(){
        return (ip+":"+sendFilePort);
    }

    @Override
    public boolean equals(Object other) {
    		if(other == this)
    			return true;
    		if(!(other instanceof Client))
    			return false;
        Client c = (Client) other;
        return (this.ip.equals(c.ip) && this.sendFilePort == c.sendFilePort);
    }

    @Override
    public int hashCode(){
        return Objects.hash(ip, sendFilePort);
    }

}

