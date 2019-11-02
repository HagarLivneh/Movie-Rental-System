package srv.ReactorPkg;
import sharedData.UsersSharedData;
import srv.Connections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConnectedClientsReactor<T> implements Connections<T> {

    private Map<Integer, NonBlockingConnectionHandler> connectedMap;
    private static ConnectedClientsReactor instance = null;
    private ConnectedClientsReactor() {
        connectedMap=new HashMap<>();
    }

    public static ConnectedClientsReactor getInstance()
    {
        if(instance == null) {
            instance = new ConnectedClientsReactor();
        }
        return instance;
    }


    @Override
    synchronized public boolean send(int connectionId, T msg) {

            boolean output = false;
            if(connectedMap.containsKey(connectionId))
        {
            connectedMap.get(connectionId).send(msg);
//            System.out.println("send to "+connectionId+" "+msg);
            output = true;
        }
            return output;

    }
    //send the given message to all the connected clients
    @Override
    public void broadcast(T msg) {

        UsersSharedData.usersLock.readLock().lock();
        Map<Integer, Boolean> clientsLoginMap = UsersSharedData.clientsLoginMap;
        Set<Integer> loggedInClients = clientsLoginMap.keySet();

        for(Integer clientId:loggedInClients){
            if(clientsLoginMap.get(clientId)){
                connectedMap.get(clientId).send(msg);

            }
        }
        UsersSharedData.usersLock.readLock().unlock();

    }

    @Override
    public void disconnect(int connectionId) {
            connectedMap.get(connectionId).close();
            connectedMap.remove(connectionId);//removes also from the connected clients map

    }

    synchronized public void addConnectedClient(int id,NonBlockingConnectionHandler handler) {//if the client doesn't exist add it to the map

            if (!connectedMap.containsKey(id)) {
                connectedMap.put(id, handler);
            }

    }

}
