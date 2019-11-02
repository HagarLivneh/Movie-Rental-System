package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import sharedData.Movie;
import sharedData.User;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class JsonObj {


    public static void addMovie(Integer id, String movieName, Integer amount, Integer price, List<String> bannedCountries) {

        List<MovieJsn>movieJsnList =getMoviesListJS();
        MovieJsn movie=new MovieJsn(Integer.toString(id),movieName,Integer.toString(price),bannedCountries,Integer.toString(amount));
        movieJsnList.add(movie);

        writeToJsn(movieJsnList,"Database/Movies.json");
    }

    public static void removeMovie(String moviename) {

        List<MovieJsn>movieJsnList =getMoviesListJS();
        movieJsnList.remove(getMovieByName(movieJsnList, moviename));
        writeToJsn(movieJsnList, "Database/Movies.json");

    }

    public static void changePrice(String moviename, Integer price)
    {
        List<MovieJsn>movieJsnList =getMoviesListJS();
        getMovieByName(movieJsnList,moviename).setPrice(Integer.toString(price));
        writeToJsn(movieJsnList,"Database/Movies.json");
    }

    public static void addUser(String username, String password, boolean isAdmin, String country) {

        List<UserJsn>userJsnList =getUsersListJS();
        UserJsn user=new UserJsn(username,password,"normal",country);
        userJsnList.add(user);
        writeToJsn(userJsnList, "Database/Users.json");
    }

    public static void removeUser(String username) {

        List<UserJsn>userJsnList =getUsersListJS();
        userJsnList.remove(getUserByName(userJsnList, username));
        writeToJsn(userJsnList, "Database/Users.json");

    }

    public static void addBalance(String username, Integer newBalance)
    {
        List<UserJsn>userJsnList =getUsersListJS();
        getUserByName(userJsnList,username).setBalance(Integer.toString(newBalance));
        writeToJsn(userJsnList,"Database/Users.json");
    }


    public static void rent(String username, String moviename)
    {
        List<UserJsn>userJsnList =getUsersListJS();
        List<MovieJsn>movieJsnList =getMoviesListJS();

        UserJsn user=getUserByName(userJsnList,username);
        MovieJsn tmpMovie=getMovieByName(movieJsnList,moviename);
        MovieJsn movieToAdd=new MovieJsn(Integer.toString(tmpMovie.getId()), tmpMovie.getName());
        user.addMovie(movieToAdd);
        user.setBalance(Integer.toString(user.getBalance()-tmpMovie.getPrice()));
        writeToJsn(userJsnList,"Database/Users.json");

        getMovieByName(movieJsnList,moviename).setAvailableAmount(Integer.toString(getMovieByName(movieJsnList,moviename).getAvailableAmount()-1));
        writeToJsn(movieJsnList,"Database/Movies.json");
    }

    public static void returnMovie(String username, String moviename)
    {
        List<UserJsn>userJsnList =getUsersListJS();
        List<MovieJsn>movieJsnList =getMoviesListJS();

        UserJsn user=getUserByName(userJsnList,username);
        MovieJsn movie=getMovieByName(getMoviesListJS(),moviename);
        MovieJsn movieToRemove=new MovieJsn(Integer.toString(movie.getId()), movie.getName());
        user.removeMovie(movieToRemove);
        writeToJsn(userJsnList,"Database/Users.json");

        getMovieByName(movieJsnList,moviename).setAvailableAmount(Integer.toString(getMovieByName(movieJsnList,moviename).getAvailableAmount()+1));
        writeToJsn(movieJsnList,"Database/Movies.json");
    }

    public static UserJsn getUserByName(List<UserJsn>userJsnList, String username)
    {
        for(int i=0; i<userJsnList.size();i++) {
            if (userJsnList.get(i).getUsername().equals(username) )
            {
                return userJsnList.get(i);
            }
        }
        return null;
    }

    public static MovieJsn getMovieByName(List<MovieJsn>movieJsnList, String moviename)
    {
        for(int i=0; i<movieJsnList.size();i++) {
            if (movieJsnList.get(i).getName().equals(moviename) )
            {
                return movieJsnList.get(i);
            }
        }
        return null;
    }

    public static List<Movie> getMoviesObjects()
    {
        List<Movie>outputList=new LinkedList<>();
        List<MovieJsn> movieJsnList=getMoviesListJS();
        for(int i=0; i<movieJsnList.size();i++) {
            {
                outputList.add(getMovieObject(movieJsnList.get(i)));
            }
        }
        return outputList;
    }

    public static Movie getMovieObject(MovieJsn mjs)
    {
        Movie movie = new Movie(mjs.getId(),mjs.getName(),mjs.getTotalAmount(),mjs.getPrice(),mjs.getBannedCountries());
        movie.setAvailableAmount(mjs.getAvailableAmount());
        return movie;

    }

    public static List<User> getUsersObjects()
    {
        List<User>outputList=new LinkedList<>();
        List<UserJsn> userJsnList=getUsersListJS();
        for(int i=0; i<userJsnList.size();i++) {
            {
                outputList.add(getUserObject(userJsnList.get(i)));
            }
        }
        return outputList;
    }

    public static User getUserObject(UserJsn ujs)
    {
        Boolean isAdmin = ujs.getType().equals("admin");
        List<String> movieNames = new LinkedList<>();
        for(MovieJsn movie: ujs.getMovies()){
            movieNames.add(movie.getName());
        }
        User user = new User(ujs.getUsername(),isAdmin,ujs.getPassword(),ujs.getCountry(),movieNames,ujs.getBalance());
        return user;

    }


    //return the list of users from the json
    public static List<UserJsn> getUsersListJS ()
    {
        List<UserJsn>usersList=new LinkedList<>();
        Gson gson=new Gson();
        try{
            FileReader reader= new FileReader("Database/Users.json");
            JsonReader jsonReader=new JsonReader(reader);
            AllUsersJsn users=gson.fromJson(jsonReader, AllUsersJsn.class);
            usersList=users.getUsers();
            reader.close();
        }
        catch (Exception e)
        { }

        return usersList;
    }

    public static List<MovieJsn> getMoviesListJS ()
    {
        List<MovieJsn>moviesList=new LinkedList<>();
        Gson gson=new Gson();
        try{
            FileReader reader= new FileReader("Database/Movies.json");
            JsonReader jsonReader=new JsonReader(reader);
            AllMoviesJsn movies=gson.fromJson(jsonReader, AllMoviesJsn.class);
            moviesList=movies.getMovies();
            reader.close();
        }
        catch (Exception e)
        {}

        return moviesList;
    }

    private static void writeToJsn(List jsnList, String path)
    {
        Object all=null;
        if(jsnList!=null&& jsnList.size()>0)
        {
            if(jsnList.get(0) instanceof UserJsn)
            {
                all=new AllUsersJsn();
                ((AllUsersJsn )all).setUsers(jsnList);
            }
            else
            {
                all=new AllMoviesJsn();
                ((AllMoviesJsn)all).setMovies(jsnList);
            }
        }


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String strJson = gson.toJson(all);
        FileWriter writer = null;
        try {
            writer = new FileWriter(path);
            writer.write(strJson);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

