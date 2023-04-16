package servers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    static HttpTaskServer httpTaskServer;
    static KVServer kvServer;
    static Gson gson;
    TaskManager taskManager;
    Task task1;
    Epic epic1;
    Subtask subtask1Epic1;

    @BeforeAll
    static void beforeAll() throws IOException {
        gson = Managers.getGson();
        kvServer = new KVServer();
        kvServer.start();
    }

    @AfterAll
    static void afterAll() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getDefault();
        httpTaskServer = new HttpTaskServer(taskManager);
        prepareData();
        httpTaskServer.start();
    }

    void prepareData() {
        task1 = new Task("Покупки", "Список покупок",
                LocalDateTime.of(2023, 2, 19, 17, 40), 60);
        taskManager.saveTask(task1);
        epic1 = new Epic("Большая задача1", "Нужно было описать");
        taskManager.saveEpic(epic1);
        subtask1Epic1 = new Subtask("Подзадача1эпик1", "у меня нет фантазии",
                LocalDateTime.of(2023, 2, 18, 17, 40), 60, 2);
        taskManager.saveSubtask(subtask1Epic1);
    }

    @AfterEach
    void tearDown() {
        httpTaskServer.stop();
    }

    @Test
    public void handleTasks() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(task1, tasks.get(0), "Задачи нет");
    }

    @Test
    public void handleSubtasks() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<List<Subtask>>() {
        }.getType());
        assertEquals(subtask1Epic1, subtasks.get(0), "Подзадачи нет");
    }
    @Test
    public void handleEpics() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> epics = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());
        assertNotNull(epics, "Список эпиков пуст");
        assertEquals(epic1, epics.get(0), "Эпика нет");
    }

    @Test
    public void handleSubtaskCheckGet() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=3");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtaskFromServer = gson.fromJson(response.body(), Subtask.class);
        assertEquals(200, response.statusCode(), "Код ответа неверен, ожидалось 200, а получен "+response.statusCode());
        assertEquals(subtask1Epic1, subtaskFromServer, "Подзадачи нет");
    }
    @Test
    public void handleTaskCheckGet() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskFromServer = gson.fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode(), "Код ответа неверен, ожидалось 200, а получен "+response.statusCode());
        assertEquals(task1, taskFromServer, "Задачи нет");
    }

    @Test
    public void handleEpicCheckGet() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/epic?id=2");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicFromServer = gson.fromJson(response.body(), Epic.class);
        assertEquals(200, response.statusCode(), "Код ответа неверен, ожидалось 200, а получен "+response.statusCode());
        assertEquals(epic1, epicFromServer, "Задачи нет");
    }

    @Test
    public void handleTaskCheckPost() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/task?id=1");
        String taskInGson= gson.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskInGson))
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode(), "Код ответа неверен, ожидалось 200, а получен "+response.statusCode());
    }
    @Test
    public void handleSubtaskCheckPost() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=3");
        String subtaskInGson= gson.toJson(subtask1Epic1);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtaskInGson))
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode(), "Код ответа неверен, ожидалось 200, а получен "+response.statusCode());
    }
    @Test
    public void handleEpicCheckPost() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/epic?id=2");
        String epicInGson= gson.toJson(epic1);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epicInGson))
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode(), "Код ответа неверен, ожидалось 200, а получен "+response.statusCode());
    }


    @Test
    public void handleTaskCheckDelete() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode(), "Код ответа неверен, ожидалось 200, а получен "+response.statusCode());
    }
    @Test
    public void handleSubtaskCheckDelete() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/subtask?id=3");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode(), "Код ответа неверен, ожидалось 200, а получен "+response.statusCode());
    }

    @Test
    public void handleEpicCheckDelete() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/epic?id=2");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
        assertEquals(200, response.statusCode(), "Код ответа неверен, ожидалось 200, а получен "+response.statusCode());
    }

    @Test
    public void history() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/history");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertNotNull(history, "История пуста");
        assertEquals(200, response.statusCode(), "Код ответа неверен, ожидалось 200, а получен "+response.statusCode());
    }


}