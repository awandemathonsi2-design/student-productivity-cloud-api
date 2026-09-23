package com.studentproductivity.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Holds the database connection settings and creates the {@code tasks} table
 * on startup if it doesn't already exist.
 * <p>
 * Settings are read from environment variables ({@code DB_HOST}, {@code DB_PORT},
 * {@code DB_NAME}, {@code DB_USER}, {@code DB_PASSWORD}), falling back to local
 * Docker defaults if they're not set. This lets the same code run unchanged
 * locally and on AWS, since only the environment variables differ between them.
 */
public class DatabaseConfig {

    private static final String HOST = env("DB_HOST", "localhost");
    private static final String PORT = env("DB_PORT", "5432");
    private static final String NAME = env("DB_NAME", "student_productivity");
    private static final String USER = env("DB_USER", "student_app");
    private static final String PASSWORD = env("DB_PASSWORD", "student_password");

    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + NAME;

    /**
     * Opens a new connection to the configured PostgreSQL database.
     *
     * @return an open JDBC connection
     * @throws SQLException if the database can't be reached or credentials are wrong
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Creates the {@code tasks} table if it doesn't already exist. Safe to call
     * every time the application starts, since {@code CREATE TABLE IF NOT EXISTS}
     * does nothing when the table is already there. This is what lets a brand-new,
     * empty AWS RDS database become usable without a manual setup step.
     */
    public static void initSchema() {
        String sql = """
                CREATE TABLE IF NOT EXISTS tasks (
                    id SERIAL PRIMARY KEY,
                    title VARCHAR(100) NOT NULL,
                    description TEXT,
                    completed BOOLEAN NOT NULL DEFAULT FALSE
                )
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
            System.out.println("Database connection successful, tasks table ready.");
        } catch (SQLException e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }
    }

    /**
     * Reads an environment variable, falling back to a default if it is
     * missing or blank.
     *
     * @param name         the environment variable name
     * @param defaultValue the value to use if the variable isn't set
     * @return the environment variable's value, or the default
     */
    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}