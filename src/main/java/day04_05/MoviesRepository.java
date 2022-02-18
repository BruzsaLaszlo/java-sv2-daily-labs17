package day04_05;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.util.Collections.singletonList;

@Log4j2
public class MoviesRepository {

    DataSource dataSource;

    public MoviesRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveMovie(Movie movie) {
        saveMovies(singletonList(movie));
    }

    public void saveMovies(Movie... movies) {
        saveMovies(List.of(movies));
    }

    public void saveMovies(List<Movie> movies) {
        String sql = "INSERT INTO movies(title, release_date) VALUES (?,?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, RETURN_GENERATED_KEYS)
        ) {
            for (Movie movie : movies) {
                if (isMovieInDatabase(movie) == 0) {
                    saveMovie(movie, stmt);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("cant insert", e);
        }
    }

    private void saveMovie(@NonNull Movie movie, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, movie.getTitle());
        stmt.setDate(2, Date.valueOf(movie.getReleaseDate()));
        stmt.executeUpdate();
        movie.setId(getKey(stmt));
    }

    public List<Movie> findAllMovies() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select * from movies")
        ) {
            List<Movie> result = new ArrayList<>();
            while (rs.next()) {
                result.add(getMovieFromResultSet(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("cant select", e);
        }
    }

    public Optional<Movie> findMovieById(long id) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select * from movies where id =" + id)
        ) {
            if (!rs.next()) return Optional.empty();
            return Optional.of(getMovieFromResultSet(rs));
        } catch (SQLException e) {
            throw new IllegalStateException("cant select", e);
        }
    }

    private Movie getMovieFromResultSet(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String title = rs.getString("title");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        return new Movie(id, title, releaseDate);
    }

    private Long getKey(PreparedStatement stmt) {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new IllegalStateException("no generated key", e);
        }
    }

    public int isMovieInDatabase(Movie movie) {
        String sql = """
                SELECT *
                FROM movies
                WHERE title = ?  AND  release_date = ?""";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, movie.getTitle());
            stmt.setDate(2, Date.valueOf(movie.getReleaseDate()));
            return isMovieResult(movie,stmt);

        } catch (SQLException e) {
            throw new IllegalStateException("cant select", e);
        }
    }

    private int isMovieResult(Movie movie, PreparedStatement stmt) throws SQLException {
        stmt.execute();
        try (ResultSet rs = stmt.getResultSet()) {
            if (rs != null && rs.last()) {
                log.warn("movie already in database");
                movie.setId(rs.getLong("id"));
                return rs.getRow();
            }
            return 0;
        }
    }
}
