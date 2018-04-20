package edu.umn.SDFS.ClientSide;

import edu.umn.SDFS.ServerSide.ServerMain;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by mouba005 on 4/17/18.
 */
public class ClientMain {
    public static String serverIp = "localhost";
    public static String homeFolder;
    public static int portBase = 1234;
    public static int myPort;
    public static String myIp;


    //This function contacts the tracking server to get a list of Clients that own the requested file
    public static ArrayList<Client> getFileOwners(String fileName) throws Exception {
        Socket clientSocket = null;
        boolean dataArrived;
        do{
            dataArrived = true;
            try {
                clientSocket = new Socket(serverIp, ServerMain.getOwnersServerPort);
                OutputStream outToServer = clientSocket.getOutputStream();
                DataOutputStream out = new DataOutputStream(outToServer);

                out.writeUTF(fileName);
            } catch (Exception e) {
                dataArrived = false;
                System.out.println("Server offline, retrying to get owners of the file: " + fileName + " again in 5 seconds...");
                Thread.sleep(5000);
            }

        } while(!dataArrived);

        ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
        ArrayList<Client> ownersList = (ArrayList<Client>)inFromServer.readObject();
        clientSocket.close();
        return ownersList;
    }


    public static void main(String args[]) throws Exception{
        Scanner sc = new Scanner(System.in);
//        System.out.println("Please enter client ip");
//        String myIp = sc.nextLine();
        myIp = "localhost";
        System.out.println("Please enter client id");
        int id = Integer.parseInt(sc.nextLine());
        myPort = id + portBase;
        String serverIp = "localhost";

//        System.out.println("Please enter you home directory that contains your files");
//        homeFolder = sc.nextLine();
        homeFolder = "src/Clients/C"+id;

        ArrayList<String> fileNames = new ArrayList<>();
        File folder = new File(homeFolder);
        File[] listOfFiles = folder.listFiles();
        for(File f: listOfFiles) {
            fileNames.add(f.getName());
        }


        RegisterRequest registerRequest = new RegisterRequest(serverIp, ServerMain.registerServerPort,
                                            myIp, myPort, fileNames);
        registerRequest.register();
        System.out.println("client registered!");
        //Start the thread that listens for requests from other clients
        new Thread(new ClientListener(myPort)).start();
        while(true) {
            System.out.println("Please enter a filename to download!");
            String requestedFile = sc.nextLine();
            ArrayList<Client> owners = getFileOwners(requestedFile);
            new Thread(new RequestFileHandler(owners, requestedFile)).start();
        }

    }
}
