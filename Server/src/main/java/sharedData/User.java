package sharedData;

import java.util.List;

public class User{

    private String username;
    private boolean isAdmin;
    private String password;
    private String country;
    private List<String> movies;
    private Integer balance;
    private boolean isLoggedIn;

    public User(String username, boolean isAdmin, String password,String country,List<String> movies, Integer balance)
    {
        this.username=username;
        this.isAdmin=isAdmin;
        this.country=country;
        this.password=password;
        this.movies=movies;
        this.balance=balance;
        isLoggedIn=false;
    }
    public String getUsername() {
        return username;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public String getPassword() {
        return password;
    }

    public String getCountry() {
        return country;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public Boolean isUserRentMovie(String movieName){

        for(String m : movies){
            if(movieName.equals(m)){
                return true;
            }
        }
        return false;
    }

    public void removeMovie (String movie){
        movies.remove(movie);
    }

    public void rentMovie(String movieName, Integer price) {
        balance = balance-price;
        movies.add(movieName);
    }
}