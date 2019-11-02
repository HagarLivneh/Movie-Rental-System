package commands;

import sharedData.UsersSharedData;

public class signout extends Command{

    public signout(Integer clientId, UsersSharedData usd){

        super.clientId = clientId;
        super.usd = usd;
    }

    @Override
    public String activateCommand() {

        UsersSharedData.usersLock.writeLock().lock();
        String output;
        if(!(usd.isclientLogdIn(clientId))){
            output = "ERROR signout failed";
        }
        else {

            usd.signOut(clientId);
            output = "ACK signout succeeded";
        }

        UsersSharedData.usersLock.writeLock().unlock();
        return output;
    }
}
