package com.studentproductivity.controller;

import com.studentproductivity.model.Task;
import com.studentproductivity.model.TaskRequest;
import com.studentproductivity.repository.TaskRepository;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.Map;

public class TaskController {

    private final TaskRepository repository;

    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    public void registerRoutes(Javalin app) {
        app.post("/tasks", this::createTask);
        app.get("/tasks", this::getAllTasks);
    }

    private void createTask(Context ctx) throws SQLException {
        TaskRequest request = ctx.bodyAsClass(TaskRequest.class);

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            ctx.status(400).json(Map.of("ERROR", "Title is required"));
            return;
        }

        Task task = repository.createTask(request.getTitle().trim(), request.getDescription());
        ctx.status(201).json(task);
    }

    private void getAllTasks(Context ctx) throws SQLException {
        ctx.json(repository.getAllTasks());
    }
}