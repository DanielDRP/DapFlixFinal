package Data.WebScraping;

import Model.Movie;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /*public List<Movie> getHistoricalData() {
        List<Movie> movies = new ArrayList<>();
        WebDriver driver = createWebDriver(); // Usamos el método de la clase base para crear el WebDriver

        try {
            driver.get("https://www.taquillaespana.es/"); // Cargamos la página

            // Esperamos hasta que la tabla específica esté visible
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h2[contains(text(), 'MÁS TAQUILLERAS DE LA HISTORIA')]/following-sibling::table/tbody")));

            // Localizamos la tabla específica (usamos el encabezado como referencia)
            WebElement table = driver.findElement(
                    By.xpath("//h2[contains(text(), 'MÁS TAQUILLERAS DE LA HISTORIA')]/following-sibling::table/tbody"));

            // Localizamos todas las filas de esa tabla
            List<WebElement> rows = table.findElements(By.tagName("tr"));

            for (WebElement row : rows) {
                try {
                    // Verificamos que la fila no esté vacía
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    if (cells.size() < 3) {
                        continue; // Saltamos las filas que no tienen al menos 3 celdas
                    }

                    // Extraemos el ranking, título y recaudación
                    String rankingText = cells.get(0).getText().trim();
                    String titleText = cells.get(1).getText().trim();
                    String revenueText = cells.get(2).getText()
                            .replace("€", "")
                            .replace(".", "") // Reemplaza puntos para evitar errores en los valores numéricos
                            .replace(",", ".") // Ajuste por formato europeo (coma como separador decimal)
                            .trim();

                    // Parseamos los valores extraídos
                    int ranking = Integer.parseInt(rankingText);
                    double revenue = Double.parseDouble(revenueText);

                    // Creamos un objeto Movie y lo añadimos a la lista
                    Movie movie = new Movie(titleText, String.valueOf(revenue), String.valueOf(ranking));
                    movies.add(movie);
                } catch (NumberFormatException nfe) {
                    System.err.println("Error al procesar datos numéricos en la fila: " + row.getText());
                } catch (Exception e) {
                    System.err.println("Error procesando una fila: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener las películas de 'Más taquilleras de la historia': " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit(); // Cerramos el driver después de procesar
            }
        }

        return movies;
    }*/

    public List<String[]> getYearRevenueData() {
        List<String[]> yearlyData = new ArrayList<>();
        WebDriver driver = createWebDriver(); // Creamos el WebDriver usando el método base

        try {
            driver.get(baseUrl); // Navegamos a la URL base

            // Esperamos hasta que la tabla específica esté visible
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tbody")));

            // Localizamos las filas de la tabla en el tbody
            List<WebElement> rows = driver.findElements(By.xpath("//tbody/tr"));

            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));

                if (cells.size() == 2) {
                    String label = cells.get(0).getText().trim(); // Primera celda
                    String value = cells.get(1).getText().trim(); // Segunda celda

                    // Añadimos solo las filas que necesitamos
                    if (label.equals("Recaudación total") || label.equals("Cine español")) {
                        yearlyData.add(new String[]{label, value});
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener los datos anuales específicos: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit(); // Cerramos el WebDriver
            }
        }

        return yearlyData;
    }


}