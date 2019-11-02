package json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieJsn {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("bannedCountries")
    @Expose
    private List<String> bannedCountries = null;
    @SerializedName("availableAmount")
    @Expose
    private String availableAmount;
    @SerializedName("totalAmount")
    @Expose
    private String totalAmount;


    public MovieJsn(String id, String name, String price, List<String> bannedCountries,String totalAmount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.bannedCountries = bannedCountries;
        this.totalAmount = totalAmount;
        this.availableAmount = totalAmount;
    }
    public MovieJsn(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return Integer.parseInt(id);
    }

    public void setId(String id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public Integer getPrice() {
        return Integer.parseInt(price);
    }

    public void setPrice(String price) { this.price = price; }

    public List<String> getBannedCountries() {
        return bannedCountries;
    }

    public void setBannedCountries(List<String> bannedCountries) { this.bannedCountries = bannedCountries; }

    public Integer getAvailableAmount() {
        return Integer.parseInt(availableAmount);
    }

    public void setAvailableAmount(String availableAmount) { this.availableAmount = availableAmount; }

    public Integer getTotalAmount() {
        return Integer.parseInt(totalAmount);
    }

    public void setTotalAmount(String totalAmount) { this.totalAmount = totalAmount; }
}