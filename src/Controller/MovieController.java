package Controller;

import Data.WebScraping.MTenerifeScraper;
import Data.WebScraping.YelmoScraper;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class MovieController implements HttpHandler {

    final String driver = "/Users/dani/Documents/Uni/DAP/Libraries/chromedriver-mac-x64/chromedriver";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "";
        int statusCode = 200;

        Gson gson = new Gson();

        if ("GET".equals(exchange.getRequestMethod())) {
            String path = exchange.getRequestURI().getPath();
            System.out.println("Peticion recibida: " + path);

            if (path.equals("/api/movies/yelmo-meridiano")){
                System.out.println("Buscando peliculas en Yelmo Cines Meridiano "); // BORRAR
                YelmoScraper yelmo = new YelmoScraper(
                        driver,
                        "https://www.yelmocines.es/cartelera/"
                );
                response = gson.toJson(yelmo.getSchedule("santa-cruz-tenerife/meridiano"));
            }

            if (path.equals("/api/movies/multicines-tenerife")){
                System.out.println("Buscando peliculas en Multicines Tenerife "); // BORRAR
                MTenerifeScraper mt = new MTenerifeScraper(
                        driver,
                        "https://multicinestenerife.com/cartelera-tenerife/"
                );
                response = gson.toJson(mt.getSchedule(""));
            }

            // Configurar encabezados CORS
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Content-Type", "application/json");

            // Enviar respuesta
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            exchange.sendResponseHeaders(405, -1); // Método no permitido
        }
    }
}
