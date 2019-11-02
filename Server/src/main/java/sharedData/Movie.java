package sharedData;

import java.util.List;

public class Movie{
    private Integer id;
    private String name;
    private Integer totalAmount;
    private Integer availableAmount;
    private Integer price;
    private List<String> bannedCountries;




    public Movie(Integer id,String name,Integer totalAmount,Integer price, List<String>bannedCountries)
    {
        this.id=id;
        this.name=name;
        this.totalAmount=totalAmount;
        availableAmount=totalAmount;
        this.price=price;
        this.bannedCountries=bannedCountries;

    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public List<String> getBannedCountries() {
        return bannedCountries;
    }

    public Integer getAvailableAmount() {
        return availableAmount;
    }

    public void setAvailableAmount(Integer availableAmount) {
        this.availableAmount = availableAmount;
    }

    public Boolean isMovieLegalInCountry(String country){
        for(String c : bannedCountries){
            if(country.equals(c)){
                return false;
            }
        }
        return true;
    }


    public boolean isRentd() {
        return (availableAmount != totalAmount);
    }
}