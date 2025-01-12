package Controller;

import Data.WebScraping.MoviesDataApi;
import Data.TMDBApi.TMDBApi;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class MovieCatalogController implements HttpHandler {

    private final MoviesDataApi moviesDataApi;
    TMDBApi tmdb;
    private final Gson gson;

    public MovieCatalogController(MoviesDataApi mv) {
        this.moviesDataApi = mv; // Instancia centralizada de MoviesDataApi
        this.tmdb = TMDBApi.getInstance();
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode = 200;

        if ("GET".equals(exchange.getRequestMethod())) {
            String path = exchange.getRequestURI().getPath();
            System.out.println("Petición recibida: " + path);

            if (path.equals("/api/movies/yelmo-meridiano")) {
                response = gson.toJson(moviesDataApi.getMoviesByCinema("yelmo-santa-cruz-tenerife/meridiano"));
            } else if (path.equals("/api/movies/la-villa-de-orotava")) {
                response = gson.toJson(moviesDataApi.getMoviesByCinema("yelmo-santa-cruz-tenerife/la-villa-de-orotava"));
            } else if (path.equals("/api/movies/multicines-tenerife")) {
                response = gson.toJson(moviesDataApi.getMoviesByCinema("multicinestenerife"));
            } else if (path.equals("/api/movies/netflix")) {
                System.out.println("Buscando películas en Netflix "); // BORRAR
                response = gson.toJson(tmdb.fetchMovies("8"));
            } else if (path.equals("/api/movies/disneyplus")) {
                System.out.println("Buscando películas en Disney "); // BORRAR
                response = gson.toJson(tmdb.fetchMovies("337"));
            } else if (path.equals("/api/movies/max")) {
                System.out.println("Buscando películas en Max "); // BORRAR
                response = gson.toJson(tmdb.fetchMovies("1899"));
            } else if (path.equals("/api/movies/allTheaterMovies")) {
                response = gson.toJson(moviesDataApi.getAllMoviesList());
            } else if (path.equals("/api/movies/count/netflix")) {
                System.out.println("Contando películas en Netflix");
                response = gson.toJson(tmdb.getTotalMovies("8"));
            } else if (path.equals("/api/movies/count/disneyplus")) {
                System.out.println("Contando películas en Disney+");
                response = gson.toJson(tmdb.getTotalMovies("337"));
            } else if (path.equals("/api/movies/count/max")) {
                System.out.println("Contando películas en Max");
                response = gson.toJson(tmdb.getTotalMovies("1899"));
            } else {
                response = gson.toJson("Ruta no encontrada");
                statusCode = 404;
            }

            // Configurar encabezados CORS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            // Enviar respuesta
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            exchange.sendResponseHeaders(405, -1); // Método no permitido
        }
    }
}