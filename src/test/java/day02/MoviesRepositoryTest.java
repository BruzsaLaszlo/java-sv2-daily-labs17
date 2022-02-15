package day02;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.util.List;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MoviesRepositoryTest {

    MoviesRepository repository;

    Movie titanic = new Movie("Titanic", now());

    @BeforeEach
    void setUp() throws SQLException {

        MariaDbDataSource dataSource = new MariaDbDataSource();
        dataSource.setUrl("jdbc:mariadb://localhost:3306/movies_actors?useUnicode=true");
        dataSource.setUser("movies_actors");
        dataSource.setPassword("movies_actors");

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .load();

        flyway.clean();
        flyway.migrate();

        repository = new MoviesRepository(dataSource);

    }

    @Test
    void saveMovie() {
        repository.saveMovie(titanic);
    }

    @Test
    void findAllMovies() {
        repository.saveMovie(titanic);
        var actual = repository.findAllMovies();
        assertEquals("Titanic", actual.get(0).getTitle());
        assertEquals(now(), actual.get(0).getReleaseDate());
    }
}