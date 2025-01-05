package Data.WebScraping;

import Model.Movie;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class MTenerifeScraper extends BaseMovieScraper {

    private final String baseUrl;

    public MTenerifeScraper(String driverPath, String baseUrl) {
        super(driverPath);
        this.baseUrl = baseUrl;
    }

    @Override
    public List<Movie> getSchedule(String ciudad) {
        List<Movie> movies = new ArrayList<>();
        WebDriver driver = createWebDriver();

        try {
            driver.get(baseUrl + ciudad);
            waitForElement(driver, By.cssSelector("div.elementor-widget-container"));
            WebElement scheduleContainer = driver.findElement(By.cssSelector("div.amy-movie-showtimews-2"));
            List<WebElement> movieRows = scheduleContainer.findElements(By.cssSelector("div.amy-movie-showtimews-row"));

            for (WebElement row : movieRows) {
                try {
                    String titulo = row.findElement(By.cssSelector("h3")).getText();
                    String imageUrl = row.findElement(By.cssSelector("img")).getAttribute("src");
                    movies.add(new Movie(titulo, "",imageUrl));

                } catch (Exception e) {
                    // Maneja filas sin datos correctamente
                }
            }
        } finally {
            driver.quit();
        }

        return movies;
    }
}
