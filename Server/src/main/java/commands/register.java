package commands;

import sharedData.UsersSharedData;

public class register extends Command{
    private String username;
    private String password;
    private String country;


    public register(Integer clientId, String username, String password, String country, UsersSharedData usd)
    {
        super.usd = usd;
        super.clientId=clientId;
        this.username=username;
        this.password=password;
        this.country=country;


    }

    public String activateCommand()
    {
        UsersSharedData.usersLock.writeLock().lock();
        String output;

        if(username == null || password == null || country == ""){
            output = "ERROR registration failed";
        }

        else if(usd.userExist(username)){
            output = "ERROR registration failed";
        }

        else if(usd.isclientLogdIn(clientId)){
            output = "ERROR registration failed";
        }
        else {
            usd.addUser(username, password, country, clientId);
            output = "ACK registration succeeded";
        }

        UsersSharedData.usersLock.writeLock().unlock();
        return output;
    }
}


