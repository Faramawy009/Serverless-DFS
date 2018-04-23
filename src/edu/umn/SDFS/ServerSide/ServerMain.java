package edu.umn.SDFS.ServerSide;

import edu.umn.SDFS.ClientSide.ClientMain;

import java.io.File;
import java.util.Scanner;

public class ServerMain {
	public static int registerServerPort = 55555;
	public static int getOwnersServerPort = 44444;
	public static int removeClientServerPort = 33333;
	public static int numClients = 5;
	public static String homeFolder = "src/Server/";
	public static void main (String ... args) throws Exception {
		Thread[] tids = new Thread[numClients];
		for (int i=0; i < numClients; i++){
			tids[i] = new Thread(new RequestClientFiles(ClientMain.registerPortBase+i+1));
			tids[i].start();
		}
		for (int i=0; i < numClients; i++){
			tids[i].join();
		}

		new Thread(new RegisterRequestListener(registerServerPort)).start();
		new Thread(new GetOwnersListener(getOwnersServerPort)).start();
//		new Thread(new SaveDBThread()).start();
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
