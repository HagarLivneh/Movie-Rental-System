package bgu.spl181.net.impl.BBtpc;

import sharedData.MoviesSharedData;
import sharedData.UsersSharedData;
import srv.LineMessageEncoderDecoder;
import srv.MovieRentProtocol;
import srv.Server;

import java.util.function.Supplier;

public class TPCMain {
    public static void main(String[] args) {

        UsersSharedData.remInstance();
        MoviesSharedData.remInstance();

        Supplier protocol=new Supplier() {
            @Override
            public MovieRentProtocol get() {
              return new MovieRentProtocol(UsersSharedData.getInstance(),MoviesSharedData.getInstance());
            }
        };

        Supplier encDec=new Supplier() {
            @Override
            public LineMessageEncoderDecoder get() {
                return new LineMessageEncoderDecoder();
            }
        };


        Server<String > bidiServer = Server.<String>threadPerClient(Integer.parseInt(args[0]),protocol,encDec);
        bidiServer.serve();
    }

}
