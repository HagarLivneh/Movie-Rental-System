package srv;

import commands.request;
import sharedData.MoviesSharedData;
import sharedData.UsersSharedData;

public class MovieRentProtocol extends UserServiceTextBaseProtocol {

    private MoviesSharedData msd;

    public MovieRentProtocol(UsersSharedData usd, MoviesSharedData msd)
    {
        super(usd);
        this.msd = msd;
    }

    @Override
    protected String switchCommand(String msg, Connections connections) {
        String[] arrStr = msg.split(" ");
        String commandName=arrStr[0];
        if(commandName.equals("REQUEST"))
        {
            request request = new request(connections,usd,msd,connectionId,msg);
            return request.activateCommand();
        }

        return super.switchCommand(msg, connections);
    }
}
