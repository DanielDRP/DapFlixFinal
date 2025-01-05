package Model;

public class Movie {
    private final String title;
    private final String info;
    private final String image;

    public Movie(String titulo, String info, String imagenUrl) {
        this.title = titulo;
        this.info = info;
        this.image = imagenUrl;
    }

    public String getTitulo() {
        return title;
    }

    public String getInfo() {
        return info;
    }

    public String getImagenUrl() {
        return image;
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