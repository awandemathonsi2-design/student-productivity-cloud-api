package com.studentproductivity.repository;

import com.studentproductivity.model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskRepositoryTest {

    private final TaskRepository repository = new TaskRepository();

    @Test
    void createTask_storesTaskAndReturnsGeneratedId() throws Exception {
        Task task = repository.createTask("Test: create task", "created by a test");

        assertTrue(task.getId() > 0);
        assertEquals("Test: create task", task.getTitle());
        assertEquals("created by a test", task.getDescription());
        assertFalse(task.isCompleted());

        repository.deleteTask(task.getId());
    }

    @Test
    void getTaskById_returnsTaskWhenItExists() throws Exception {
        Task created = repository.createTask("Test: get by id", null);

        Optional<Task> found = repository.getTaskById(created.getId());

        assertTrue(found.isPresent());
        assertEquals("Test: get by id", found.get().getTitle());

        repository.deleteTask(created.getId());
    }

    @Test
    void getTaskById_returnsEmptyWhenTaskDoesNotExist() throws Exception {
        Optional<Task> found = repository.getTaskById(-1);

        assertTrue(found.isEmpty());
    }

    @Test
    void getAllTasks_includesNewlyCreatedTask() throws Exception {
        Task created = repository.createTask("Test: appears in list", null);

        List<Task> all = repository.getAllTasks();

        assertTrue(all.stream().anyMatch(t -> t.getId() == created.getId()));

        repository.deleteTask(created.getId());
    }

    @Test
    void updateTask_changesFieldsAndReturnsUpdatedTask() throws Exception {
        Task created = repository.createTask("Test: before update", "old description");

        Optional<Task> updated = repository.updateTask(
                created.getId(), "Test: after update", "new description", true);

        assertTrue(updated.isPresent());
        assertEquals("Test: after update", updated.get().getTitle());
        assertEquals("new description", updated.get().getDescription());
        assertTrue(updated.get().isCompleted());

        repository.deleteTask(created.getId());
    }

    @Test
    void updateTask_returnsEmptyWhenTaskDoesNotExist() throws Exception {
        Optional<Task> updated = repository.updateTask(-1, "Nope", null, false);

        assertTrue(updated.isEmpty());
    }

    @Test
    void deleteTask_removesTaskAndReturnsTrue() throws Exception {
        Task created = repository.createTask("Test: to be deleted", null);

        boolean firstDelete = repository.deleteTask(created.getId());
        boolean secondDelete = repository.deleteTask(created.getId());

        assertTrue(firstDelete);
        assertFalse(secondDelete);
        assertTrue(repository.getTaskById(created.getId()).isEmpty());
    }
}