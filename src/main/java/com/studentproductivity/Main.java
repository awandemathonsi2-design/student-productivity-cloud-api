package com.studentproductivity;

import com.studentproductivity.controller.TaskController;
import com.studentproductivity.database.DatabaseConfig;
import com.studentproductivity.repository.TaskRepository;
import io.javalin.Javalin;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) {

        try (Connection connection = DatabaseConfig.getConnection()) {
            System.out.println("Database connection successful!");
        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }

        TaskRepository repository = new TaskRepository();
        TaskController controller = new TaskController(repository);

        Javalin app = Javalin.create();
        controller.registerRoutes(app);
        app.start(7070);

        System.out.println("Student Productivity API started on port 7070");
    }
}