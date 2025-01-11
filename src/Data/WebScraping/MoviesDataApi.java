package Data.WebScraping;

import Model.Cinema;
import Model.Movie;

import java.util.*;
import java.util.stream.Collectors;

public class MoviesDataApi {
    private List<Cinema> cinemas = new ArrayList<>();  // Lista de cines
    private final String driver = "/Users/dani/Documents/Uni/DAP/Libraries/chromedriver-mac-x64/chromedriver";
    private List<Movie> yearRanking = new ArrayList<>();
    Movie mostViewedMovie;

    public MoviesDataApi() {
        loadCineData();
    }

    // Método para cargar los cines y sus películas
    private void loadCineData() {
        loadYelmoCinesData("santa-cruz-tenerife/meridiano");
        System.out.println("Yelmo meridiano cargado");
        loadYelmoCinesData("santa-cruz-tenerife/la-villa-de-orotava");
        System.out.println("Yelmo la villa cargado");
        loadMTenerifeCinesData();
        System.out.println("Multicines cargado");
        loadYearRankingData();
        System.out.println("Ranking cargado");
        loadMostViewed();
        System.out.println("Mas vista cargada");
    }

    // Método para cargar datos de Yelmo Cines
    private void loadYelmoCinesData(String city) {
        YelmoScraper yelmo = new YelmoScraper(driver, "https://www.yelmocines.es/cartelera/", city);
        List<Movie> yelmoMovies = yelmo.getSchedule();
        cinemas.add(new Cinema("Yelmo Cines-" + city, yelmoMovies));
    }

    // Método para cargar datos de Multicines Tenerife
    private void loadMTenerifeCinesData() {
        MTenerifeScraper mt = new MTenerifeScraper(driver, "https://multicinestenerife.com/cartelera-tenerife/");
        List<Movie> mtMovies = mt.getSchedule();
        cinemas.add(new Cinema("Multicines Tenerife", mtMovies));
    }

    private void loadYearRankingData() {
        TaquillaESP taquillaESP = new TaquillaESP(driver, "https://www.taquillaespana.es/en/");
        yearRanking = taquillaESP.getYearRanking(); // Suponiendo que TaquillaESP devuelve una lista de Movie
    }

    private void loadMostViewed(){
        TaquillaESP taquillaESP = new TaquillaESP(driver, "https://www.taquillaespana.es/");
        mostViewedMovie = taquillaESP.getFirstMovieTitle(); // Suponiendo que TaquillaESP devuelve una lista de Movie
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

    public List<Movie> getYearRanking(){
        return yearRanking;
    }

    public Movie getMostViewed(){
        return mostViewedMovie;
    }
}
