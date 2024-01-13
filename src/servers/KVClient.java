package servers;

import managers.exceptions.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Класс KVClient представляет клиент для работы с удаленным сервисом Key-Value.
 */
public class KVClient {
    private final String url;
    private final String apiToken;

    public KVClient(String url) {
        this.url = url;
        this.apiToken = register(url);
    }

    /**
     * Загружает данные по указанному ключу.
     *
     * @param key ключ, по которому требуется загрузить данные.
     * @return загруженные данные в виде строки.
     * @throws ManagerSaveException если возникает ошибка во время загрузки данных.
     */
    public String load(String key) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Can't do save request, status code: " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Can't do save request", e);
        }
    }

    /**
     * Сохраняет данные по указанному ключу.
     *
     * @param key   ключ, по которому требуется сохранить данные.
     * @param value сохраняемые данные в виде строки.
     * @throws ManagerSaveException если возникает ошибка во время сохранения данных.
     */
    public void put(String key, String value) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken))
                    .POST(HttpRequest.BodyPublishers.ofString(value))
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Can't do save request, status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Can't do save request", e);
        }
    }

    /**
     * Метод register(String url) выполняет регистрацию клиента и получение API токена.
     *
     * @param url URL удаленного сервиса Key-Value
     * @return API токен
     * @throws ManagerSaveException если возникает ошибка во время сохранения данных.
     */
    private String register(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "register"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Can't do save request, status code: " + response.statusCode());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Can't do save request", e);
        }
    }
}