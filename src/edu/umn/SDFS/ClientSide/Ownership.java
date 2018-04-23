package edu.umn.SDFS.ClientSide;

import java.io.Serializable;
import java.util.ArrayList;
//A wrapper class to send both the client information and the list of files in the same TCP request
public class Ownership implements Serializable {
	Client c;
	ArrayList<String> files;

	public Ownership(Client c, ArrayList<String> files) {
		this.c = c;
		this.files = files;
	}

	public Client getC() {
		return c;
	}

	public ArrayList<String> getFiles() {
		return files;
	}
}
