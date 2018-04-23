package edu.umn.SDFS.ServerSide;


import edu.umn.SDFS.ClientSide.Client;
import edu.umn.SDFS.ClientSide.ClientMain;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by mouba005 on 4/16/18.
 */
//This class contains a static hashtable object that contains our database
//Which maps a file name to a list of clients
//It is a hashtable because we require synchronization between the different
//Thread that can be manipulating the data base at the same time.
//It also contains all static methods for database manipulation.
public class ServerDB {
    static private Hashtable<String, ArrayList<Client>> db;
    static {
        db = new Hashtable<>();
    }

    public static void insert(String file, Client client){
        if (!db.containsKey(file)){
            db.put(file, new ArrayList<>());
        }
        if (!db.get(file).contains(client)) {
            db.get(file).add(client);
        }
    }

    public static void registerClient(Client client, ArrayList<String> files){
        for (String file : files)
            insert(file, client);
    }

    public static void updateClient(Client client, ArrayList<String> files){
        for(Map.Entry<String, ArrayList<Client>> entry : db.entrySet())
            entry.getValue().remove(client);
        for (String file : files)
            insert(file, client);
    }

    public static void removeInactiveClient (Client client){
        for(Map.Entry<String, ArrayList<Client>> entry : db.entrySet()){
            entry.getValue().remove(client);
        }
        int removedId = client.getsendFilePort() - ClientMain.sendFilePortBase;
        System.out.println("inactive client "+removedId+" was removed");
    }

    public static ArrayList<Client> getOwners(String file){
        if (db.containsKey(file))
            return db.get(file);
        else return new ArrayList<>();
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
}

