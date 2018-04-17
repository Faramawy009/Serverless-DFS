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

    public ClientRegisterHandler(String registerMsg) {
        this.registerMsg = registerMsg;
    }

    @Override
    public void run() {
        String[] msgElements = registerMsg.split(";");
        if (msgElements.length != 3) {
            System.out.println("invalid registration msg!");
            return;
        }
        Client client = new Client(msgElements[0], Integer.parseInt(msgElements[1]));
        String[] files  = msgElements[2].split(",");
        for (int i=0; i < files.length; i++){
            ServerDB.insert(files[i], client);
        }
    }
}

