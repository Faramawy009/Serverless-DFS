package edu.umn.SDFS.ServerSide;

/**
 * Created by mouba005 on 4/16/18.
 */

import com.sun.security.ntlm.Server;
import edu.umn.SDFS.ClientSide.Client;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by mouba005 on 4/16/18.
 */
public class ServerDB {
    static private Hashtable<String, ArrayList<Client>> db;
//    static private Hashtable<Client, Client> activeClients;
    static {
        db = new Hashtable<String, ArrayList<Client>>();
//        activeClients = new Hashtable<>();
    }


    public static void insert(String file, Client client){
//        if (!activeClients.contains(client))
//            activeClients.put(new Client(client), new Client(client));
        if (!db.containsKey(file)){
            db.put(file, new ArrayList<>());
        }
        db.get(file).add(client);
    }

    public static void registerClient(Client client, ArrayList<String> files){
        for (String file : files)
            insert(file, client);
    }
    /*
     * This method will just make all pointers to that object point to null
     */
    public static void removeInactiveClient (Client client){
        for(Map.Entry<String, ArrayList<Client>> entry : db.entrySet()){
            entry.getValue().remove(client);
        }
        System.out.println("inactive client "+client.getIp()+":"+client.getPort()+" was removed");
    }
    /*
     * The returned list may contain null. Net to handled by the client
     */
    public static ArrayList<Client> getOwners(String file){
        if (db.containsKey(file))
            return db.get(file);
        else return new ArrayList<Client>();
    }

    public static String dbToString(){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, ArrayList<Client>> entry : db.entrySet()){
            sb.append(entry.getKey()+";");
            for(Client c : entry.getValue()){
                sb.append(c+",");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void buildDbFromFile() throws Exception{
        FileReader fileReader = new FileReader(ServerMain.homeFolder + "dbImage.txt");

        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                if(line.length()==0)
                    break;
                String [] lineContent = line.split(";");
                String fileName = lineContent[0];
                String clientNames = lineContent[1];
                String [] clients = clientNames.split(",");
                for(String c: clients) {
                    ServerDB.insert(fileName, new Client(c.split(":")[0], Integer.parseInt(c.split(":")[1])));
                }
                //System.out.println(line);
            }
        }
    }

}

