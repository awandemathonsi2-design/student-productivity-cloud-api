package com.studentproductivity.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {

    private static final String HOST = env("DB_HOST", "localhost");
    private static final String PORT = env("DB_PORT", "5432");
    private static final String NAME = env("DB_NAME", "student_productivity");
    private static final String USER = env("DB_USER", "student_app");
    private static final String PASSWORD = env("DB_PASSWORD", "student_password");

    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + NAME;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /** Creates the tasks table if it doesn't exist (needed on a fresh AWS RDS database). */
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

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}