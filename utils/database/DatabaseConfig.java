package be.kdg.oopswetried.utils.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("postgres.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find postgres.properties");
                System.exit(1);
            }

            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getUrl() {
        return properties.getProperty("postgres.url");
    }

    public static String getUsername() {
        return properties.getProperty("postgres.username");
    }

    public static String getPassword() {
        return properties.getProperty("postgres.password");
    }
}
