package Data.WebScraping;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

abstract class BaseScraper {

    private final String driverPath;

    public BaseScraper(String driverPath) {
        this.driverPath = driverPath;
        System.setProperty("webdriver.chrome.driver", driverPath);
    }

    protected WebDriver createWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        return new ChromeDriver(options);
    }

    protected void waitForElement(WebDriver driver, By selector) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(selector));
    }
}
