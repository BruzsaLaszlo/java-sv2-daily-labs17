package day04_05;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.time.LocalDate;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.*;

class MoviesRepositoryTest {

    MoviesRepository repository;

    Movie titanic = new Movie("Titanic", LocalDate.of(1997, 9, 4));
    Movie games = new Movie("Hunger games", now().minusYears(12));

    @BeforeEach
    void setUp() throws SQLException {

        MariaDbDataSource dataSource = new MariaDbDataSource();
        dataSource.setUrl("jdbc:mariadb://localhost:3306/movies_actors?useUnicode=true");
        dataSource.setUser("movies_actors");
        dataSource.setPassword("movies_actors");

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .load();

//        flyway.clean();
        flyway.migrate();

        repository = new MoviesRepository(dataSource);

        repository.saveMovies(titanic,games);

    }

    @Test
    void isMovieInDatabase() {

        repository.saveMovie(titanic);
        assertEquals(1, repository.isMovieInDatabase(titanic));

    }

    @Test
    void findMovieById() {

        Long id = titanic.getId();

        var actual = repository.findMovieById(id);

        assertFalse(actual.isEmpty());
        assertEquals(titanic.getTitle(), actual.get().getTitle());
    }

    @Test
    void findAllMovies() {

        var actual = repository.findAllMovies();
        assertEquals(titanic.getTitle(), actual.get(0).getTitle());
        assertEquals(titanic.getReleaseDate(), actual.get(0).getReleaseDate());

    }
}