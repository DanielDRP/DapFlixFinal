package Data.WebScraping;

public class ScraperFactory {

    public static MovieScraper createScraper(String driverPath, String baseUrl, String city, String cinemaType) {
        switch (cinemaType.toLowerCase()) {
            case "yelmo":
                return new YelmoScraper(driverPath, baseUrl, city);
            case "multicinestenerife":
                return new MTenerifeScraper(driverPath,baseUrl);
            default:
                throw new IllegalArgumentException("Unknown cinema type: " + cinemaType);
        }
    }
}
