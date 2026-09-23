package com.studentproductivity.model;

/**
 * Represents a single task as stored in the database.
 * This is the "outgoing" shape of a task: it always has an id (assigned by
 * PostgreSQL) and is what the API returns to clients as JSON.
 */
public class Task {

    private int id;
    private String title;
    private String description;
    private boolean completed;

    /**
     * Creates a Task with all fields set. Used by {@code TaskRepository} to
     * build a Task from a database row.
     *
     * @param id          the database-generated primary key
     * @param title       the task's title
     * @param description an optional longer description, may be null
     * @param completed   whether the task has been marked done
     */
    public Task(int id, String title, String description, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}