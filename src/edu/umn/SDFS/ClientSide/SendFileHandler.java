package edu.umn.SDFS.ClientSide;


import sun.misc.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.Math.toIntExact;

/**
 * Created by elfar009 on 4/16/18.
 */
public class SendFileHandler implements Runnable{
//    private String filePath;
//    private String ip;
//    private int port;
    private Socket clientSocket;
    public SendFileHandler(Socket clientSocket) {
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
        String filePath = null;
        try {
            filePath = in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Path path = Paths.get(filePath);
        File file = path.toFile();
        if (!file.exists()){
            System.out.println("requested file doesn't exist!");
            return;
        }
        int fileSize = toIntExact(file.length());
        byte[] buffer = new byte[0];
        try {
            buffer = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DownloadObject downloadObject = new DownloadObject(fileSize, buffer);
        try {
            ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOutput.writeObject(downloadObject);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        Socket socket = null;
//        DataOutputStream out = null;
//        InputStream in = null;
//
//        try {
//            socket = new Socket(ip, port);
//            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//            in = new FileInputStream(file);
//            // send file size first
//            out.writeLong(fileSize);
//            //out.writeUTF(";");
//            out.flush();
//            int count;
//            while((count = in.read(buffer)) > 0){
//                out.write(buffer, 0, count);
//            }
//
//            in.close();
//            out.close();
//            socket.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }
}
