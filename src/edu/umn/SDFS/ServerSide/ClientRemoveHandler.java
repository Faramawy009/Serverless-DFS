package edu.umn.SDFS.ServerSide;

import edu.umn.SDFS.ClientSide.Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientRemoveHandler implements Runnable{

		private Socket clientSocket;
    public ClientRemoveHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		@Override
		public void run() {
			DataInputStream in = null;
			try {
				in = new DataInputStream(clientSocket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			String clientToRemove = null;
			try {
				clientToRemove = in.readUTF();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String[] clientElements = clientToRemove.split(";");
			if (clientElements.length != 2) {
				System.out.println("invalid client sent!");
				return;
			}
			Client client = new Client(clientElements[0], Integer.parseInt(clientElements[1]));
			ServerDB.removeInactiveClient(client);
		}
}
