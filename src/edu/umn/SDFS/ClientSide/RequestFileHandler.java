package edu.umn.SDFS.ClientSide;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by mouba005 on 4/16/18.
 */
public class RequestFileHandler implements Runnable{
    private String[] ownerIps;
    private int[] ownerPorts;
    private String fileName;
    // make sure the arrays are send using new (will not be removed from stack after initialization)
    public RequestFileHandler(String[] ownerIps, int[] ownerPorts, String fileName) {
        this.ownerIps = ownerIps;
        this.ownerPorts = ownerPorts;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        long fileSize = 0;
        File file = new File(fileName);
        if (ownerIps.length < 1){
            System.out.println("No owners for this file "+ fileName);
            return;
        }
        int currentOwnerIdx = 0;
        do {
            String ownerIp = ownerIps[currentOwnerIdx];
            int ownerPort = ownerPorts[currentOwnerIdx];
            if (currentOwnerIdx >= ownerIps.length){
                System.out.println("failed to fetch this file from all owners "+fileName);
            }
            // get a socket ready to receive the requested file from peer
            ServerSocket recvSocket = null;
            String recvIp = "";
            int recvPort = 0;

            try {
                recvSocket = new ServerSocket();
                recvSocket.bind(null);
                recvIp = recvSocket.getInetAddress().getHostAddress();
                recvPort = recvSocket.getLocalPort();

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Socket sendReqSocket = new Socket(ownerIp, ownerPort);
                String requestBody = new String(recvIp + ";" + Integer.toString(recvPort) + ";" + fileName);
                DataOutputStream bodyOut = new DataOutputStream(sendReqSocket.getOutputStream());
                bodyOut.writeUTF(requestBody);
                sendReqSocket.close();
                bodyOut.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            //new receive the file on the receive socket
            Socket socket = null;
            DataInputStream in = null;
            OutputStream out = null;
            try {
                if (!file.createNewFile()) {
                    System.out.println("failed to create " + fileName);
                    return;
                }
                socket = recvSocket.accept();
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                // true to append
                out = new FileOutputStream(fileName, true);
                fileSize = in.readLong();
                byte[] bytes = new byte[4096];
                int count;
                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
                in.close();
                out.close();
                socket.close();
                recvSocket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            currentOwnerIdx++;
        // check if file is currupted
        } while (file.length() != fileSize);


    }
}
