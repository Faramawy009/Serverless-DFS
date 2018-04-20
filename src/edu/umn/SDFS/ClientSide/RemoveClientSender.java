package edu.umn.SDFS.ClientSide;

import edu.umn.SDFS.ServerSide.ServerMain;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import static edu.umn.SDFS.ClientSide.ClientMain.serverIp;

public class RemoveClientSender implements Runnable{
	private String clientToRemove;

	public RemoveClientSender(String ip, int port) {
		this.clientToRemove = ip+";"+port;
	}

	@Override
	public void run() {
		Socket socket = null;
		try {
			socket = new Socket(serverIp, ServerMain.removeClientServerPort);
			OutputStream outToServer = socket.getOutputStream();
			DataOutputStream out = new DataOutputStream(outToServer);
			out.writeUTF(clientToRemove);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
