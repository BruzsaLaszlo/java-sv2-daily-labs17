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
                Optional<Movie> found = findMovie(movie);
                if (found.isEmpty()) {
                    saveMovie(movie, stmt);
                } else {
                    movie.setId(found.get().getId());
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
        try {
            ResultSet rs = select("SELECT * FROM movies");
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
        String sql = "SELECT * FROM movies WHERE id = " + id;
        return getMovie(sql);
    }

    public Optional<Movie> findMovieByTitleAndReleaseDate(String title, LocalDate releaseDate) {
        return findMovie(new Movie(title, releaseDate));
    }

    public Optional<Movie> findMovie(Movie movie) {
        String sql = """
                SELECT *
                FROM movies
                WHERE title LIKE '1'  AND  release_date LIKE '2'"""
                .replace("1", movie.getTitle())
                .replace("2", Date.valueOf(movie.getReleaseDate()).toString());
        log.debug(sql);
        return getMovie(sql);
    }

    private Optional<Movie> getMovie(String sql) {
        try {
            ResultSet rs = select(sql);
            if (!rs.next()) return Optional.empty();
            return Optional.of(getMovieFromResultSet(rs));
        } catch (SQLException e) {
            throw new IllegalStateException("cant select", e);
        }
    }

    private ResultSet select(String sql) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)
        ) {
            return rs;
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

}
