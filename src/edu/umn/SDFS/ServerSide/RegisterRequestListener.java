package edu.umn.SDFS.ServerSide;

import java.net.ServerSocket;
import java.net.Socket;

public class RegisterRequestListener implements Runnable {
	private ServerSocket serverSocket;
	public RegisterRequestListener(int port) throws Exception{
		serverSocket = new ServerSocket(port);
	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("Waiting for client register request on port " +
								serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();
				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				new Thread(new ClientRegisterHandler(server)).start();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
