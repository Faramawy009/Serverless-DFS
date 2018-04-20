package edu.umn.SDFS.ServerSide;

import java.net.ServerSocket;
import java.net.Socket;

public class RemoveClientListener implements  Runnable{
	private ServerSocket serverSocket;
	public RemoveClientListener(int port) throws Exception{
		serverSocket = new ServerSocket(port);
	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("Waiting for client remove request on port " +
								serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();
				System.out.println("received remove request from client " + server.getRemoteSocketAddress());
				new Thread(new ClientRemoveHandler(server)).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
