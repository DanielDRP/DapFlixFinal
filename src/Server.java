import Controller.DashBoardDataController;
import Controller.MovieCatalogController;
import Data.WebScraping.MoviesDataApi;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080),0);
        MoviesDataApi mv = new MoviesDataApi();
        server.createContext("/api/movies", new MovieCatalogController(mv));
        server.createContext("/api/dashboard", new DashBoardDataController(mv));

        server.setExecutor(null);
        System.out.println("Servidor iniciado en: http://localhost;:8080");
        server.start();

    }
}
