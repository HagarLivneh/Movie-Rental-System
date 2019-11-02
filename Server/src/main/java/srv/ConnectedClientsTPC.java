package srv;

import sharedData.UsersSharedData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConnectedClientsTPC<T> implements Connections<T> {

    private Map<Integer, BlockingConnectionHandler> connectedMap;
    private static ConnectedClientsTPC instance = null;


    private ConnectedClientsTPC() {
        connectedMap=new HashMap<>();
    }

    public static ConnectedClientsTPC getInstance()
    {
        if(instance == null) {
            instance = new ConnectedClientsTPC();
        }
            return instance;
    }


    @Override
    synchronized public boolean send(int connectionId, T msg) {
        if(connectedMap.containsKey(connectionId))
        {
            connectedMap.get(connectionId).send(msg);
        return true;
        }
        return false;
    }

    //send the given message to all the connected clients
    @Override
    public void broadcast(T msg) {

        Map<Integer, Boolean> clientsLoginMap = UsersSharedData.clientsLoginMap;
        Set<Integer> loggedInClients = clientsLoginMap.keySet();

        for(Integer clientId:loggedInClients){
            if(clientsLoginMap.get(clientId)){
                connectedMap.get(clientId).send(msg);
            }


        }
    }

    @Override
    public void disconnect(int connectionId) {
        try {
            connectedMap.get(connectionId).close();
            connectedMap.remove(connectionId);//removes also from the connected clients map
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addConnectedClient(int id,BlockingConnectionHandler handler)
    {//if the client doesn't exist add it to the map
        if(!connectedMap.containsKey(id))
        {
            connectedMap.put(id,handler);
        }
    }
}
