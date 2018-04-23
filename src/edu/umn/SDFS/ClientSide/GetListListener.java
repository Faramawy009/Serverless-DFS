package edu.umn.SDFS.ClientSide;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class GetListListener implements Runnable{
	private ServerSocket serverSocket;
	public GetListListener(int port) throws Exception{
		serverSocket = new ServerSocket(port);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				DataInputStream in = new DataInputStream(clientSocket.getInputStream());
				in.readUTF();

				Ownership downloadObject = new Ownership(ClientMain.myself, ClientMain.fileNames);
				ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
				objectOutput.writeObject(downloadObject);

				clientSocket.close();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
