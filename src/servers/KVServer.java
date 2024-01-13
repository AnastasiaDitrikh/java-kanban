package servers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Класс KVServer представляет сервер для работы с удаленным сервисом Key-Value.
 */
public class KVServer {
    public static final int PORT = 8078;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    /**
     * Метод main() используется для запуска сервера.
     *
     * @throws IOException возникает в случае ошибки создания KVServer
     */
    public static void main(String[] args) throws IOException {
        new KVServer().start();
    }


    /**
     * Метод load() выполняет загрузку значения по ключу.
     *
     * @param h объект HttpExchange для обработки запроса
     * @throws IOException возникает в случае ошибки чтения или отправки в HttpExchange
     */
    private void load(HttpExchange h) throws IOException {
        try (h) {
            System.out.println("\n/load");
            if (!hasAuth(h)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /load/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = data.get(key);
                if (value == null) {
                    System.out.println("Данные по ключу " + key + " временно отсутствуют!");
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                sendText(h, value);
                System.out.println("Значение для ключа " + key + " успешно отправлено в ответ на запрос!");
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    /**
     * Метод save() выполняет сохранение значения по ключу.
     *
     * @param h объект HttpExchange для обработки запроса
     * @throws IOException возникает в случае ошибки чтения или записи в HttpExchange
     */
    private void save(HttpExchange h) throws IOException {
        try (h) {
            System.out.println("\n/save");
            if (!hasAuth(h)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    /**
     * Метод register() выполняет регистрацию клиента
     *
     * @param h объект HttpExchange для обработки запроса
     * @throws IOException возникает в случае ошибки чтения или записи в HttpExchange
     */
    private void register(HttpExchange h) throws IOException {
        try (h) {
            System.out.println("\n/register");
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, apiToken);
            } else {
                System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    /**
     * Метод start() запускает сервер на указанном порту.
     */
    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    /**
     * Метод stop() останавливает сервер на указанном порту.
     */
    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    /**
     * Генерирует уникальный API токен.
     */
    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    /**
     * Метод hasAuth(HttpExchange h) проверяет наличие аутентификации в запросе.
     *
     * @param h объект HttpExchange для обработки запроса
     * @return true, если аутентификация присутствует, иначе false
     */
    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    /**
     * Метод readText(HttpExchange h) считывает текст из тела запроса.
     *
     * @param h объект HttpExchange для обработки запроса
     * @return считанный текст из тела запроса
     * @throws IOException возникает в случае ошибки чтения из HttpExchange
     */
    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    /**
     * Метод sendText(HttpExchange h, String text) отправляет текст в ответ на запрос.
     *
     * @param h    объект HttpExchange для обработки запроса
     * @param text текст для отправки
     * @throws IOException возникает в случае ошибки записи в HttpExchange
     */
    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}