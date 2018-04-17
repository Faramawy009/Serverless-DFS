package edu.umn.SDFS.ClientSide;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.Math.toIntExact;

/**
 * Created by mouba005 on 4/16/18.
 */
public class RequestFileHandler implements Runnable{
    private ArrayList<Client> ownerClients;
//    private ArrayList<Integer> ownerPorts;
    private String fileName;
    // make sure the arrays are send using new (will not be removed from stack after initialization)
    public RequestFileHandler(ArrayList<Client> ownerClients,  String fileName) {
        this.ownerClients = ownerClients;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        int fileSize = 0;
        File file = new File(fileName);
        if (ownerClients.size() < 1){
            System.out.println("No owners for this file "+ fileName);
            return;
        }
        int currentOwnerIdx = 0;
        do {
            String ownerIp = ownerClients.get(currentOwnerIdx).getIp();
            int ownerPort = ownerClients.get(currentOwnerIdx).getPort();
            // get a socket ready to receive the requested file from peer
            OutputStream outFile = null;

            try {
                Socket clientSocket = new Socket(ownerIp, ownerPort);
                OutputStream outToServer = clientSocket.getOutputStream();
                DataOutputStream out = new DataOutputStream(outToServer);

                out.writeUTF(fileName);

                ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
                DownloadObject downloadObject = (DownloadObject)inFromServer.readObject();
                fileSize = toIntExact(downloadObject.checkSum);
                outFile.write(downloadObject.file, 0, fileSize);
                //Received Download Object from serving peer.
//                DataInputStream in = new DataInputStream(inFromServer);
//                outFile = new FileOutputStream(fileName, true);
//                fileSize = in.readLong();
//                byte[] bytes = new byte[4096];
//                int count;
//                while ((count = in.read(bytes)) > 0) {
//                    outFile.write(bytes, 0, count);
//                }
//                in.close();
                outFile.close();
                clientSocket.close();


            } catch (Exception e) {
                e.printStackTrace();
            }

//            try {
//                Socket sendReqSocket = new Socket(ownerIp, ownerPort);
//                String requestBody = new String(recvIp + ";" + Integer.toString(recvPort) + ";" + fileName);
//                DataOutputStream bodyOut = new DataOutputStream(sendReqSocket.getOutputStream());
//                bodyOut.writeUTF(requestBody);
//                sendReqSocket.close();
//                bodyOut.close();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

//            //new receive the file on the receive socket
//            Socket socket = null;
//            DataInputStream in = null;
//            OutputStream out = null;
//            try {
//                if (!file.createNewFile()) {
//                    System.out.println("failed to create " + fileName);
//                    return;
//                }
//                socket = recvSocket.accept();
//                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
//                // true to append
//                out = new FileOutputStream(fileName, true);
//                fileSize = in.readLong();
//                byte[] bytes = new byte[4096];
//                int count;
//                while ((count = in.read(bytes)) > 0) {
//                    out.write(bytes, 0, count);
//                }
//                in.close();
//                out.close();
//                socket.close();
//                recvSocket.close();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            currentOwnerIdx = (currentOwnerIdx+1) % ownerClients.size();
        // check if file is corrupted
        } while (file.length() != fileSize);


    }
}
