package day04_05;

import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NonNull
@ToString
public class Movie {

    @Setter
    private Long id;

    @NonNull
    private String title;

    @NonNull
    private LocalDate releaseDate;


    public Movie(String title, LocalDate releaseDate) {
        this.title = title;
        this.releaseDate = releaseDate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
