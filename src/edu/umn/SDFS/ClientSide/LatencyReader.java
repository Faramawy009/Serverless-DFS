package edu.umn.SDFS.ClientSide;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static edu.umn.SDFS.ClientSide.ClientMain.peers;

public class LatencyReader {

	private static class ClientLatency implements Comparable{
		public Client c;
		public int latency;

		public ClientLatency(Client c, int latency) {
			this.c = c;
			this.latency = latency;
		}

		@Override
		public int compareTo(Object o) {
			ClientLatency other = (ClientLatency) o;
			return this.latency - ((ClientLatency) o).latency;
		}
	}

	public static void readPeerLatencies(String latencyFile, int myId) throws IOException {
		String line = Files.readAllLines(Paths.get(latencyFile)).get(myId-1);
		String [] latencies = line.split(";")[1].split(",");
		ArrayList<String> peerLatenciesStrings = new ArrayList<>(Arrays.asList(latencies));
		peerLatenciesStrings.remove(myId-1);
		peers.remove(myId-1);

		ArrayList<ClientLatency> peerLatencies = new ArrayList<>();
		for(int i=0; i<ClientMain.numPeers-1; i++) {
			peerLatencies.add(new ClientLatency(peers.get(i), Integer.parseInt(peerLatenciesStrings.get(i))));
		}
//		peers.sort(Comparator.comparingInt(item -> peerLatencies.indexOf(item)));
		Collections.sort(peerLatencies);
		peers.clear();
		for(ClientLatency cl: peerLatencies) {
			peers.add(cl.c);
		}
		System.out.println("Hello I am client of port " + ClientMain.myPort + " And these are the closest clients to me: ");
		for(Client c:peers) {
			System.out.println(c.getPort());
		}
	}
}
