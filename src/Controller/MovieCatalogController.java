package Controller;

import Data.WebScraping.MoviesDataApi;
import Data.TMDBApi.TMDBApi;
import View.ResponseView;
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
                response = ResponseView.render(moviesDataApi.getMoviesByCinema("yelmo-santa-cruz-tenerife/meridiano"));
                //response = gson.toJson(moviesDataApi.getMoviesByCinema("yelmo-santa-cruz-tenerife/meridiano"));
            } else if (path.equals("/api/movies/la-villa-de-orotava")) {
                response = ResponseView.render(moviesDataApi.getMoviesByCinema("yelmo-santa-cruz-tenerife/la-villa-de-orotava"));
                //response = gson.toJson(moviesDataApi.getMoviesByCinema("yelmo-santa-cruz-tenerife/la-villa-de-orotava"));
            } else if (path.equals("/api/movies/multicines-tenerife")) {
                response = ResponseView.render(moviesDataApi.getMoviesByCinema("multicinestenerife"));
                //response = gson.toJson(moviesDataApi.getMoviesByCinema("multicinestenerife"));
            } else if (path.equals("/api/movies/netflix")) {
                response = ResponseView.render(tmdb.fetchMovies("8"));
                //response = gson.toJson(tmdb.fetchMovies("8"));
            } else if (path.equals("/api/movies/disneyplus")) {
                response = ResponseView.render(tmdb.fetchMovies("337"));
                //response = gson.toJson(tmdb.fetchMovies("337"));
            } else if (path.equals("/api/movies/max")) {
                response = ResponseView.render(tmdb.fetchMovies("1899"));
                //response = gson.toJson(tmdb.fetchMovies("1899"));
            } else if (path.equals("/api/movies/allTheaterMovies")) {
                response = ResponseView.render(moviesDataApi.getAllMoviesList());
                //response = gson.toJson(moviesDataApi.getAllMoviesList());
            } else if (path.equals("/api/movies/count/netflix")) {
                response = ResponseView.render(tmdb.getTotalMovies("8"));
                //response = gson.toJson(tmdb.getTotalMovies("8"));
            } else if (path.equals("/api/movies/count/disneyplus")) {
                response = ResponseView.render(tmdb.getTotalMovies("337"));
                //response = gson.toJson(tmdb.getTotalMovies("337"));
            } else if (path.equals("/api/movies/count/max")) {
                response = ResponseView.render(tmdb.getTotalMovies("1899"));
                //response = gson.toJson(tmdb.getTotalMovies("1899"));
            } else if (path.equals("/api/movies/count/netflixtv")) {
                response = ResponseView.render(tmdb.getTotalSeries("8"));
                //response = gson.toJson(tmdb.getTotalSeries("8"));
                System.out.println(response);
            } else if (path.equals("/api/movies/count/disneytv")) {
                response = ResponseView.render(tmdb.getTotalSeries("337"));
                //response = gson.toJson(tmdb.getTotalSeries("337"));
            }  else if (path.equals("/api/movies/count/maxtv")) {
                response = ResponseView.render(tmdb.getTotalSeries("1899"));
                //response = gson.toJson(tmdb.getTotalSeries("1899"));
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