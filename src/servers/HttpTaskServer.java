package servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;


import java.io.IOException;
import java.net.InetSocketAddress;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final Gson gson;
    private final TaskManager taskManager;


    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handler);
    }

    public static void main(String[] args) throws IOException {
        final HttpTaskServer server = new HttpTaskServer();
        server.start();
    }

    private void handler(HttpExchange h) {
        try (h) {
            System.out.println("\n/tasks: " + h.getRequestURI());
            final String path = h.getRequestURI().getPath().substring(6);
            final String requestMethod = h.getRequestMethod();
            switch (path) {
                case "" -> {
                    if (!requestMethod.equals("GET")) {
                        System.out.println("/ Ждет GET-запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                    }
                    final String response = gson.toJson(taskManager.getPrioritizedTasksList());
                    sendText(h, response);
                }
                case "/history" -> {
                    if (!requestMethod.equals("GET")) {
                        System.out.println("/history ждет GET-запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                    }
                    final String response = gson.toJson(taskManager.getHistory());
                    sendText(h, response);
                }
                case "/task" -> handleTask(h);
                case "/subtask" -> handleSubtask(h);
                case "/subtask/epic" -> {
                    if (!h.getRequestMethod().equals("GET")) {
                        System.out.println("/subtask/epic ждет GET-запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                    }
                    final String query = h.getRequestURI().getQuery();
                    String idParam = query.substring(3);
                    final int id = Integer.parseInt(idParam);
                    final List<Subtask> subtasks = taskManager.getEpicSubtasks(id);
                    final String response = gson.toJson(subtasks);
                    System.out.println("Получены подзадачи эпика id=" + id);
                    sendText(h, response);
                }
                case "/epic" -> handleEpic(h);
                default -> {
                    System.out.println("Неизвестный запрос: " + h.getRequestURI());
                    h.sendResponseHeaders(404, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleTask(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case "GET" -> {
                if (query == null) {
                    final List<Task> tasks = taskManager.getListAllTask();
                    final String response = gson.toJson(tasks);
                    System.out.println("Получили все задачи");
                    sendText(h, response);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                final Task task = taskManager.getTaskById(id);
                final String response = gson.toJson(task);
                System.out.println("Получили задачу с id = " + id);
                sendText(h, response);
            }
            case "DELETE" -> {
                if (query == null) {
                    taskManager.removeAllTask();
                    System.out.println("Удалили все задачи");
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                taskManager.removeTaskById(id);
                System.out.println("Удалили задачу с id = " + id);
                h.sendResponseHeaders(200, 0);
            }
            case "POST" -> {
                String json = readText(h);
                if (json.isEmpty()) {
                    System.out.println("В теле запроса body с пустой задачей");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                final Task task = gson.fromJson(json, Task.class);
                final Integer id = task.getId();
                if (id != null) {
                    taskManager.updateTask(task);
                    System.out.println("Обновили задачу id = " + id);
                    h.sendResponseHeaders(200, 0);
                } else {
                    taskManager.saveTask(task);
                    System.out.println("Создали задачу id=" + id);
                    final String response = gson.toJson(task);
                    sendText(h, response);
                }
            }
            default -> {
                System.out.println("Неизвестный метод: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);

            }
        }
    }

    private void handleSubtask(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case "GET" -> {
                if (query == null) {
                    final List<Subtask> subtasks = taskManager.getSubtasks();
                    final String response = gson.toJson(subtasks);
                    System.out.println("Получили все подзадачи");
                    sendText(h, response);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                final Subtask subtask = taskManager.getSubtaskById(id);
                final String response = gson.toJson(subtask);
                System.out.println("Получили подзадачу с id = " + id);
                sendText(h, response);
            }
            case "DELETE" -> {
                if (query == null) {
                    taskManager.deleteSubtasks();
                    System.out.println("Удалили все подзадачи");
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                taskManager.removeSubtaskById(id);
                System.out.println("Удалили подзадачу с id = " + id);
                h.sendResponseHeaders(200, 0);
            }
            case "POST" -> {
                String json = readText(h);
                if (json.isEmpty()) {
                    System.out.println("В теле запроса body с пустой подзадачей");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                final Subtask subtask = gson.fromJson(json, Subtask.class);
                final Integer id = subtask.getId();
                if (id != null) {
                    taskManager.updateSubtask(subtask);
                    System.out.println("Обновили подзадачу id = " + id);
                    h.sendResponseHeaders(200, 0);
                } else {
                    taskManager.saveSubtask(subtask);
                    System.out.println("Создали подзадачу id=" + id);
                    final String response = gson.toJson(subtask);
                    sendText(h, response);
                }
            }
            default -> {
                System.out.println("Неизвестный метод: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        }
    }


    private void handleEpic(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case "GET" -> {
                if (query == null) {
                    final List<Epic> epics = taskManager.getListAllEpic();
                    final String response = gson.toJson(epics);
                    System.out.println("Получили все эпики");
                    sendText(h, response);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                final Epic epic = taskManager.getEpicById(id);
                final String response = gson.toJson(epic);
                System.out.println("Получили эпик с id = " + id);
                sendText(h, response);
            }
            case "DELETE" -> {
                if (query == null) {
                    taskManager.removeAllEpic();
                    System.out.println("Удалили все эпики");
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                taskManager.removeEpicById(id);
                System.out.println("Удалили эпик с id = " + id);
                h.sendResponseHeaders(200, 0);
            }
            case "POST" -> {
                String json = readText(h);
                if (json.isEmpty()) {
                    System.out.println("В теле запроса body с пустым эпиком");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                final Epic epic = gson.fromJson(json, Epic.class);
                final Integer id = epic.getId();
                if (id != null) {
                    taskManager.updateEpic(epic);
                    System.out.println("Обновили эпик id = " + id);
                    h.sendResponseHeaders(200, 0);
                } else {
                    taskManager.saveEpic(epic);
                    System.out.println("Создали эпик id=" + id);
                    final String response = gson.toJson(epic);
                    sendText(h, response);
                }
            }
            default -> {
                System.out.println("Неизвестный метод: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }


}
