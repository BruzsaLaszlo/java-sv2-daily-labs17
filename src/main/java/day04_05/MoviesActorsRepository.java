package day04_05;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MoviesActorsRepository {

    DataSource dataSource;

    public MoviesActorsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Movie movie, List<Actor> actors) {
        new MoviesRepository(dataSource).saveMovie(movie);
        if (isMovieInDatabase(movie.getId())) return;
        new ActorsRepository(dataSource).saveActors(actors);

        String sql = """
                INSERT INTO movies_actors(movie_id, actor_id)
                VALUES (?,?)""";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            for (Actor actor : actors) {
                stmt.setLong(1, movie.getId());
                stmt.setLong(2, actor.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("cant insert", e);
        }
    }

    private boolean isMovieInDatabase(Long id) {
        String sql = """
                SELECT COUNT(*)
                FROM movies_actors
                WHERE movie_id = ?""";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, id);
            return getCount(stmt) > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("cant select", e);
        }
    }

    private long getCount(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        }
    }

    public Map<Movie, List<Actor>> loadMoviesAndActors() {
        String sql = """
                SELECT *
                FROM movies_actors""";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)
        ) {
            Map<Movie, List<Actor>> result = new LinkedHashMap<>();
            MoviesRepository moviesRepository = new MoviesRepository(dataSource);
            ActorsRepository actorsRepository = new ActorsRepository(dataSource);
            while (rs.next()) {
                long movieId = rs.getLong("movie_id");
                long actorId = rs.getLong("actor_id");
                Movie movie = moviesRepository.findMovieById(movieId).orElseThrow();
                Actor actor = actorsRepository.findActorById(actorId).orElseThrow();
                List<Actor> actors = result.computeIfAbsent(movie, m -> new ArrayList<>());
                actors.add(actor);
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("cant select", e);
        }
    }

}
