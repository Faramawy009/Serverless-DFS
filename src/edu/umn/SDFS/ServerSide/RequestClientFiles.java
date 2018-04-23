package edu.umn.SDFS.ServerSide;

import edu.umn.SDFS.ClientSide.Client;
import edu.umn.SDFS.ClientSide.ClientMain;
import edu.umn.SDFS.ClientSide.Ownership;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class RequestClientFiles implements Runnable{
	int port;
	public RequestClientFiles(int port){
		this.port = port;
	}
	@Override
	public void run() {
		Socket clientSocket = null;
			try {
				clientSocket = new Socket("localhost", port);
				OutputStream outToServer = clientSocket.getOutputStream();
				DataOutputStream out = new DataOutputStream(outToServer);

				out.writeUTF("getFiles");
			} catch (Exception e) {
				System.out.println("client with id "+(port- ClientMain.registerPortBase)+" is offline...");
			}


		ObjectInputStream inFromClient = null;
		Ownership ownership = null;
		try {
			inFromClient = new ObjectInputStream(clientSocket.getInputStream());
			ownership = (Ownership)inFromClient.readObject();
			clientSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ServerDB.registerClient(ownership.getC(), ownership.getFiles());
	}
}
