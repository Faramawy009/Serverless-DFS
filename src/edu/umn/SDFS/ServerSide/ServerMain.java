package edu.umn.SDFS.ServerSide;

import edu.umn.SDFS.ClientSide.ClientMain;

import java.io.File;
import java.util.Scanner;

public class ServerMain {
	public static int registerServerPort = 55555;
	public static int getOwnersServerPort = 44444;
	public static int removeClientServerPort = 33333;
	public static int numClients = 5;
	public static void main (String ... args) throws Exception {
		//Start by asking all the clients for the files they have... Concurrently!
		Thread[] tids = new Thread[numClients];
		for (int i=0; i < numClients; i++){
			tids[i] = new Thread(new RequestClientFiles(ClientMain.registerPortBase+i+1));
			tids[i].start();
		}
		for (int i=0; i < numClients; i++){
			tids[i].join();
		}

		//A thread listener that listen for clients updatelist/register requests
		new Thread(new RegisterRequestListener(registerServerPort)).start();
		//A thread listener that listens for clients get owner requests and returns the
		//Owners of the specified files to their TCP requests
		new Thread(new GetOwnersListener(getOwnersServerPort)).start();
		//A thread listener that listens for clients that ask for a removal of another peer
		//Since that peer didn't respond to their requests, and thus is offline.
		new Thread(new RemoveClientListener(removeClientServerPort)).start();
		while (true){
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter d to show database");
			String s = sc.nextLine();
			if (s.equals("d")){
				System.out.println(ServerDB.dbToString());
			}
		}
	}
}
