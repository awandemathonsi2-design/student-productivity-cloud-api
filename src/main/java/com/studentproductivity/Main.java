package com.studentproductivity;

import com.studentproductivity.database.DatabaseConfig;
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

        Javalin app = Javalin.create()
                .start(7070);

        System.out.println("Student Productivity API started on port 7070");
    }
}