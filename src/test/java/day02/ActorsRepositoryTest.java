package day02;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActorsRepositoryTest {

    ActorsRepository repository;

    @org.junit.jupiter.api.BeforeEach
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

        repository = new ActorsRepository(dataSource);

    }

    @Test
    void saveActor() {
        repository.saveActor("John Doe");
        repository.saveActor("Jack Doe");
        repository.saveActor("Jane Doe");
    }

    @Test
    void getActorsNameWithPrefix() {
        repository.saveActor("John Doe");
        repository.saveActor("Jack Doe");
        repository.saveActor("Jane Doe");

        assertEquals(List.of("John Doe"), repository.getActorsNameWithPrefix("Jo"));

        assertEquals(3, repository.getActorsNameWithPrefix("").size());
    }

    @Test
    void clearTable() {
        repository.clearTable();

        assertTrue(repository.getActorsNameWithPrefix("").isEmpty());
    }
}