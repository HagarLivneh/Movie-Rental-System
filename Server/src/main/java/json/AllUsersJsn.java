package json;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AllUsersJsn {
    @SerializedName("users")
    @Expose
    private List<UserJsn> users = null;

    public List<UserJsn> getUsers() { return users; }

    public void setUsers(List<UserJsn> users) { this.users = users; }

}
