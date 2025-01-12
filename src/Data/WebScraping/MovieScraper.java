package Data.WebScraping;

import Model.Movie;

import java.util.List;

public interface MovieScraper {
    List<Movie> getSchedule();
}
