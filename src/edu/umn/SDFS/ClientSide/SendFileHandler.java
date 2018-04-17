package edu.umn.SDFS.ClientSide;


import java.io.*;
import java.net.Socket;

/**
 * Created by mouba005 on 4/16/18.
 */
public class SendFileHandler implements Runnable{
    private String filePath;
    private String ip;
    private int port;

    public SendFileHandler(String filePath, String ip, int port) {
        this.filePath = filePath;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        File file = new File (filePath);
        if (!file.exists()){
            System.out.println("requested file doesn't exist!");
            return;
        }
        long fileSize = file.length();

        Socket socket = null;
        DataOutputStream out = null;
        InputStream in = null;
        byte[] buffer = new byte[4096];
        try {
            socket = new Socket(ip, port);
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            in = new FileInputStream(file);
            // send file size first
            out.writeLong(fileSize);
            //out.writeUTF(";");
            out.flush();
            int count;
            while((count = in.read(buffer)) > 0){
                out.write(buffer, 0, count);
            }

            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
