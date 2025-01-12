package Data.WebScraping;

import Model.Movie;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class YelmoScraper extends BaseScraper implements MovieScraper{

    private final String baseUrl;
    private String city;

    public YelmoScraper(String driverPath, String baseUrl, String city) {
        super(driverPath);
        this.city = city;
        this.baseUrl = baseUrl;
    }

    @Override
    public List<Movie> getSchedule() {
        List<Movie> movies = new ArrayList<>();
        WebDriver driver = createWebDriver();

        try {
            driver.get(baseUrl + city);

            waitForElement(driver, By.cssSelector("article.now__movie"));

            List<WebElement> elementosPeliculas = driver.findElements(By.cssSelector("article.now__movie"));

            for (WebElement elemento : elementosPeliculas) {
                String titulo = elemento.findElement(By.tagName("h3")).getText();
                String clasificacion = elemento.findElement(By.className("clasificacion")).getText();
                String imagenUrl = elemento.findElement(By.tagName("img")).getAttribute("src");
                if (!titulo.isEmpty()) movies.add(new Movie(titulo, clasificacion, imagenUrl));
            }
        } finally {
            driver.quit();
        }

        return movies;
    }
}
