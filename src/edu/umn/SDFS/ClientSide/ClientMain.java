package edu.umn.SDFS.ClientSide;

import edu.umn.SDFS.ServerSide.ServerMain;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.toIntExact;

/**
 * Created by mouba005 on 4/17/18.
 */
public class ClientMain {
	  public static int registerServerPort = 55555;
	  public static int getOwnersServerPort = 44444;
	  public static int removeClientServerPort = 33333;
    public static String serverIp = "localhost";

    public static int sendFilePortBase = 10000;
    public static int getLoadPortBase = 20000;
    public static int registerPortBase = 30000;
    public static int mySendFilePort;
    public static int myGetLoadPort;
    public static int myRegisterPort;
    public static String myIp = "localhost";
    public static Client myself;
    public static int myId;

    public static int numPeers = 5;
		public static String homeFolder;
	  public static ArrayList<String> fileNames;


	public static List<Client> peers;
		static {
			fileNames = new ArrayList<>();
			peers = new ArrayList<>();
			for(int i=0; i<numPeers; i++) {
				peers.add(new Client("localhost", sendFilePortBase+i+1, getLoadPortBase+i+1, registerPortBase+i+1));
			}
		}

    //This function contacts the tracking server to get a list of Clients that own the requested file
    public static ArrayList<Client> getFileOwners(String fileName) throws Exception {
				int counter = 1;
        Socket clientSocket = null;
        boolean dataArrived;
        do{
            dataArrived = true;
            try {
                clientSocket = new Socket(serverIp, ServerMain.getOwnersServerPort);
                OutputStream outToServer = clientSocket.getOutputStream();
                DataOutputStream out = new DataOutputStream(outToServer);

                out.writeUTF(fileName);
            } catch (Exception e) {
                dataArrived = false;
								if(counter == 5) {
									System.out.println("Server offline, Please try again later...");
									return new ArrayList<>();
								}
								System.out.println("Trial " + counter + " out of 5 --- Server offline, retrying to get owners of the file: " + fileName + " again in 5 seconds...");
								counter++;
                Thread.sleep(5000);
            }

        } while(!dataArrived);

        ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
        ArrayList<Client> ownersList = (ArrayList<Client>)inFromServer.readObject();
        clientSocket.close();
        return ownersList;
    }

    //This function contacts a specified peer to ask for its load, it is used by both the UI
		//And the download function to know the load of the peer before requesting a file to download
	  //From it.
    public static int getLoad(int peerID){
			int peerGetLoadPort = getLoadPortBase + peerID;
			int load;
			try {
				Socket clientSocket = new Socket("localhost", peerGetLoadPort);
				OutputStream outToServer = clientSocket.getOutputStream();
				DataOutputStream out = new DataOutputStream(outToServer);
				out.writeUTF("getLoad");
				DataInputStream in = new DataInputStream(clientSocket.getInputStream());
				String loadMsg = in.readUTF();
				clientSocket.close();
				load = Integer.parseInt(loadMsg);
			} catch (Exception e){
				return -1;
			}
			return load;
		}

    public static void download(String fileName, ArrayList<Client> owners) throws Exception {
			if (owners.contains(myself)) {
				System.out.println("local copy of the file " + fileName + " exists");
				return;
			}
			if (owners.size() < 1) {
				System.out.println("No owners for this file " + fileName);
				return;
			}
			HashSet<Client> hashedOwners = new HashSet<>(owners);
			boolean firstRound = true;

			for (int peerIndex = 0; peerIndex < peers.size(); peerIndex++) {
				if (!hashedOwners.contains(peers.get(peerIndex))) {
					if (peerIndex == peers.size() - 1 && firstRound) {
						peerIndex = 0;
						firstRound = false;
					}
					continue;
				}

				String ownerIp = peers.get(peerIndex).getIp();
				int ownerPort = peers.get(peerIndex).getsendFilePort();
				int ownerId = ownerPort - sendFilePortBase;


				if (firstRound) {
					int peerLoad = getLoad(ownerId);
					if (peerLoad == -1 || peerLoad > 2) {
						if (peerIndex == peers.size() - 1) {
							peerIndex = 0;
							firstRound = false;
						}
						if(peerLoad == -1) {
							System.out.println("peer " + ownerId + " offline, retrying to get  the file: " + fileName + " from next owner in 2 seconds...");
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							new Thread(new RemoveClientSender(peers.get(peerIndex))).start();
							hashedOwners.remove(peers.get(peerIndex));
						}
						if(peerLoad>2) {
							System.out.println("peer " + ownerId + " overloaded, retrying to get  the file: " + fileName + " from next owner will get back to this peer if other peers fail...");
						}
					} else {
						Socket clientSocket = null;
						DownloadObject downloadObject = null;
						try {
							clientSocket = new Socket(ownerIp, ownerPort);
							OutputStream outToServer = clientSocket.getOutputStream();
							DataOutputStream out = new DataOutputStream(outToServer);
							out.writeUTF(fileName);
							ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
							downloadObject = (DownloadObject) inFromServer.readObject();
							clientSocket.close();
						} catch (Exception e) {
							System.out.println("peer " + ownerId + " offline, retrying to get  the file: " + fileName + " from next owner in 2 seconds...");
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							new Thread(new RemoveClientSender(peers.get(peerIndex))).start();
							hashedOwners.remove(peers.get(peerIndex));
							if (peerIndex == peers.size() - 1) {
								peerIndex = 0;
								firstRound = false;
							}
							continue;
						}

						String localCheckSum = computeCheckSum(downloadObject.file);

						if (localCheckSum.equals(downloadObject.checkSum)) {
							File file = new File(homeFolder + "/" + fileName);
							FileOutputStream outFile = new FileOutputStream(file);
							outFile.write(downloadObject.file, 0, downloadObject.file.length);
							outFile.close();
							System.out.println("File " + fileName + " Was received successfully from owner " + ownerId);
							return;
						} else {
							System.out.println("File received from owner " + ownerId + " but with invalid checksum retrying with different owner...");
							if (peerIndex == peers.size() - 1) {
								peerIndex = 0;
								firstRound = false;
							}
						}
					}
				}	else {
					DownloadObject downloadObject = null;
					try {
						Socket clientSocket = new Socket(ownerIp, ownerPort);
						OutputStream outToServer = clientSocket.getOutputStream();
						DataOutputStream out = new DataOutputStream(outToServer);
						out.writeUTF(fileName);
						ObjectInputStream inFromServer = new ObjectInputStream(clientSocket.getInputStream());
						downloadObject = (DownloadObject) inFromServer.readObject();
						clientSocket.close();
					} catch (Exception e) {
						System.out.println("peer " + ownerId + " offline, retrying to get  the file: " + fileName + " from next owner in 2 seconds...");
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						new Thread(new RemoveClientSender(peers.get(peerIndex))).start();
						hashedOwners.remove(peers.get(peerIndex));
						continue;
					}
					String localCHeckSum = computeCheckSum(downloadObject.file);

					if (localCHeckSum.equals(downloadObject.checkSum)) {
						System.out.println("File " + fileName + " Was received successfully from owner " + ownerId);
						File file = new File(homeFolder + "/" + fileName);
						FileOutputStream outFile = new FileOutputStream(file);
						outFile.write(downloadObject.file, 0, downloadObject.file.length);
						outFile.close();
						return;
					} else {
						System.out.println("File received from owner " + ownerId + " but with invalid checksum retrying with different owner...");
					}
				}
			}
			System.out.println("Failed to receive  " + fileName );
		}

		public static String computeCheckSum(byte [] fileData) throws Exception{
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				md.update(fileData);
				byte[] mdbytes = md.digest();
				StringBuffer sb = new StringBuffer("");
				for (int i = 0; i < mdbytes.length; i++) {
					sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
				}
				return sb.toString();
			}

    public static void main(String args[]) throws Exception{

        Scanner sc = new Scanner(System.in);

        System.out.println("Please enter client id");

        /* Setting up receiving port numbers based on the client id and the predefined port bases */
			  myId = Integer.parseInt(sc.nextLine());
        mySendFilePort = myId + sendFilePortBase;
        myGetLoadPort = myId + getLoadPortBase;
        myRegisterPort = myId + registerPortBase;
				myself = new Client(myIp, mySendFilePort, myGetLoadPort, myRegisterPort);
				/*
					Reading the directory of this client to know what files do we have in it
				  Each client folder name is C + client id (e.g client with id 1, reads from folder "C1")
				 */
        homeFolder = "src/Clients/C"+myId;

        File folder = new File(homeFolder);
        File[] listOfFiles = folder.listFiles();
        for(File f: listOfFiles) {
            fileNames.add(f.getName());
        }

        //Overloading peer 3 with fake requests simulation (useless threads) to stress test the overloaded peer
				if(myId == 3) {
        	new Thread(new Runnable() { @Override public void run() { while(true) {	}}}).start();
        	new Thread(new Runnable() { @Override public void run() { while(true) {	}}}).start();
        	new Thread(new Runnable() { @Override public void run() { while(true) {	}}}).start();
        	new Thread(new Runnable() { @Override public void run() { while(true) {	}}}).start();
				}

        System.out.println("Getting peers latencies... \n");
        LatencyReader.readPeerLatencies("src/Clients/Latencies", myId);
        //Start the thread that listens for download file requests from other clients
        new Thread(new ClientListener(mySendFilePort)).start();
        //start the thread that listens for getLoad requests from other clients
			  new Thread(new GetLoadListener(myGetLoadPort)).start();
			  //start the thread that listens for getlist commands from server
			  new Thread(new GetListListener(myRegisterPort)).start();
			  /* IMPORTANT NOTE:
			  			The server asks for all the clients files when it starts, so clients MUST be running
			  			Ahead of the server (Which makes sense because server organizes client traffic)
			  			However, when a client fails and restarts, it must inform the server manually using
			  			an "updatelist" request what files it has.
			   */
				System.out.println("If recovered from failure, please update list first to allow other peers to ask for your files");
				while(true) {
        	  System.out.println("Please enter one of the following commands:");
            System.out.println("find <filename>");
            System.out.println("download <filename>");
            System.out.println("getload <peerid>");
            System.out.println("updatelist");
            String command = sc.nextLine();
            String [] commandList = command.split(" ");
            if(commandList.length > 2) {
            	System.out.println("Invalid option");
							continue;
            }
            if(commandList.length == 1) {
            	if(commandList[0].equals("updatelist")) {
								//Send update list request
								RegisterRequest.register();
							} else {
								System.out.println("Invalid option");
								continue;
							}
						} else{
            	if(commandList[0].equals("find")) {
								ArrayList<Client> owners = getFileOwners(commandList[1]);
								if(owners.size()>0) {
									for(Client c:owners) {
										System.out.println( "Peer id:" + (c.getsendFilePort()-sendFilePortBase));
									}
								} else {
									System.out.println("No owners found...");
								}
							} else if(commandList[0].equals("download")){
            		ArrayList<Client> owners = getFileOwners(commandList[1]);
            		download(commandList[1], owners);

							} else if(commandList[0].equals("getload")) {
								int load = getLoad(Integer.parseInt(commandList[1]));
								if (load == -1){
									System.out.println("peer is offline, try again later...");
								} else{
									System.out.println("peer load is "+load);
								}
							} else{
								System.out.println("Invalid option");
								continue;
							}
						}
        }

    }
}
