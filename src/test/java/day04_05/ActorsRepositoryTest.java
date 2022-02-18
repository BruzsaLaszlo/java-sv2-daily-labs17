package day04_05;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActorsRepositoryTest {

    ActorsRepository repository;

    Actor jancsi = new Actor("Jancsi");
    Actor juliska = new Actor("Juliska");
    Actor pisti = new Actor("Pisti");
    Actor john = new Actor("John Doe");
    Actor jack = new Actor("Jack Doe");

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

        repository = new ActorsRepository(dataSource);

        repository.saveActor(jancsi, juliska, pisti, john, jack);

    }

    @Test
    void getAllActors() {

        var actual = repository.getAllActor();
        assertEquals(5, actual.size());
        assertEquals("Jancsi", actual.get(0).getName());
        assertEquals("John Doe", actual.get(3).getName());

    }

    @Test
    void getActorsNameWithPrefix() {

        assertEquals(5, repository.getActorsNameWithPrefix("").size());
        assertEquals(List.of("Jancsi", "Jack Doe"), repository.getActorsNameWithPrefix("Ja"));

    }

    @Test
    void findActorByName() {

        var actual = repository.findActorByName("Jancsi");

        assertTrue(actual.isPresent());
        assertEquals("Jancsi", actual.get().getName());

        actual = repository.findActorByName("Jane Doe");
        assertTrue(actual.isEmpty());

    }

    @Test
    @Disabled("be careful")
    void clearTable() {
        repository.clearTable();

        assertTrue(repository.getAllActor().isEmpty());
    }
}