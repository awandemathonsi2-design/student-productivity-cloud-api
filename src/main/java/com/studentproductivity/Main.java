package com.studentproductivity;

import com.studentproductivity.controller.TaskController;
import com.studentproductivity.database.DatabaseConfig;
import com.studentproductivity.repository.TaskRepository;
import io.javalin.Javalin;

/** Application entry point: sets up the database, builds the API, and starts it. */
public class Main {

    /** Starts the application: prepares the database schema, then serves the API on port 7070. */
    public static void main(String[] args) {
        DatabaseConfig.initSchema();
        createApp().start(7070);
        System.out.println("Student Productivity API started on port 7070");
    }

    /**
     * Builds the Javalin app with all routes registered, without starting it.
     * Split out from {@code main()} so tests can start the real API on a
     * random port instead of duplicating route setup.
     *
     * @return a configured but not-yet-started Javalin app.
     */
    public static Javalin createApp() {
        TaskRepository repository = new TaskRepository();
        TaskController controller = new TaskController(repository);

        Javalin app = Javalin.create();
        controller.registerRoutes(app);
        return app;
    }
}