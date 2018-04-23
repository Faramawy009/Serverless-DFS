package edu.umn.SDFS.ClientSide;

import edu.umn.SDFS.ServerSide.ServerMain;

import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import static edu.umn.SDFS.ClientSide.ClientMain.serverIp;
//This class allows each peer to send a request to the tracking server
//To remove another peer from its data in case that peer is offline.
public class RemoveClientSender implements Runnable{
	private Client clientToRemove;

	public RemoveClientSender(Client clientToRemove) {
		this.clientToRemove = clientToRemove;
	}

	@Override
	public void run() {
		Socket socket = null;
		try {
			socket = new Socket(serverIp, ServerMain.removeClientServerPort);
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(clientToRemove);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
