package be.kdg.oopswetried.controller;

import be.kdg.oopswetried.model.Player;
import be.kdg.oopswetried.utils.database.DatabaseManager;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerController {
    private static final String QUERY_ALL = "SELECT id, username, total_moves, total_time_spent FROM player";
    private static final String QUERY_USERNAME = "SELECT id, username, total_moves, total_time_spent FROM player WHERE username = ?";
    private static final String QUERY_CREATE = "INSERT INTO player (username) VALUES (?)";
    private static final String QUERY_UPDATE = "UPDATE player SET total_moves = ?, total_time_spent = ? WHERE id = ?";
    private static final String QUERY_RESET = "DELETE FROM game WHERE player_id = ?; UPDATE player SET total_moves = 0, total_time_spent = 0 WHERE id = ?";

    public List<Player> getAll() {
        List<Player> players = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Player player = new Player(resultSet.getString("username"));
                player.setId(resultSet.getInt("id"));
                player.setTotalMoves(resultSet.getInt("total_moves"));
                player.setTotalTimeSpent(resultSet.getObject("total_time_spent", BigInteger.class));
                players.add(player);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return players;
    }

    public Player getByUsername(String username) {
        Player player = null;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_USERNAME)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    player = new Player(resultSet.getString("username"));
                    player.setId(resultSet.getInt("id"));
                    player.setTotalMoves(resultSet.getInt("total_moves"));
                    player.setTotalTimeSpent(resultSet.getObject("total_time_spent", BigInteger.class));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return player;
    }

    public void create(Player player) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_CREATE, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, player.getUsername());
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    player.setId(resultSet.getInt("id"));
                } else {
                    throw new SQLException("Error creating player. No id found!");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Error updating player. Player cannot be null!");
        }

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_UPDATE)) {
            statement.setInt(1, player.getTotalMoves());
            statement.setObject(2, player.getTotalTimeSpent());
            statement.setInt(3, player.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void reset(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Error updating player. Player cannot be null!");
        }

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_RESET)) {
            statement.setInt(1, player.getId());
            statement.setInt(2, player.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
