package be.kdg.oopswetried.controller;

import be.kdg.oopswetried.game.Game;
import be.kdg.oopswetried.model.Level;
import be.kdg.oopswetried.model.Player;
import be.kdg.oopswetried.utils.database.DatabaseManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class GameController {
    private static final Gson gson = new Gson();

    private static final String QUERY_GET = "SELECT * FROM game WHERE player_id = ? AND level_id = ?";
    private static final String QUERY_CREATE = "INSERT INTO game (player_id, level_id, moves_made, duration, saved_vehicles_state, completed) VALUES (?, ?, ?, ?, ?::JSONB, ?);";
    private static final String QUERY_UPDATE = "UPDATE game SET moves_made = ?, duration = ?, saved_vehicles_state = ?::JSONB, completed = ? WHERE id = ?";

    public Game getByPlayerAndLevel(Player player, Level level) {
        Game game = null;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_GET)) {
            statement.setInt(1, player.getId());
            statement.setInt(2, level.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    game = new Game(player, level);
                    game.setId(resultSet.getInt("id"));
                    game.setMovesMade(resultSet.getInt("moves_made"));
                    game.setDuration(resultSet.getObject("duration", BigInteger.class));
                    game.setSavedVehiclesState(deserialize(resultSet.getString("saved_vehicles_state")));
                    game.setCompleted(resultSet.getBoolean("completed"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return game;
    }

    public void create(Game game) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_CREATE, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, game.getPlayer().getId());
            statement.setInt(2, game.getLevel().getId());
            statement.setInt(3, game.getMovesMade());
            statement.setObject(4, game.getDuration());
            statement.setString(5, serialize(game.getSavedVehiclesState()));
            statement.setBoolean(6, game.isCompleted());

            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    game.setId(resultSet.getInt("id"));
                } else {
                    throw new SQLException("Error creating game. No id found!");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("Error updating game. Game cannot be null!");
        }

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_UPDATE)) {
            statement.setInt(1, game.getMovesMade());
            statement.setObject(2, game.getDuration());
            statement.setString(3, serialize(game.getSavedVehiclesState()));
            statement.setBoolean(4, game.isCompleted());
            statement.setInt(5, game.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Map<String, Integer>> deserialize(String serialization) {
        return gson.fromJson(serialization, new TypeToken<Map<String, Map<String, Integer>>>(){}.getType());
    }

    private String serialize(Map<String, Map<String, Integer>> savedVehiclesState) {
        return gson.toJson(savedVehiclesState);
    }
}
