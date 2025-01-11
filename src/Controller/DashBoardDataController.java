package Controller;

import Data.WebScraping.MoviesDataApi;
import Model.Message;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class DashBoardDataController implements HttpHandler {

    private final MoviesDataApi moviesDataApi;
    private final Gson gson;

    public DashBoardDataController(MoviesDataApi moviesDataApi) {
        this.moviesDataApi = moviesDataApi; // Inyección de dependencia
        this.gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode = 200;

        if ("GET".equals(exchange.getRequestMethod())) {
            String path = exchange.getRequestURI().getPath();
            System.out.println("Petición recibida: " + path);

            if (path.equals("/api/dashboard/moviescount")) {
                int movieCount = moviesDataApi.getMoviesCount();
                System.out.println(movieCount);
                response = gson.toJson(new Message(String.valueOf(movieCount)));
            } else if (path.equals("/api/dashboard/year-ranking")) {
                response = gson.toJson(moviesDataApi.getYearRanking());
            } else if (path.equals("/api/dashboard/most-viewed")) {
                response = gson.toJson(moviesDataApi.getMostViewed());
            }else {
                response = gson.toJson(new Message("Ruta no encontrada"));
                statusCode = 404;
            }

            // Configurar encabezados CORS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            // Enviar respuesta
            exchange.sendResponseHeaders(statusCode, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Método no permitido
        }
    }
}