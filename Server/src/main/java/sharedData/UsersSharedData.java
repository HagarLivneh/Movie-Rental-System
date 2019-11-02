package sharedData;

import json.JsonObj;
import srv.ConnectedClientsTPC;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UsersSharedData {

    private Map<String, User> usersMap;
    private Map<Integer,String> clientUsernameMap;
    public static Map<Integer,Boolean>clientsLoginMap;
    public static ReadWriteLock usersLock=null;


    private static UsersSharedData instance = null;


    public UsersSharedData() {
        this.usersLock = new ReentrantReadWriteLock();
        usersMap =new ConcurrentHashMap<>();
        clientsLoginMap=new ConcurrentHashMap<>();
        clientUsernameMap = new ConcurrentHashMap<>();
        initializeMap();

    }

    public static UsersSharedData getInstance()
    {
        if(instance == null) {
            instance = new UsersSharedData();
        }
        return instance;
    }


    private int initializeMap()
    {
        int output = 1;
        List<User> usersObjects = JsonObj.getUsersObjects();
        for(User user: usersObjects){
            usersMap.put(user.getUsername(),user);
            output++;
        }
        return output;
    }

    public void addUser(String username, String password ,String country,Integer clientId)
    {

        List<String> movies = new LinkedList<>();
        User newUser=new User(username,false,password,country,movies,0);
        usersMap.put(username,newUser);
        clientUsernameMap.put(clientId, newUser.getUsername());
        clientsLoginMap.put(clientId, false);
        JsonObj.addUser(username,password,false,country);

    }

    //    if client exist, return false
    public boolean addClient(Integer clientId){
        if(clientsLoginMap.containsKey(clientId)){
            return false;
        }
        clientsLoginMap.put(clientId,false);
        return true;
    }

    public boolean userExist (String username)
    {
        return (usersMap.containsKey(username));
    }

    public boolean isclientLogdIn(Integer clientId)
    {
        return clientsLoginMap.get(clientId);
    }

    public User getUser (String username){
        return usersMap.get(username);
    }

    public void logIn(Integer clientId, String username){

        User userToLogIn = usersMap.get(username);
        userToLogIn.setLoggedIn(true);
        clientUsernameMap.put(clientId,username);
        clientsLoginMap.put(clientId,true);
    }

    public void signOut(Integer clientId){

        User userToSignOut = usersMap.get(clientUsernameMap.get(clientId));
        userToSignOut.setLoggedIn(false);
        clientsLoginMap.put(clientId,false);
        clientUsernameMap.remove(clientId);
    }

    public String getClientUsername(Integer clientId){
        return clientUsernameMap.get(clientId);
    }

    public void userRentMovie(String username, String movieName, Integer price) {
        usersMap.get(username).rentMovie(movieName,price);
    }

    public void removeMovieFromUser(String username, String movieName) {
        usersMap.get(username).removeMovie(movieName);
    }

    public void changeUserBalance(String username, int newBalance) {
        usersMap.get(username).setBalance(newBalance);
        JsonObj.addBalance(username,newBalance);
    }

    public static void remInstance() {
        instance = null;
        clientsLoginMap = null;
    }

}
