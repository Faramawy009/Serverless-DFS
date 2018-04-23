package edu.umn.SDFS.ClientSide;


import sun.misc.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static edu.umn.SDFS.ClientSide.ClientMain.homeFolder;
import static java.lang.Math.toIntExact;

/**
 * Created by elfar009 on 4/16/18.
 */
public class SendFileHandler implements Runnable{
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
        Path path = Paths.get(homeFolder + "/" + filePath);
        File file = path.toFile();
        if (!file.exists()){
            System.out.println("requested file doesn't exist!");
            return;
        }
        byte[] buffer = new byte[0];
        try {
            buffer = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            DownloadObject downloadObject = new DownloadObject(ClientMain.computeCheckSum(buffer), buffer);
            ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
            objectOutput.writeObject(downloadObject);
            System.out.println("File " + filePath + " Was sent successfully to client of port " +clientSocket.getLocalPort());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
