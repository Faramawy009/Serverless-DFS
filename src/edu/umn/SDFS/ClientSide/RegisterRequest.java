package edu.umn.SDFS.ClientSide;

/**
 * Created by mouba005 on 4/17/18.
 */

import java.io.DataOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by mouba005 on 4/16/18.
 *
 * This class sends a registration request to the tracking server from the client
 * When the client starts, this request contains all the files that the client has
 */
public class RegisterRequest {
    public static void register() throws Exception{
			Socket socket = null;
			boolean dataArrived;
			int counter = 1;
			do{
					dataArrived = true;
					try {
							socket = new Socket(ClientMain.serverIp, ClientMain.registerServerPort);
							ClientMain.fileNames.clear();
							File folder = new File(ClientMain.homeFolder);
							File[] listOfFiles = folder.listFiles();
							for(File f: listOfFiles) {
								ClientMain.fileNames.add(f.getName());
							}
							Ownership downloadObject = new Ownership(ClientMain.myself, ClientMain.fileNames);
							ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
							objectOutput.writeObject(downloadObject);
					} catch (Exception e) {
							dataArrived = false;
							if(counter == 5) {
								System.out.println("Server offline, Please try again later...");
								return;
							}
							System.out.println("Trial " + counter + " out of 5 --- Server offline, retrying to update list again in 5 seconds...");
							counter++;
							Thread.sleep(5000);
					}


			} while(!dataArrived);
			socket.close();
    }
}


