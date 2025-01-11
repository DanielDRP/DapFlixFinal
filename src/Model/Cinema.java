package Model;

import java.util.Arrays;
import java.util.List;

public class Cinema {
    private String cinemaName;
    private List<Movie> movies;

    public Cinema(String cinemaName, List<Movie> movies) {
        this.cinemaName = cinemaName;
        this.movies = movies;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public int getMovieCount() {
        return movies.size();
    }

    public List<Movie> getMovies() {
        return movies;
    }
}
