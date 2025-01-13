package View;

import Model.Message;
import Model.Movie;
import com.google.gson.Gson;

import java.util.List;

public class ResponseView {

    private static final Gson gson = new Gson();

    public static String render(Message message) {
        return gson.toJson(message);
    }

    public static String render(List<Movie> message) {
        return gson.toJson(message);
    }

    public static String render(Movie message) {
        return gson.toJson(message);
    }

}
