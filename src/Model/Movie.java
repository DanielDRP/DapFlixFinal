package Model;

import java.util.Objects;

public class Movie {
    private final String title;
    private final String info;
    private final String image;

    public Movie(String titulo, String info, String imagenUrl) {
        this.title = titulo;
        this.info = info;
        this.image = imagenUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getInfo() {
        return info;
    }

    public String getImage() {
        return image;
    }

    @Override
    public boolean equals(Object o) {

        if (o == null || getClass() != o.getClass()) return false;

        // Comparar el título de las películas
        Movie movie = (Movie) o;
        return title.equals(movie.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);  // Usar id para el hashCode
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", Information='" + info + '\'' +
                ", imageUrl='" + image + '\'' +
                '}';
    }
}