package edu.umn.SDFS.ClientSide;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class GetLoadListener implements Runnable {
	private ServerSocket serverSocket;
	public GetLoadListener(int port) throws Exception{
		serverSocket = new ServerSocket(port);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				DataInputStream in = new DataInputStream(clientSocket.getInputStream());
				in.readUTF();
				OutputStream outToServer = clientSocket.getOutputStream();
				DataOutputStream out = new DataOutputStream(outToServer);
				out.writeUTF(""+(Thread.activeCount()-5));
				clientSocket.close();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
