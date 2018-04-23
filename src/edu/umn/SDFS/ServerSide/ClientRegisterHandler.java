package edu.umn.SDFS.ServerSide;

/**
 * Created by mouba005 on 4/16/18.
 */

import edu.umn.SDFS.ClientSide.Client;
import edu.umn.SDFS.ClientSide.Ownership;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
        Ownership ownership = null;
        try {
            ObjectInputStream inFromClient = new ObjectInputStream(clientSocket.getInputStream());
            ownership = (Ownership)inFromClient.readObject();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ownership != null)
            ServerDB.updateClient(ownership.getC(), ownership.getFiles());
    }
}

