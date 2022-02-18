package day04_05;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.*;

class MoviesRatingsServiceTest {

    MoviesRatingsService service;

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

        service = new MoviesRatingsService(dataSource);

    }

    @Test
    void addRatings() {

        service.addRatings(titanic, List.of(1, 2, 1));

        Movie notExist = new Movie("NoTitles", now());
        var ratings =  List.of(1);
        assertThrows(IllegalArgumentException.class, () -> service.addRatings(notExist,ratings));

    }

    @Test
    void getRatings() {

        List<Integer> actual = service.getRatings(titanic);
        var expected = List.of(1, 2, 1);
        assertEquals(expected,actual);

        Movie notExist = new Movie("NoTitles", now());
        assertThrows(IllegalArgumentException.class, () -> service.getRatings(notExist));

        assertThrows(IllegalArgumentException.class, () -> service.getRatings(games));

    }

    @Test
    void getAverageRating() {

        assertEquals(1.33, service.getAverageRating(titanic), 0.01);

        assertEquals(0d,service.getAverageRating(games));

    }
}