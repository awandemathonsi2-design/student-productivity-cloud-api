package com.studentproductivity.repository;

import com.studentproductivity.database.DatabaseConfig;
import com.studentproductivity.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles all SQL for the {@code tasks} table. This is the only class in the
 * project that talks directly to the database. Controllers and the rest of
 * the app go through this class instead of writing SQL themselves, which
 * keeps database logic in one place.
 */
public class TaskRepository {

    /** Inserts a new task and returns it with its database-generated id. */
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
                    return mapRowToTask(resultSet);
                }

                throw new SQLException("Failed to create task");
            }
        }
    }

    /** Returns every task in the database, ordered by id. */
    public List<Task> getAllTasks() throws SQLException {

        String sql = """
            SELECT id, title, description, completed
            FROM tasks
            ORDER BY id
            """;

        List<Task> tasks = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                tasks.add(mapRowToTask(resultSet));
            }
        }

        return tasks;
    }

    /** Looks up a single task by its id. */
    public Optional<Task> getTaskById(int id) throws SQLException {

        String sql = """
            SELECT id, title, description, completed
            FROM tasks
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToTask(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    /** Replaces a task's title, description and completed status. */
    public Optional<Task> updateTask(int id, String title, String description, boolean completed)
            throws SQLException {

        String sql = """
            UPDATE tasks
            SET title = ?, description = ?, completed = ?
            WHERE id = ?
            RETURNING id, title, description, completed
            """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, title);
            statement.setString(2, description);
            statement.setBoolean(3, completed);
            statement.setInt(4, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToTask(resultSet));
                }
                return Optional.empty();
            }
        }
    }

    /** Deletes a task by id. */
    public boolean deleteTask(int id) throws SQLException {

        String sql = """
            DELETE FROM tasks
            WHERE id = ?
            """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        }
    }

    /** Converts one row of a {@link ResultSet} into a {@link Task}. */
    private Task mapRowToTask(ResultSet resultSet) throws SQLException {
        return new Task(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                resultSet.getBoolean("completed")
        );
    }
}