package com.studentproductivity;

import io.javalin.Javalin;

public class Main {

    public static void main(String[] args) {

        Javalin app = Javalin.create()
                .get("/tasks", ctx -> ctx.json("Task endpoint is working"))
                .start(7070);

        System.out.println("Student Productivity API started on port 7070");
    }
}