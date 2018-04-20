package edu.umn.SDFS.ClientSide;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static edu.umn.SDFS.ClientSide.ClientMain.homeFolder;
import static java.lang.Math.toIntExact;

/**
 * Created by mouba005 on 4/16/18.
 */
public class RequestFileHandler implements Runnable{
    private ArrayList<Client> ownerClients;
    private String fileName;
    public RequestFileHandler(ArrayList<Client> ownerClients,  String fileName) {
        this.ownerClients = ownerClients;
        this.fileName = fileName;
        for(Client c : ownerClients)
            System.out.println(c);
    }

    @Override
    public void run() {
        if (ownerClients.contains(new Client(ClientMain.myIp, ClientMain.myPort))){
            System.out.println("local copy of the file "+this.fileName+" exists");
            return;
        }
        int fileSize = 0;
        if (ownerClients.size() < 1){
            System.out.println("No owners for this file "+ fileName);
            return;
        }

        for(int currentOwnerIdx = 0; currentOwnerIdx<ownerClients.size(); currentOwnerIdx++) {
            String ownerIp = ownerClients.get(currentOwnerIdx).getIp();
            int ownerPort = ownerClients.get(currentOwnerIdx).getPort();
            OutputStream outFile = null;
            Socket clientSocket = null;

            try {
                clientSocket = new Socket(ownerIp, ownerPort);
                OutputStream outToServer = clientSocket.getOutputStream();
                DataOutputStream out = new DataOutputStream(outToServer);

                out.writeUTF(fileName);

            } catch (Exception e) {
                System.out.println("peer "+ownerPort+" offline, retrying to get  the file: " + fileName + " from next owner in 2 seconds...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                new Thread(new RemoveClientSender(ownerIp, ownerPort)).start();
                continue;
            }
            DownloadObject downloadObject = null;
            try {
                ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
                downloadObject = (DownloadObject) inFromServer.readObject();
                fileSize = toIntExact(downloadObject.checkSum);

                clientSocket.close();
            } catch (Exception e){
                e.printStackTrace();
            }
            //If file is not corrupted, end the loop.
            File file = new File(homeFolder + "/" + fileName);
            try {
                outFile = new FileOutputStream(file);
                outFile.write(downloadObject.file, 0, fileSize);
                outFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (file.length() == fileSize)
            {
                System.out.println("File " + fileName + " Was received successfully from owner " + ownerPort);
                return;
            }
        }
        System.out.println("Failed to receive  " + fileName );

    }
}
