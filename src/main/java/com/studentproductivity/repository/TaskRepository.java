package com.studentproductivity.repository;

import com.studentproductivity.database.DatabaseConfig;
import com.studentproductivity.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskRepository {

    public Task createTask(String title, String description) throws SQLException {

        String sql = """
                INSERT INTO tasks (title, description)
                VALUES (?, ?)
                RETURNING id, title, description, completed
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, title);
            statement.setString(2, description);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return new Task(
                            resultSet.getInt("id"),
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                            resultSet.getBoolean("completed")
                    );
                }

                throw new SQLException("Failed to create task");
            }
        }
    }
}