package day01;

import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.SQLException;

public class MoviesActors {

    public static void main(String[] args) throws SQLException {

        MariaDbDataSource dataSource = new MariaDbDataSource();
        dataSource.setUrl("jdbc:mariadb://localhost:3306/movies_actors?useUnicode=true");
        dataSource.setUser("movies_actors");
        dataSource.setPassword("movies_actors");

        ActorsRepository repository = new ActorsRepository(dataSource);

        repository.clearTable();

        repository.saveActor("Jack Doe");

        repository.getActorsWithPrefix("J").forEach(System.out::println);
    }

}
