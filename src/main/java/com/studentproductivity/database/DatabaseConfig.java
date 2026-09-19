package com.studentproductivity.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/student_productivity";

    private static final String USER = "student_app";

    private static final String PASSWORD = "student_password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}