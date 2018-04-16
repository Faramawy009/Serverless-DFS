package ServerSide;

import ClientSide.Client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * Created by mouba005 on 4/16/18.
 */
public class ServerDB {
    static private Hashtable<String, ArrayList<Client>> db;
    static private Hashtable<Client, Client> activeClients;
    static {
        db = new Hashtable<String, ArrayList<Client>>();
        activeClients = new Hashtable<>();
    }


    public static void insert(String file, Client client){
        if (!activeClients.contains(client))
            activeClients.put(new Client(client), new Client(client));
        if (!db.contains(file)){
            db.put(new String(file), new ArrayList<>());
        }
        db.get(file).add(activeClients.get(client));
    }

    public static void registerClient(Client client, ArrayList<String> files){
        for (String file : files)
            insert(file, client);
    }
    /*
     * This method will just make all pointers to that object point to null
     */
    public static void removeInactiveClient (Client client){
        activeClients.remove(client);
    }
    /*
     * The returned list may contain null. Net to handled by the client
     */
    public static ArrayList<Client> getOwners(String file){
        if (db.containsKey(file))
            return db.get(file);
        else return null;
    }
}
