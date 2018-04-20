package edu.umn.SDFS.ServerSide;

/**
 * Created by mouba005 on 4/16/18.
 */

import edu.umn.SDFS.ClientSide.Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mouba005 on 4/16/18.
 */
public class ClientRegisterHandler implements Runnable{
    /*
     * registerMsg is of the form ip;socket;f1,f2,f3....
     */
    private Socket clientSocket;
    public ClientRegisterHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        DataInputStream in = null;
        try {
            in = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String registerMsg = null;
        try {
            registerMsg = in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] msgElements = registerMsg.split(";");
        if (msgElements.length != 3) {
            System.out.println("invalid registration msg!");
            return;
        }
        Client client = new Client(msgElements[0], Integer.parseInt(msgElements[1]));
        String[] files  = msgElements[2].split(",");
        ServerDB.registerClient(client, new ArrayList<>(Arrays.asList(files)));
//        for (int i=0; i < files.length; i++){
//            System.out.println(files[i]);
//            ServerDB.insert(files[i], client);
//        }
    }
}

