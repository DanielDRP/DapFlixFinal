package Data.WebScraping;

import Model.Movie;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TaquillaESP extends BaseScraper {

    private final String baseUrl;

    public TaquillaESP(String driverPath, String baseUrl) {
        super(driverPath);
        this.baseUrl = baseUrl;
    }

    public List<Movie> getYearRanking() {
        List<Movie> movies = new ArrayList<>();
        WebDriver driver = createWebDriver();  // Usamos el método de la clase base para crear el WebDriver

        try {
            driver.get(baseUrl);  // Cargamos la página
            waitForElement(driver, By.xpath("//section[@id='block-50']//table"));  // Esperamos a que la tabla de películas cargue

            List<WebElement> rows = driver.findElements(By.xpath("//section[@id='block-50']//table/tbody/tr"));

            for (WebElement row : rows) {
                try {
                    // Extraemos la posición, título y recaudación
                    //WebElement positionElement = row.findElement(By.xpath(".//td[1]"));
                    WebElement titleElement = row.findElement(By.xpath(".//td[2]//a"));
                    WebElement revenueElement = row.findElement(By.xpath(".//td[3]//div"));

                    //String position = positionElement.getText();
                    String title = titleElement.getText();
                    String revenue = revenueElement.getText().replace("€", "").trim();  // Eliminamos el símbolo de Euro

                    // Creamos un objeto Movie con los datos extraídos
                    Movie movie = new Movie(title, revenue, "");
                    movies.add(movie);
                } catch (Exception e) {
                    // Si alguna fila no tiene los datos esperados, la ignoramos
                    continue;
                }
            }
        } finally {
            driver.quit();  // Cerramos el driver después de obtener los datos
        }

        return movies;
    }

    public Movie getFirstMovieTitle() {
        WebDriver driver = createWebDriver();  // Usamos el método de la clase base para crear el WebDriver
        String firstMovieTitle = null;

        try {
            driver.get(baseUrl);  // Cargamos la página

            // Esperamos hasta que la tabla esté visible
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//section[@id='block-48']//table")));

            // Localizamos el primer enlace de película dentro de la tabla
            WebElement firstMovieElement = driver.findElement(By.xpath("//section[@id='block-48']//table//tr[2]//td[2]//a"));

            // Extraemos el título de la película
            firstMovieTitle = firstMovieElement.getText();

        } catch (Exception e) {
            // Manejo de excepciones si no se puede extraer el valor
            System.err.println("Error al obtener el título de la primera película: " + e.getMessage());
        } finally {
            driver.quit();  // Cerramos el driver después de obtener el dato
        }

        System.out.println(firstMovieTitle);  // Mostrar el título en la consola para verificación
        return new Movie(firstMovieTitle, "", "");
    }

}