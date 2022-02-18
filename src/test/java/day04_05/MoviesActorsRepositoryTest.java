package day04_05;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MoviesActorsRepositoryTest {

    MoviesActorsRepository repository;

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

        repository = new MoviesActorsRepository(dataSource);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void save() {

        Movie titanic = new Movie("Titanic", LocalDate.of(1997, 9, 4));
        Movie games = new Movie("Hunger games", now().minusYears(12));

        Actor jancsi = new Actor("Jancsi");
        Actor juliska = new Actor("Juliska");
        Actor pisti = new Actor("Pisti");
        Actor john = new Actor("John Doe");
        Actor jack = new Actor("Jack Doe");

        repository.save(titanic, List.of(jancsi, juliska, pisti));
        repository.save(games, List.of(pisti, john, jack));

        var actual = repository.loadMoviesAndActors();

        assertEquals(2, actual.keySet().size());
        assertEquals(5, actual.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .collect(toSet()).size());

    }

}