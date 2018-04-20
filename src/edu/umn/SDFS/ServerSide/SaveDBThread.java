package edu.umn.SDFS.ServerSide;

import java.io.*;

public class SaveDBThread implements Runnable {

	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(5000);
				PrintWriter writer = new PrintWriter(ServerMain.homeFolder+"dbImage.txt", "UTF-8");
				writer.println(ServerDB.dbToString());
				writer.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
