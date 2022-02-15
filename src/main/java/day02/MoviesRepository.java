package day02;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MoviesRepository {

    DataSource dataSource;

    public MoviesRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveMovie(Movie movie) {
        String sql = "insert into movies(title, release_date) VALUES (?,?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, movie.getTitle());
            stmt.setDate(2, Date.valueOf(movie.getReleaseDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("cant insert", e);
        }
    }

    public List<Movie> findAllMovies() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select * from movies")
        ) {
            List<Movie> result = new ArrayList<>();
            while (rs.next()) {
                long id = rs.getLong("id");
                String title = rs.getString("title");
                LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
                result.add(new Movie(id, title, releaseDate));
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("cant select", e);
        }
    }

}
