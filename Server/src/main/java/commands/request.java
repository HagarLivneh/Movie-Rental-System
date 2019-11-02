package commands;

import json.JsonObj;
import sharedData.Movie;
import sharedData.MoviesSharedData;
import sharedData.User;
import sharedData.UsersSharedData;
import srv.Connections;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class request extends Command {

    private String msg;
    private Connections connections;


    public request(Connections connections,UsersSharedData usd, MoviesSharedData msd, Integer clientId, String msg){
        super.usd = usd;
        super.msd = msd;
        super.clientId = clientId;
        this.connections=connections;
        this.msg = msg;
    }

    @Override
    public String activateCommand() {

        ArrayList<String> arrStr = splitMsg(msg);

        String regName = arrStr.get(1);

        String movieName = "";

        switch (regName) {
            case "info":

                if (arrStr.size() >= 3) {
                    movieName = cutQuMarks(arrStr,2);
                }
                return infoReq(movieName);

            case "rent":
                if(arrStr.size() >= 3) {
                    movieName = cutQuMarks(arrStr, 2);
                }
                return rentReq(movieName);

            case "return":
                if(arrStr.size() >= 3){
                    movieName = cutQuMarks(arrStr,2);
                    return returnReq(movieName);
                }
            case "addmovie":

                if(arrStr.size() >= 5){
                    movieName = cutQuMarks(arrStr,2);
                    Integer amount = 0;
                    Integer price = 0;

                    try {
                        amount = Integer.parseInt(arrStr.get(3));
                        price = Integer.parseInt(arrStr.get(4));
                    }

                    catch (Exception e){}

                    List<String> bannedCountry = new LinkedList<>();
                    for (int i = 5; i < arrStr.size();i++){
                        bannedCountry.add(arrStr.get(i));
                    }

                    cutQuMarksFromList(bannedCountry);

                    return addMovieReq(movieName,amount,price,bannedCountry);
                }
            case "remmovie":
                if(arrStr.size() == 3){
                    movieName = cutQuMarks(arrStr,2);
                    return remmovieReq(movieName);
                }
            case "changeprice":

                if(arrStr.size() == 4){
                    movieName = cutQuMarks(arrStr,2);
                    Integer price = 0;
                    try{
                        price = Integer.parseInt(arrStr.get(3));
                    }
                    catch (Exception e){}

                    return changepriceReq(movieName,price);
                }
        }

        //reg name contain tow words
        if(arrStr.size() >= 3) {
            regName += " " + arrStr.get(2);
        }

        switch (regName) {
            case "balance info":
                return balanceInfoReq();

            case "balance add":
                if(arrStr.size() >= 4) {
                    return balanceAddReq(Integer.parseInt(arrStr.get(3)));
                }
            //can throw exception
        }

    return "ERROR unknown command";

    }

    private String rentReq(String movieName){
        UsersSharedData.usersLock.readLock().lock();
        User user = getUser();
        UsersSharedData.usersLock.readLock().unlock();

        if(user == null){
            return "ERROR request rent failed";
        }

        UsersSharedData.usersLock.writeLock().lock();
        MoviesSharedData.moviesLock.writeLock().lock();
        Movie movie = msd.getMovie(movieName);

        if(movie == null){
            MoviesSharedData.moviesLock.writeLock().unlock();
            UsersSharedData.usersLock.writeLock().unlock();
            return  "ERROR request rent failed";
        }
        if (movie.getAvailableAmount() <= 0) {
            MoviesSharedData.moviesLock.writeLock().unlock();
            UsersSharedData.usersLock.writeLock().unlock();
            return "ERROR request rent failed";
        }

        if(user.getBalance() < movie.getPrice()){
            MoviesSharedData.moviesLock.writeLock().unlock();
            UsersSharedData.usersLock.writeLock().unlock();
            return  "ERROR request rent failed";
        }

        if(!(movie.isMovieLegalInCountry(user.getCountry()))){
            MoviesSharedData.moviesLock.writeLock().unlock();
            UsersSharedData.usersLock.writeLock().unlock();
            return  "ERROR request rent failed";
        }

        if(user.isUserRentMovie(movieName)){
            MoviesSharedData.moviesLock.writeLock().unlock();
            UsersSharedData.usersLock.writeLock().unlock();
            return  "ERROR request rent failed";
        }
        msd.movieRent(movieName);
        usd.userRentMovie(usd.getClientUsername(clientId), movieName, movie.getPrice());
        JsonObj.rent(user.getUsername(), movieName);
        connections.broadcast(String.format("BROADCAST movie \"%s\" %d %d", movieName,msd.getNumOfAvalableCopies(movieName),msd.getMoviePrice(movieName)));

        MoviesSharedData.moviesLock.writeLock().unlock();
        UsersSharedData.usersLock.writeLock().unlock();
        return String.format("ACK rent \"%s\" success", movieName);
    }

    private String infoReq(String movieName){


        UsersSharedData.usersLock.readLock().lock();
        User user = getUser();
        UsersSharedData.usersLock.readLock().unlock();

        if(user == null){
            return "ERROR request info failed";
        }

        MoviesSharedData.moviesLock.readLock().lock();

        String output = "ACK info";
        if(movieName.equals("")){

            for(String movie : msd.getMovies()){
                output += String.format(" \"%s\"", movie);
            }
        }
        else {

            if (!msd.isMovieExist(movieName)) {
                output = "ERROR request info failed";
            }
            else {

                Movie movie = msd.getMovie(movieName);
                output += String.format(" \"%s\" %d %d", movieName, movie.getAvailableAmount(), movie.getPrice());

                for (String c : movie.getBannedCountries()) {
                    output += String.format(" \"%s\"", c);
                }
            }
        }

        MoviesSharedData.moviesLock.readLock().unlock();

        return output;
    }

    private String balanceAddReq(Integer amount){
    UsersSharedData.usersLock.writeLock().lock();
        User user = getUser();
        String output;
        if(user == null){
            output= "ERROR request balance failed";
        }
        else {
            usd.changeUserBalance(user.getUsername(), user.getBalance() + amount);
            output = "ACK balance " + user.getBalance() + " added " + amount;
        }
        UsersSharedData.usersLock.writeLock().unlock();
        return output;
    }

    private String returnReq(String movieName){

        UsersSharedData.usersLock.readLock().lock();
        User user = getUser();
        UsersSharedData.usersLock.readLock().unlock();

        if(user == null){
            return "ERROR request return failed";
        }

        UsersSharedData.usersLock.writeLock().lock();
        MoviesSharedData.moviesLock.writeLock().lock();

        if(!msd.isMovieExist(movieName)){
            MoviesSharedData.moviesLock.writeLock().unlock();
            UsersSharedData.usersLock.writeLock().unlock();
            return  "ERROR request return failed";
        }

        if(!user.isUserRentMovie(movieName)){
            MoviesSharedData.moviesLock.writeLock().unlock();
            UsersSharedData.usersLock.writeLock().unlock();
            return "ERROR request return failed";
        }

        usd.removeMovieFromUser(user.getUsername(), movieName);
        msd.returnMovie(movieName);
        JsonObj.returnMovie(user.getUsername(), movieName);
        Movie movie = msd.getMovie(movieName);
        connections.broadcast(String.format("BROADCAST movie \"%s\" %d %d", movieName, movie.getAvailableAmount(), movie.getPrice()));

        MoviesSharedData.moviesLock.writeLock().unlock();
        UsersSharedData.usersLock.writeLock().unlock();
        return String.format("ACK return \"%s\" success", movieName);
    }

    private String balanceInfoReq(){
        UsersSharedData.usersLock.readLock().lock();

        String output;
        User user = getUser();

        if(user == null){
            output = "ERROR request balance failed";
        }
        else {

            int balance = user.getBalance();
            output = "ACK balance " + balance;
        }
        UsersSharedData.usersLock.readLock().unlock();
        return output;
    }

    //--------ADMIN REQUEST--------

    private String addMovieReq(String movieName, Integer amount, Integer price, List bannedCountrys){

        UsersSharedData.usersLock.readLock().lock();
        if(!isLegalAdminReq()) {

            UsersSharedData.usersLock.readLock().unlock();
            return "ERROR request addmovie failed";
        }
        else {
            UsersSharedData.usersLock.readLock().unlock();
        }

        MoviesSharedData.moviesLock.writeLock().lock();
        String output;

        if(msd.getMovie(movieName) != null){
            output = "ERROR request addmovie failed";
        }

        else if(amount <= 0 || price <= 0){
            output = "ERROR request addmovie failed";
        }
        else {

            msd.addMovie(movieName, amount, price, cutQuMarksFromList(bannedCountrys));
            connections.broadcast(String.format("BROADCAST addmovie \"%s\" %d %d", movieName, amount, price));
            output = String.format("ACK addmovie \"%s\" success", movieName);
        }
        MoviesSharedData.moviesLock.writeLock().unlock();
        return output;
    }

    private String remmovieReq(String movieName){

        UsersSharedData.usersLock.readLock().lock();
        if(!isLegalAdminReq()) {

            UsersSharedData.usersLock.readLock().unlock();
            return "ERROR request remmovie failed";
        }
        else {
            UsersSharedData.usersLock.readLock().unlock();
        }

        MoviesSharedData.moviesLock.writeLock().lock();
        String output;

        if(!msd.isMovieExist(movieName)){
            output = "ERROR request remmovie failed";
        }

        else if(msd.isMovieRented(movieName)){
            output = "ERROR request remmovie failed";
        }
        else {

            msd.removeMovie(movieName);
            connections.broadcast(String.format("BROADCAST movie \"%s\" removed", movieName));
            output = String.format("ACK remmovie \"%s\" success", movieName);
        }

        MoviesSharedData.moviesLock.writeLock().unlock();
        return output;
    }

    private String changepriceReq(String movieName, Integer price){
        UsersSharedData.usersLock.readLock().lock();
        if(!isLegalAdminReq()) {

            UsersSharedData.usersLock.readLock().unlock();
            return "ERROR request changeprice failed";
        }
        else {
            UsersSharedData.usersLock.readLock().unlock();

        }

        MoviesSharedData.moviesLock.writeLock().lock();
        String output;

        if(!msd.isMovieExist(movieName)){
            output = "ERROR request changeprice failed";
        }

        else if(price <= 0){
            output = "ERROR request changeprice failed";
        }

        else {

            msd.setMoviePrice(movieName, price);
            int numOfCopies = msd.getNumOfAvalableCopies(movieName);
            connections.broadcast(String.format("BROADCAST movie \"%s\" %d %d", movieName, numOfCopies, price));
            output = String.format("ACK changeprice \"%s\" success", movieName);
        }
        MoviesSharedData.moviesLock.writeLock().unlock();
        return output;
    }

    //-------METHODS-------

    private User getUser(){
        User userOutput=null;
        if(usd.isclientLogdIn(clientId)){
            String username = usd.getClientUsername(clientId);
            userOutput= usd.getUser(username);
        }
        return userOutput;
    }

    private Boolean isLegalAdminReq(){
        User user = getUser();
        return (user != null) && (user.getIsAdmin());
    }

    private ArrayList<String> splitMsg(String message) {
        ArrayList<String> output = new ArrayList<String>();
        String s = "([^\"]\\S*|\".+?\")\\s*";
        Matcher m = Pattern.compile(s).matcher(message);
        while (m.find())
            output.add(m.group(1)); // Add .replace("\"", "") to remove surrounding quotes.
        return output;
    }

    private String cutQuMarks(List<String> parameters, int i){
        String output =  parameters.get(i);
        output = output.substring(1,output.length()-1);
        return output;
    }

    private List<String> cutQuMarksFromList(List<String> parameters){

        List<String> output = new LinkedList();

        for(int i = 0; i < parameters.size(); i++){
            output.add(cutQuMarks(parameters,i));
        }
        return output;
    }

}
