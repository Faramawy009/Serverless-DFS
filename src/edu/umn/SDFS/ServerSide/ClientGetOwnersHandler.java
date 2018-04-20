package edu.umn.SDFS.ServerSide;

import edu.umn.SDFS.ClientSide.Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientGetOwnersHandler implements Runnable {
	private Socket clientSocket;
	public ClientGetOwnersHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	@Override
	public void run() {
		ObjectOutputStream objectOutput = null;
		DataInputStream in = null;
		String filename = null;
		try {
			objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new DataInputStream(clientSocket.getInputStream());
			filename = in.readUTF();
		} catch (IOException e){
			e.printStackTrace();
		}

		ArrayList<Client> owners = ServerDB.getOwners(filename);

		if (owners.size() == 0){
			System.out.println("The info for this file does not exist! "+filename);
		}
		try {
			objectOutput.writeObject(owners);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
