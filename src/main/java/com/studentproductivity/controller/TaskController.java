package com.studentproductivity.controller;

import com.studentproductivity.model.Task;
import com.studentproductivity.model.TaskRequest;
import com.studentproductivity.repository.TaskRepository;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

/**
 * Defines the HTTP routes for {@code /tasks} and translates between HTTP
 * (status codes, JSON) and the {@link TaskRepository}. Validation of client
 * input happens here, before the repository is ever called.
 */
public class TaskController {

    private final TaskRepository repository;

    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    /** Registers every /tasks route on the given Javalin app. */
    public void registerRoutes(Javalin app) {
        app.post("/tasks", this::createTask);
        app.get("/tasks", this::getAllTasks);
        app.get("/tasks/{id}", this::getTaskById);
        app.delete("/tasks/{id}", this::deleteTask);
        app.put("/tasks/{id}", this::updateTask);
    }

    /** Handles POST /tasks: validates the title, then creates the task. */
    private void createTask(Context ctx) throws SQLException {
        TaskRequest request = ctx.bodyAsClass(TaskRequest.class);

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            ctx.status(400).json(Map.of("ERROR", "Title is required"));
            return;
        }

        Task task = repository.createTask(request.getTitle().trim(), request.getDescription());
        ctx.status(201).json(task);
    }

    /** Handles GET /tasks: returns every task as a JSON array. */
    private void getAllTasks(Context ctx) throws SQLException {
        ctx.json(repository.getAllTasks());
    }

    /** Handles GET /tasks/{id}: returns one task, or 404 if it doesn't exist. */
    private void getTaskById(Context ctx) throws SQLException {
        int id = ctx.pathParamAsClass("id", Integer.class).get();

        Optional<Task> task = repository.getTaskById(id);

        if (task.isPresent()) {
            ctx.json(task.get());
        } else {
            ctx.status(404).json(Map.of("ERROR", "Task " + id + " not found"));
        }
    }

    /**
     * Handles PUT /tasks/{id}. If the request omits {@code completed}, the
     * task's current completed value is kept rather than reset to false.
     */
    private void updateTask(Context ctx) throws SQLException {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        TaskRequest request = ctx.bodyAsClass(TaskRequest.class);

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            ctx.status(400).json(Map.of("ERROR", "Title is required"));
            return;
        }

        Optional<Task> existing = repository.getTaskById(id);
        if (existing.isEmpty()) {
            ctx.status(404).json(Map.of("ERROR", "Task " + id + " not found"));
            return;
        }

        boolean completed = request.getCompleted() != null
                ? request.getCompleted()
                : existing.get().isCompleted();

        Optional<Task> updated = repository.updateTask(
                id,
                request.getTitle().trim(),
                request.getDescription(),
                completed
        );

        if (updated.isPresent()) {
            ctx.json(updated.get());
        } else {
            ctx.status(404).json(Map.of("ERROR", "Task " + id + " not found"));
        }
    }

    /** Handles DELETE /tasks/{id}: deletes the task, or returns 404 if it doesn't exist. */
    private void deleteTask(Context ctx) throws SQLException {
        int id = ctx.pathParamAsClass("id", Integer.class).get();

        boolean deleted = repository.deleteTask(id);

        if (deleted) {
            ctx.status(204);
        } else {
            ctx.status(404).json(Map.of("ERROR", "Task " + id + " not found"));
        }
    }
}