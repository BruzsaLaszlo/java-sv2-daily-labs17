package day02;

import java.time.LocalDate;

public class Movie {

    private Long id;
    private String title;
    private LocalDate releaseDate;

    public Movie(Long id, String title, LocalDate releaseDate) {
        this(title, releaseDate);
        this.id = id;
    }

    public Movie(String title, LocalDate releaseDate) {
        this.title = title;
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
