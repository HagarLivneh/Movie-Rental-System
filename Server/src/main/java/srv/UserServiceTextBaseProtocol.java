package srv;


import commands.login;
import commands.register;
import commands.signout;
import sharedData.UsersSharedData;
import srv.ReactorPkg.ConnectedClientsReactor;

public class UserServiceTextBaseProtocol implements BidiMessagingProtocol<String> {
    protected int connectionId;
    protected Connections connections;
    protected boolean shouldTerminate;
    protected UsersSharedData usd;

    public UserServiceTextBaseProtocol(UsersSharedData usd) {
        this.usd = usd;
        shouldTerminate = false;
    }

    @Override
    public void start(int connectionId, Connections connections) {
        this.connectionId = connectionId;
        this.connections = connections;
        usd.addClient(connectionId);
    }

    @Override
    public void process(String message) {


        String originalMsg = message;
        message = switchCommand(message, connections);
        connections.send(connectionId, message);

        if (originalMsg.equals("SIGNOUT")) {
            if (message.contains("ACK"))
            {
                shouldTerminate = true;
                connections.disconnect(connectionId);
            }
        }
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }


    protected String switchCommand(String msg, Connections connections) {
        String output = "ERROR unknown command";
        String[] arrStr = msg.split(" ");
        String commandName = arrStr[0];

        switch (commandName) {
            case "REGISTER":
                if (arrStr.length >= 4) {
                    String username = arrStr[1];
                    String password = arrStr[2];
                    String country = "";
                    if (msg.contains("country")) {
                        msg = msg.substring(msg.indexOf("country=") + 9);
                        if (msg.indexOf('\"') > 0 & msg.indexOf('\"') < msg.length()) {
                            country = msg.substring(0, msg.indexOf("\""));
                        }
                    }
                    //country maybe ""
                    register reg = new register(connectionId, username, password, country, usd);
                    output = reg.activateCommand();
                }
                else {
                    output = "ERROR registration failed";
                }
                break;

            case "LOGIN":
                if (arrStr.length >= 3) {
                    String username = arrStr[1];
                    String password = arrStr[2];
                    login login = new login(username, password, connectionId, usd);
                    output = login.activateCommand();
                } else {
                    output = "ERROR login failed";
                }
                break;
            case "SIGNOUT":
                signout signout = new signout(connectionId, usd);
                output = signout.activateCommand();
                break;

        }
        return output;
    }
}


