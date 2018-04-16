package ClientSide;

/**
 * Created by mouba005 on 4/16/18.
 */
public class Client implements Comparable <Client>{
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
    public int compareTo(Client other) {
        if (this.ip.equals(other.ip) && this.port == other.port)
            return 0;
        else return this.port - other.port;
    }
}
