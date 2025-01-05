import Controller.MovieController;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080),0);

        server.createContext("/api/movies", new MovieController());

        server.setExecutor(null);
        System.out.println("Servidor iniciado en: http://localohst;:8080");
        server.start();

    }
}
