package Data.TMDBApi;

import Model.Message;
import Model.Movie;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TMDBApi {

    private static TMDBApi instance;
    private static final String API_KEY = "cede27d74412936334f128dcbce95713";

    private TMDBApi() {}

    public static TMDBApi getInstance() {
        if (instance == null) {
            instance = new TMDBApi();
        }
        return instance;
    }


    public List<Movie> fetchMovies(String watchProviderCode) {
        List<Movie> moviesList = new ArrayList<>();
        try {
            // Construir la URL de la API con el código del proveedor de streaming
            String apiUrl = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY + "&with_watch_providers=" + watchProviderCode + "&watch_region=ES";
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            // Leer la respuesta de la API
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parsear el JSON de la respuesta usando Gson
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);

            // Verificar si la clave "results" existe y contiene una lista
            if (jsonResponse.has("results")) {
                JsonArray results = jsonResponse.getAsJsonArray("results");

                // Iterar sobre cada película en "results"
                for (int i = 0; i < results.size(); i++) {
                    JsonObject movieJson = results.get(i).getAsJsonObject();
                    String title = movieJson.get("title").getAsString();
                    String overview = movieJson.get("overview").getAsString();
                    String posterPath = movieJson.has("poster_path") ? movieJson.get("poster_path").getAsString() : "";

                    // Crear el objeto Movie
                    Movie movie = new Movie(title, overview, "https://image.tmdb.org/t/p/w500" + posterPath);

                    // Agregar la película a la lista
                    moviesList.add(movie);

                    // Actualizar la interfaz de usuario (opcional)
                    // String posterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
                    // StreamingGUI.updateNotifications(userMoviesPanel, title, overview, posterUrl);
                }
            } else {
                System.out.println("No se encontraron resultados.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return moviesList;  // Retornar la lista de películas
    }

    public Message getTotalMovies(String watchProviderCode) {
        int totalMovies = 0;
        try {
            // Construir la URL de la API con el código del proveedor de streaming
            String apiUrl = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY + "&with_watch_providers=" + watchProviderCode + "&watch_region=ES";
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            // Leer la respuesta de la API
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parsear el JSON de la respuesta usando Gson
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);

            // Obtener el número total de resultados
            if (jsonResponse.has("total_results")) {
                totalMovies = jsonResponse.get("total_results").getAsInt();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Message(totalMovies + "");  // Retornar el número total de películas
    }

    public List<Movie> fetchSeries(String watchProviderCode) {
        List<Movie> seriesList = new ArrayList<>();
        try {
            // Construir la URL de la API con el código del proveedor de streaming
            String apiUrl = "https://api.themoviedb.org/3/discover/tv?api_key=" + API_KEY + "&with_watch_providers=" + watchProviderCode + "&watch_region=ES";
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            // Leer la respuesta de la API
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parsear el JSON de la respuesta usando Gson
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);

            // Verificar si la clave "results" existe y contiene una lista
            if (jsonResponse.has("results")) {
                JsonArray results = jsonResponse.getAsJsonArray("results");

                // Iterar sobre cada serie en "results"
                for (int i = 0; i < results.size(); i++) {
                    JsonObject seriesJson = results.get(i).getAsJsonObject();
                    String name = seriesJson.get("name").getAsString(); // Usar "name" para series
                    String overview = seriesJson.get("overview").getAsString();
                    String posterPath = seriesJson.has("poster_path") ? seriesJson.get("poster_path").getAsString() : "";

                    // Crear el objeto Movie (se puede renombrar a algo genérico como Media si se usa para ambos)
                    Movie series = new Movie(name, overview, "https://image.tmdb.org/t/p/w500" + posterPath);

                    // Agregar la serie a la lista
                    seriesList.add(series);
                }
            } else {
                System.out.println("No se encontraron resultados.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return seriesList;  // Retornar la lista de series
    }

    public Message getTotalSeries(String watchProviderCode) {
        int totalMovies = 0;
        try {
            // Construir la URL de la API con el código del proveedor de streaming
            String apiUrl = "https://api.themoviedb.org/3/discover/tv?api_key=" + API_KEY + "&with_watch_providers=" + watchProviderCode + "&watch_region=ES";
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            // Leer la respuesta de la API
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parsear el JSON de la respuesta usando Gson
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);

            // Obtener el número total de resultados
            if (jsonResponse.has("total_results")) {
                totalMovies = jsonResponse.get("total_results").getAsInt();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Message(totalMovies + "");  // Retornar el número total de películas
    }

}
