package edu.umn.SDFS.ServerSide;

/**
 * Created by mouba005 on 4/16/18.
 */

import edu.umn.SDFS.ClientSide.Client;

/**
 * Created by mouba005 on 4/16/18.
 */
public class ClientRegisterHandler implements Runnable{
    /*
     * registerMsg is of the form ip;socket;f1,f2,f3....
     */
    private String registerMsg;
    private Client client;
    private String[] files;
    private String response;

    public ClientRegisterHandler(String registerMsg) {
        this.registerMsg = registerMsg;
    }

    @Override
    public void run() {
        String[] msgElements = registerMsg.split(";");
        if (msgElements.length != 3) {
            response = new String("invalid registration message");
            return;
        }
        client.setIp(msgElements[0]);
        client.setPort(Integer.parseInt(msgElements[1]));
        files = msgElements[2].split(",");
        response = "registered";
    }
}

