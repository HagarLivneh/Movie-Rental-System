package json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class AllMoviesJsn {
    @SerializedName("movies")
    @Expose
    private List<MovieJsn> movies = null;

    public List<MovieJsn> getMovies() { return movies; }

    public void setMovies( List<MovieJsn> movies) { this.movies = movies; }


}

