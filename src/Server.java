import Controller.DashBoardDataController;
import Controller.MovieCatalogController;
import Data.TMDBApi.TMDBApi;
import Data.WebScraping.MoviesDataApi;
import Model.Movie;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

import java.util.List;

public class Server {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080),0);
        MoviesDataApi mv = new MoviesDataApi();
        server.createContext("/api/movies", new MovieCatalogController(mv));
        server.createContext("/api/dashboard", new DashBoardDataController(mv));

        server.setExecutor(null);
        System.out.println("Servidor iniciado en: http://localhost::8080");
        server.start();
    }
}
