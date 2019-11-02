package sharedData;

import json.JsonObj;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MoviesSharedData {


    private Map<String,Movie>moviesMap;
    private AtomicInteger availableId;
    public static ReadWriteLock moviesLock=null;

    private static MoviesSharedData instance = null;


    public MoviesSharedData() {
        this.moviesLock = new ReentrantReadWriteLock();
        moviesMap=new ConcurrentHashMap<>();
        int currId = initializeMap();
        availableId=new AtomicInteger(currId);

    }

    public static MoviesSharedData getInstance()
    {
        if(instance == null) {
            instance = new MoviesSharedData();
        }
        return instance;
    }


    private int initializeMap()
    {
        int output = -1;
        for(Movie movie: JsonObj.getMoviesObjects()){
            moviesMap.put(movie.getName(),movie);
            output = Math.max(output,movie.getId());
        }
        return output;
    }

    public boolean addMovie(String name, Integer amount, Integer price, List<String>bannedCountries)
    {

        Movie newMovie=new Movie(availableId.incrementAndGet(),name,amount,price,bannedCountries);
        moviesMap.put(name,newMovie);
        JsonObj.addMovie(newMovie.getId(),name,amount,price,bannedCountries);
        return true;
    }

    //check exist and number of rent
    public Boolean removeMovie (String movieName){

        moviesMap.remove(movieName);
        JsonObj.removeMovie(movieName);
        return true;
    }

    public Set<String> getMovies(){
        return moviesMap.keySet();
    }


    public Movie getMovie(String movieName) {
        return moviesMap.get(movieName);
    }


    public void returnMovie(String movieName){
        Movie movie = moviesMap.get(movieName);
        movie.setAvailableAmount(movie.getAvailableAmount()+1);
    }

    public Boolean isMovieExist(String movieName){
        return moviesMap.containsKey(movieName);
    }

    public boolean isMovieRented(String movieName) {
        return moviesMap.get(movieName).isRentd();
    }

    public void setMoviePrice(String movieName, Integer price) {
        moviesMap.get(movieName).setPrice(price);
        JsonObj.changePrice(movieName,price);
    }

    public int getNumOfAvalableCopies(String movieName) {
        return moviesMap.get(movieName).getAvailableAmount();
    }

    public void movieRent(String movieName) {
        Movie movie = moviesMap.get(movieName);
        movie.setAvailableAmount(movie.getAvailableAmount()-1);
    }

    public Object getMoviePrice(String movieName) {
        return moviesMap.get(movieName).getPrice();
    }

    public static void remInstance() {
        instance = null;
    }
}
