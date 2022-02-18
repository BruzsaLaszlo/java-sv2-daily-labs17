package day04_05;

import lombok.extern.log4j.Log4j2;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

@Log4j2
public class ActorsRepository {

    DataSource dataSource;

    public ActorsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveActor(Actor... actor) {
        saveActors(List.of(actor));
    }

    public void saveActors(List<Actor> actors) {
        String sql = "INSERT INTO actors(actor_name) VALUES (?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, RETURN_GENERATED_KEYS)
        ) {
            for (Actor actor : actors) {
                if (isActorInDatabase(actor) == 0) {
                    saveActor(actor, stmt);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("cant insert", e);
        }
    }

    private void saveActor(Actor actor, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, actor.getName());
        stmt.executeUpdate();
        actor.setId(getGeneratedId(stmt));
    }

    public Optional<Actor> findActorById(long id) {
        String sql = """
                SELECT *
                FROM actors
                WHERE id =?""";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, id);
            return getSingleActor(stmt);
        } catch (SQLException e) {
            throw new IllegalStateException("cant select", e);
        }
    }

    public Optional<Actor> findActorByName(String name) {
        String sql = """
                SELECT *
                FROM actors
                WHERE actor_name like ?""";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, name);
            return getSingleActor(stmt);
        } catch (SQLException e) {
            throw new IllegalStateException("cant select");
        }
    }

    private int isActorInDatabase(Actor actor) {
        String sql = """
                SELECT *
                FROM actors
                WHERE actor_name ='?'"""
                .replace("?", actor.getName());
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)
        ) {
            if (rs != null && rs.last()) {
                log.warn("actor already in database");
                actor.setId(rs.getLong("id"));
                return rs.getRow();
            }
            return 0;
        } catch (SQLException e) {
            throw new IllegalStateException("cant select", e);
        }
    }

    private Long getGeneratedId(PreparedStatement stmt) {
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            rs.absolute(1);
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new IllegalStateException("no generated key", e);
        }
    }

    public List<Actor> getAllActor() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM actors")
        ) {
            List<Actor> actors = new ArrayList<>();
            while (rs.next()) {
                actors.add(getActor(rs));
            }
            return actors;
        } catch (SQLException e) {
            throw new IllegalStateException("can select", e);
        }
    }


    private Optional<Actor> getSingleActor(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return Optional.of(getActor(rs));
            }
            return Optional.empty();
        }
    }

    private Actor getActor(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String name = rs.getString("actor_name");
        return new Actor(id, name);
    }

    public List<String> getActorsNameWithPrefix(String prefix) {
        String sql = "select * from actors where actor_name like ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, prefix + '%');
            return executeQueryAndGetActorsName(stmt);
        } catch (SQLException e) {
            throw new IllegalStateException("can select", e);
        }
    }

    private List<String> executeQueryAndGetActorsName(PreparedStatement stmt) throws SQLException {
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
