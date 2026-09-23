package com.studentproductivity.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents the JSON body a client sends to create or update a task.
 * This is the "incoming" shape: unlike {@link Task}, it has no id (the
 * database assigns that), and {@code completed} is a {@link Boolean} rather
 * than a primitive {@code boolean} so a missing field can be told apart from
 * an explicit {@code false} when updating a task.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskRequest {

    private String title;
    private String description;
    private Boolean completed;

    public String getTitle() {
        return title; }

    public void setTitle(String title) {
        this.title = title; }

    public String getDescription() {
        return description; }

    public void setDescription(String description) {
        this.description = description; }

    public Boolean getCompleted() {
        return completed; }

    public void setCompleted(Boolean completed) {
        this.completed = completed; }
}