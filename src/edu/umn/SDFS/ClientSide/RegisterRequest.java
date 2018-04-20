package edu.umn.SDFS.ClientSide;

/**
 * Created by mouba005 on 4/17/18.
 */

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by mouba005 on 4/16/18.
 *
 * This class sends a registration request to the tracking server from the client
 * When the client starts, this request contains all the files that the client has
 */
public class RegisterRequest {
    String serverIp;
    int serverPort;
    String clientIp;
    int clientPort;
    ArrayList<String> fileNames;

    public RegisterRequest(String serverIp, int serverPort, String clientIp, int clientPort, ArrayList<String> fileNames) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.clientIp = clientIp;
        this.clientPort = clientPort;
        this.fileNames = fileNames;
    }

    public void register() throws Exception{
        StringBuilder registerMsg = new StringBuilder();
        registerMsg.append(clientIp+";"+Integer.toString(clientPort)+";");
        for(String fileName : fileNames){
            registerMsg.append(fileName+",");
        }
        // delete extra ","
        registerMsg.deleteCharAt(registerMsg.length()-1);


        Socket socket = null;
        boolean dataArrived;
        do{
            dataArrived = true;
            try {
                socket = new Socket(serverIp, serverPort);
                OutputStream outToServer = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(outToServer);
                out.writeUTF(registerMsg.toString());
            } catch (Exception e) {
                dataArrived = false;
                System.out.println("Server offline, retrying to register again in 5 seconds...");
                Thread.sleep(5000);
            }

        } while(!dataArrived);
        socket.close();


//        try {
//            Socket socket = new Socket(serverIp, serverPort);
//            OutputStream outToServer = socket.getOutputStream();
//            DataOutputStream out = new DataOutputStream(outToServer);
//            out.writeUTF(registerMsg.toString());
//            socket.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}


