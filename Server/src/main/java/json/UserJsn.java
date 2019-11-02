package json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class UserJsn {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("movies")
    @Expose
    private List<MovieJsn> movies = null;
    @SerializedName("balance")
    @Expose
    private String balance;


    public UserJsn(String username,String password, String type, String country) {
        this.username = username;
        this.type = type;
        this.password = password;
        this.country = country;
        this.movies = new LinkedList<>();
        this.balance = "0";
    }

    public String getUsername() {
        return username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) { this.type = type; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) { this.password = password; }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) { this.country = country; }

    public List<MovieJsn> getMovies() {
        return movies;
    }

    public void addMovie(MovieJsn movie) { this.movies.add(movie); }

    public void removeMovie(MovieJsn movie) {
        boolean found=false;
        for(int i=0;i<movies.size()&!found;i++)
        {
            if(movies.get(i).getName().equals(movie.getName()))
            {
                this.movies.remove(movies.get(i));
                found=true;
            }
        }

    }

    public Integer getBalance() { return Integer.parseInt(balance); }

    public void setBalance(String balance) { this.balance = balance; }
}