package Data.WebScraping;

import Model.Cinema;
import Model.Movie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MoviesDataApi {
    private List<Cinema> cinemas = new ArrayList<>(); // Lista de cines
    private final String driver = "/Users/dani/Documents/Uni/DAP/Libraries/chromedriver-mac-x64/chromedriver";
    private List<Movie> yearRanking = new ArrayList<>();
    private List<String[]> yearlyData = new ArrayList<>();
    private Movie mostViewedMovie;
    private Movie mostViewedMovieSpanish;

    public MoviesDataApi() {
        loadCineData();
    }

    // Método principal para cargar datos de todos los cines
    private void loadCineData() {
        loadCineDataByType("yelmo", "https://www.yelmocines.es/cartelera/", "santa-cruz-tenerife/meridiano");
        System.out.println("Yelmo Meridiano cargado");

        loadCineDataByType("yelmo", "https://www.yelmocines.es/cartelera/", "santa-cruz-tenerife/la-villa-de-orotava");
        System.out.println("Yelmo La Villa cargado");

        loadCineDataByType("multicinestenerife", "https://multicinestenerife.com/cartelera-tenerife/", "");
        System.out.println("Multicines Tenerife cargado");

        loadYearRankingData();
        System.out.println("Ranking cargado");

        loadMostViewed();
        System.out.println("Película más vista cargada");

        loadYearRevenueData();
        loadSpanishMostViewed();
    }

    // Método generalizado para cargar datos de cines usando la factoría
    private void loadCineDataByType(String cinemaType, String baseUrl, String city) {
        MovieScraper scraper = ScraperFactory.createScraper(driver, baseUrl, city, cinemaType);
        List<Movie> movies = scraper.getSchedule();
        cinemas.add(new Cinema(cinemaType + (city != null && !city.isEmpty() ? "-" + city : ""), movies));
        System.out.println(movies.size() + " - " + cinemaType);
    }

    private void loadYearRankingData() {
        TaquillaESP taquillaESP = new TaquillaESP(driver, "https://www.taquillaespana.es/en/");
        yearRanking = taquillaESP.getYearRanking(); // Suponiendo que TaquillaESP devuelve una lista de Movie
    }

    private void loadMostViewed(){
        TaquillaESP taquillaESP = new TaquillaESP(driver, "https://www.taquillaespana.es/");
        mostViewedMovie = taquillaESP.getFirstMovieTitle(); // Suponiendo que TaquillaESP devuelve una lista de Movie
    }

    public void loadYearRevenueData() {
        TaquillaESP taquillaESP = new TaquillaESP(driver, "https://www.taquillaespana.es/");
        List<String[]> yearlyData = taquillaESP.getYearRevenueData();

        // Mostrar los resultados en consola
        for (String[] data : yearlyData) {
            System.out.println(data[0] + ": " + data[1]);
        }
        this.yearlyData = yearlyData;
    }

    public void loadSpanishMostViewed(){
        TaquillaESP taquillaESP = new TaquillaESP(driver, "https://www.taquillaespana.es/");
        this.mostViewedMovieSpanish = taquillaESP.getMostViewedSpanishMovie();
    }

    // Obtener el número total de películas únicas
    public int getMoviesCount() {
        return cinemas.stream()
                .flatMap(cinema -> cinema.getMovies().stream()) // Aplanar las películas de todos los cines
                .map(Movie::getTitle) // Obtener solo los títulos
                .collect(Collectors.toSet()) // Eliminar duplicados
                .size();
    }

    // Obtener una lista de todas las películas sin duplicados
    public List<Movie> getAllMoviesList() {
        return cinemas.stream()
                .flatMap(cinema -> cinema.getMovies().stream()) // Aplanar las películas de todos los cines
                .collect(Collectors.toList()); // Convertir el flujo a lista
    }

    // Obtener las películas de un cine específico
    public List<Movie> getMoviesByCinema(String cinemaName) {
        return cinemas.stream()
                .filter(cinema -> cinema.getCinemaName().equals(cinemaName)) // Filtrar por el nombre del cine
                .map(Cinema::getMovies) // Obtener las películas del cine
                .findFirst() // Obtener el primer cine que coincida
                .orElse(Collections.emptyList()); // Si no se encuentra, retornar lista vacía
    }

    // Obtener una lista de nombres de cines
    public List<String> getCinemas() {
        return cinemas.stream()
                .map(Cinema::getCinemaName) // Obtener solo los nombres de los cines
                .collect(Collectors.toList());
    }

    // Obtener una lista de cines con su cantidad de películas
    public List<String> getCinemasWithSize() {
        return cinemas.stream()
                .map(cinema -> cinema.getCinemaName() + " (" + cinema.getMovieCount() + " movies)") // Nombre del cine y el número de películas
                .collect(Collectors.toList());
    }

    public Movie getMostViewedMovieSpanish() {
        return mostViewedMovieSpanish;
    }

    public List<Movie> getYearRanking() {
        return yearRanking;
    }

    public Movie getMostViewed() {
        return mostViewedMovie;
    }

    public List<String[]> getYearRevenueData() {
        return yearlyData;
    }
}
