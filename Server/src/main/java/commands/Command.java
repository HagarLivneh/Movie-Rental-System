package commands;

import sharedData.UsersSharedData;
import sharedData.MoviesSharedData;
import srv.Connections;

public abstract class Command {

    protected Integer clientId;
    protected UsersSharedData usd;
    protected MoviesSharedData msd;

    public abstract String activateCommand();

}
