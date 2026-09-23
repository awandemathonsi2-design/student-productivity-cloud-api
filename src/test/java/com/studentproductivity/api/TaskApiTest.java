package com.studentproductivity.repository;

import com.studentproductivity.Main;
import io.javalin.Javalin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TaskApiTest {

    private static Javalin app;
    private static String baseUrl;
    private static final HttpClient client = HttpClient.newHttpClient();

    @BeforeAll
    static void startApi() {
        app = Main.createApp().start(0); // port 0 = "pick any free port"
        baseUrl = "http://localhost:" + app.port();
    }

    @AfterAll
    static void stopApi() {
        app.stop();
    }

    @Test
    void fullCrudLifecycle() throws Exception {
        // CREATE
        HttpResponse<String> created = send("POST", "/tasks",
                "{\"title\":\"Test: API lifecycle\",\"description\":\"created by TaskApiTest\"}");
        assertEquals(201, created.statusCode());
        assertTrue(created.body().contains("\"completed\":false"));
        int id = extractId(created.body());

        // READ one
        HttpResponse<String> one = send("GET", "/tasks/" + id, null);
        assertEquals(200, one.statusCode());
        assertTrue(one.body().contains("Test: API lifecycle"));

        // READ all
        HttpResponse<String> all = send("GET", "/tasks", null);
        assertEquals(200, all.statusCode());
        assertTrue(all.body().contains("\"id\":" + id));

        // UPDATE
        HttpResponse<String> updated = send("PUT", "/tasks/" + id,
                "{\"title\":\"Test: API lifecycle updated\",\"description\":\"changed\",\"completed\":true}");
        assertEquals(200, updated.statusCode());
        assertTrue(updated.body().contains("Test: API lifecycle updated"));
        assertTrue(updated.body().contains("\"completed\":true"));

        // DELETE
        assertEquals(204, send("DELETE", "/tasks/" + id, null).statusCode());
        assertEquals(404, send("GET", "/tasks/" + id, null).statusCode());
    }

    @Test
    void createWithoutTitleReturns400() throws Exception {
        HttpResponse<String> response = send("POST", "/tasks", "{\"description\":\"no title\"}");
        assertEquals(400, response.statusCode());
    }

    @Test
    void getUnknownTaskReturns404() throws Exception {
        assertEquals(404, send("GET", "/tasks/999999999", null).statusCode());
    }

    @Test
    void updateUnknownTaskReturns404() throws Exception {
        assertEquals(404, send("PUT", "/tasks/999999999", "{\"title\":\"Nope\"}").statusCode());
    }

    @Test
    void deleteUnknownTaskReturns404() throws Exception {
        assertEquals(404, send("DELETE", "/tasks/999999999", null).statusCode());
    }

    // ---- helpers ----

    private static HttpResponse<String> send(String method, String path, String jsonBody) throws Exception {
        HttpRequest.BodyPublisher body = jsonBody == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(jsonBody);

        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .method(method, body)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static int extractId(String json) {
        Matcher matcher = Pattern.compile("\"id\"\\s*:\\s*(\\d+)").matcher(json);
        assertTrue(matcher.find(), "no id in response: " + json);
        return Integer.parseInt(matcher.group(1));
    }
}