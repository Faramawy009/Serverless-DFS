package edu.umn.SDFS.ServerSide;

import edu.umn.SDFS.ClientSide.Client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientRemoveHandler implements Runnable{

		private Socket clientSocket;
    public ClientRemoveHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		@Override
		public void run() {
    	Client toRemove = null;
			try {
				ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
				toRemove = (Client)in.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			}
			ServerDB.removeInactiveClient(toRemove);
		}
}
