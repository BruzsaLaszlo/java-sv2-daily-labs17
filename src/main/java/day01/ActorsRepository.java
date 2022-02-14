package day01;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActorsRepository {

    DataSource dataSource;

    public ActorsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveActor(String name) {
        String sql = "insert into actors(actor_name) values (?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("cant update", e);
        }
    }

    public List<String> getActorsWithPrefix(String prefix) {
        String sql = "select * from actors where actor_name like ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, prefix + '%');
            return executeQueryAndGetActors(stmt);
        } catch (SQLException e) {
            throw new IllegalStateException("can select", e);
        }
    }

    private List<String> executeQueryAndGetActors(PreparedStatement stmt) throws SQLException {
        var result = new ArrayList<String>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(rs.getString("actor_name"));
            }
        }
        return result;
    }

    public void clearTable() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()
        ) {
            stmt.executeQuery("DELETE FROM actors");
        } catch (SQLException e) {
            throw new IllegalStateException("cant delete", e);
        }
    }
}
