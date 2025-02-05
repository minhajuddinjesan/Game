package be.kdg.oopswetried.controller;

import be.kdg.oopswetried.model.Board;
import be.kdg.oopswetried.model.Level;
import be.kdg.oopswetried.model.Vehicle;
import be.kdg.oopswetried.utils.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LevelController {
    private static final String QUERY_ALL = """
            SELECT l.id AS level_id, l.number, l.difficulty, b.id AS board_id, b.size, v.id AS vehicle_id, v.color, v.length, v.vertical, v.starting_position_x, v.starting_position_y
            FROM level l
            JOIN board b ON l.board_id = b.id
            JOIN board_vehicle bv ON b.id = bv.board_id
            JOIN vehicle v ON bv.vehicle_id = v.id
            """;
    private static final String QUERY_BY_NUMBER = """
            SELECT l.id AS level_id, l.number, l.difficulty, b.id AS board_id, b.size, v.id AS vehicle_id, v.color, v.length, v.vertical, v.starting_position_x, v.starting_position_y
            FROM level l
            JOIN board b ON l.board_id = b.id
            JOIN board_vehicle bv ON b.id = bv.board_id
            JOIN vehicle v ON bv.vehicle_id = v.id
            WHERE l.number = ?
            """;

    public List<Level> getAll() {
        List<Level> levels = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int levelId = resultSet.getInt("level_id");
                int size = resultSet.getInt("size");
                int number = resultSet.getInt("number");
                int boardId = resultSet.getInt("board_id");
                String difficulty = resultSet.getString("difficulty");

                List<Vehicle> vehicles = new ArrayList<>();

                do {
                    Vehicle vehicle = new Vehicle(resultSet.getInt("color"), resultSet.getInt("length"), resultSet.getBoolean("vertical"), resultSet.getInt("starting_position_x"), resultSet.getInt("starting_position_y"));

                    vehicle.setId(resultSet.getInt("vehicle_id"));
                    vehicles.add(vehicle);
                } while (resultSet.next() && resultSet.getInt("level_id") == levelId);

                Board board = new Board(size, vehicles);
                board.setId(boardId);

                Level level = new Level(board, number, Level.Difficulty.fromString(difficulty));
                level.setId(levelId);

                levels.add(level);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return levels;
    }

    public Level getByNumber(int number) {
        Level level = null;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_BY_NUMBER)) {
            statement.setInt(1, number);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int levelId = resultSet.getInt("level_id");
                    int size = resultSet.getInt("size");
                    int boardId = resultSet.getInt("board_id");
                    String difficulty = resultSet.getString("difficulty");

                    List<Vehicle> vehicles = new ArrayList<>();

                    do {
                        Vehicle vehicle = new Vehicle(resultSet.getInt("color"), resultSet.getInt("length"), resultSet.getBoolean("vertical"), resultSet.getInt("starting_position_x"), resultSet.getInt("starting_position_y"));

                        vehicle.setId(resultSet.getInt("vehicle_id"));
                        vehicles.add(vehicle);
                    } while (resultSet.next() && resultSet.getInt("level_id") == levelId);

                    Board board = new Board(size, vehicles);
                    board.setId(boardId);

                    level = new Level(board, number, Level.Difficulty.fromString(difficulty));
                    level.setId(levelId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return level;
    }
}
