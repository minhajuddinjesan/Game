package be.kdg.oopswetried.controller;

import be.kdg.oopswetried.utils.DurationUtils;
import be.kdg.oopswetried.utils.database.DatabaseManager;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LeaderboardController {
    public static final String QUERY_ALL = "SELECT p.username, count(g.completed) FILTER (WHERE g.completed = true) as levels_completed, p.total_moves, p.total_time_spent FROM player p LEFT JOIN game g ON p.id = g.player_id GROUP BY p.id ORDER BY levels_completed DESC, p.total_moves ASC, p.total_time_spent ASC LIMIT 25";
    public static final String QUERY_INDIVIDUAL = "SELECT p.username, count(g.completed) FILTER (WHERE g.completed = true) as levels_completed, p.total_moves, p.total_time_spent FROM player p LEFT JOIN game g ON p.id = g.player_id WHERE p.username = ? GROUP BY p.id";

    public static String[][] getLeaderboard() {
        String[][] results = new String[25][4];
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 4; j++) {
               results[i][j] = " ";
            }
        }

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_ALL)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                int i = 0;
                while (resultSet.next()) {
                    results[i][0] = resultSet.getString("username");
                    results[i][1] = resultSet.getString("levels_completed");
                    results[i][2] = resultSet.getString("total_moves");
                    results[i][3] = DurationUtils.formatSecondsToPrettyDuration(new BigInteger(resultSet.getString("total_time_spent")));
                    i++;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    public static String[] getIndividualLeaderboard(String username) {
        String[] result = new String[4];

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_INDIVIDUAL)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result[0] = resultSet.getString("username");
                    result[1] = resultSet.getString("levels_completed");
                    result[2] = resultSet.getString("total_moves");
                    result[3] = DurationUtils.formatSecondsToPrettyDuration(new BigInteger(resultSet.getString("total_time_spent")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
