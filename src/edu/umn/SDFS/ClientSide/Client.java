package edu.umn.SDFS.ClientSide;

/**
 * Created by mouba005 on 4/16/18.
 */

import java.util.Objects;

/**
 * Created by mouba005 on 4/16/18.
 */
public class Client{
    private String ip;
    private int port;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    public Client(Client other) {
        this.ip = other.ip;
        this.port = other.port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object other) {
        Client c = (Client) other;
        return (this.ip.equals(c.ip) && this.port == c.port);
    }

    @Override
    public int hashCode(){
        return Objects.hash(ip, port);
    }

}

