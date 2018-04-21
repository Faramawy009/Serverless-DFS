package edu.umn.SDFS.ClientSide;

import edu.umn.SDFS.ServerSide.ServerMain;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static edu.umn.SDFS.ClientSide.ClientMain.homeFolder;
import static edu.umn.SDFS.ClientSide.ClientMain.peers;
import static edu.umn.SDFS.ClientSide.ClientMain.serverIp;
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

        HashSet<Client> hashedOwners = new HashSet<>(ownerClients);
        boolean firstRound = true;

//        for(int currentOwnerIdx = 0; currentOwnerIdx<ownerClients.size(); currentOwnerIdx++) {
        for(int currentOwnerIdx = 0; currentOwnerIdx < peers.size(); currentOwnerIdx++){
            if (!hashedOwners.contains(peers.get(currentOwnerIdx))) {
                if (currentOwnerIdx == peers.size() - 1) {
                    currentOwnerIdx = 0;
                    firstRound = false;
                }
                continue;
            }
            String ownerIp = peers.get(currentOwnerIdx).getIp();
            int ownerPort = peers.get(currentOwnerIdx).getPort();
            OutputStream outFile = null;
            Socket clientSocket = null;
            if (firstRound) {
                try {
                    clientSocket = new Socket(ownerIp, ownerPort);
                    OutputStream outToServer = clientSocket.getOutputStream();
                    DataOutputStream out = new DataOutputStream(outToServer);
                    out.writeUTF("getLoad");
                    DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                    String loadMsg = in.readUTF();
                    clientSocket.close();
                    int peerLoad = Integer.parseInt(loadMsg);
                    System.out.println("Load of the peer " + ownerPort + " is " + peerLoad);
                    if (peerLoad > 2) {
                        if(currentOwnerIdx == peers.size()-1){
                            currentOwnerIdx = 0;
                            firstRound = false;
                        }
                        continue;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

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
                try {
                    new RegisterRequest(ClientMain.serverIp, ClientMain.registerServerPort,
														ClientMain.myIp, ClientMain.myPort,
														new ArrayList<>(Arrays.asList(fileName))).register();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            if(currentOwnerIdx == peers.size()-1){
                currentOwnerIdx = 0;
                firstRound = false;
            }

        }
        System.out.println("Failed to receive  " + fileName );

    }
}
