package commands;

import sharedData.User;
import sharedData.UsersSharedData;


public class login extends Command {
    private String username;
    private String password;

    public login(String username, String password, Integer clientId, UsersSharedData usd) {
        super.usd = usd;
        super.clientId = clientId;
        this.username = username;
        this.password = password;
    }


    @Override
    public String activateCommand() {

        UsersSharedData.usersLock.writeLock().lock();
        User user = usd.getUser(username);
        String output;

        if (user == null) {
            output = "ERROR login failed";

        } else if (!(user.getPassword().equals(password))) {
            output = "ERROR login failed";

        } else if (user.getIsLoggedIn()) {
            output = "ERROR login failed";

        } else if (usd.isclientLogdIn(clientId)) {
            output = "ERROR login failed";

        } else {

            usd.logIn(clientId, username);
            output = "ACK login succeeded";
        }

        UsersSharedData.usersLock.writeLock().unlock();
        return output;
    }
}
