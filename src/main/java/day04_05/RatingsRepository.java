package day04_05;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RatingsRepository {

    DataSource dataSource;

    public RatingsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertRating(long movieId, List<Integer> ratings) {
        deletePreviousRatings(movieId);
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            insert(conn, movieId, ratings);
        } catch (SQLException e) {
            throw new IllegalStateException("cant insert");
        }
    }


    private void insert(Connection conn, long movieId, List<Integer> ratings) throws SQLException {
        String sql = """
                INSERT INTO ratings (movie_id, rating)
                VALUES (?,?)""";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int rating : ratings) {
                if (rating < 1 || rating > 5) {
                    conn.rollback();
                    return;
                }
                stmt.setLong(1, movieId);
                stmt.setInt(2, rating);
                stmt.executeUpdate();
            }
            conn.commit();
        }
    }

    public Optional<List<Integer>> loadRatings(Movie movie) {
        String sql = """
                             SELECT rating
                             FROM ratings
                             WHERE movie_id =""" + movie.getId();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)
        ) {
            List<Integer> ratings = new ArrayList<>();
            while (rs.next()) {
                ratings.add(rs.getInt("rating"));
            }
            return ratings.isEmpty() ? Optional.empty() : Optional.of(ratings);
        } catch (SQLException e) {
            throw new IllegalStateException("cant select", e);
        }
    }


    private void deletePreviousRatings(long movieId) {
        String sql = "DELETE FROM ratings WHERE movie_id =" + movieId;
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()
        ) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("cant delete", e);
        }
    }

    public double loadAverage(long movieId) {
        String sql = """
                             SELECT AVG(rating)
                             FROM ratings
                             WHERE movie_id =""" + movieId;
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)
        ) {
            rs.next();
            return rs.getDouble(1);
        } catch (SQLException e) {
            throw new IllegalStateException("cant get average", e);
        }
    }
}
