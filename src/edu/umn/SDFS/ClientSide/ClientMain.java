package edu.umn.SDFS.ClientSide;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by mouba005 on 4/17/18.
 */
public class ClientMain {
    public static String homeFolder;
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter client ip");
        String myIp = sc.nextLine();
        System.out.println("Please enter client port");
        int myPort = Integer.parseInt(sc.nextLine());
//        System.out.println("Please enter server ip");
//        String serverIp = sc.nextLine();
//        System.out.println("Please enter server port");
//        int serverPort = Integer.parseInt(sc.nextLine());
        String serverIp = "localhost";
        int serverPort = 55555;
        System.out.println("Please enter you home directory that contains your files");
        homeFolder = sc.nextLine();
        ArrayList<String> fileNames = new ArrayList<>();
        File folder = new File(homeFolder);
        File[] listOfFiles = folder.listFiles();
        for(File f: listOfFiles) {
            fileNames.add(f.getName());
        }
        RegisterRequest registerRequest = new RegisterRequest(serverIp, serverPort,
                                            myIp, myPort, fileNames);
        registerRequest.register();
        System.out.println("client registered!");
        System.out.println("Please enter a filename to download!");
        String requestedFile = sc.nextLine();
        new Thread(new SendFileHandler(server)).start();

    }
}
