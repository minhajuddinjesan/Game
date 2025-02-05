package be.kdg.oopswetried.utils.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    public static Connection getConnection() throws SQLException {

        try {
            String jdbcUrl = DatabaseConfig.getUrl();
            String user = DatabaseConfig.getUsername();
            String password = DatabaseConfig.getPassword();

            return DriverManager.getConnection(jdbcUrl, user, password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
