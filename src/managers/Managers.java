package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import servers.HttpTaskManager;
import servers.LocalDateTimeTypeAdapter;

import java.time.LocalDateTime;

public class Managers {
    private static final String KVSERVER_URL = "http://localhost:8078/";

    /**
     * Получает экземпляр TaskManager по умолчанию.
     *
     * @return Экземпляр TaskManager.
     */
    public static TaskManager getDefault() {
        return new HttpTaskManager(KVSERVER_URL);
    }

    /**
     * Получает экземпляр HistoryManager по умолчанию.
     *
     * @return Экземпляр HistoryManager.
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    /**
     * Получает экземпляр Gson с настройками по умолчанию.
     * Регистрирует адаптер типа LocalDateTime для сериализации/десериализации объектов LocalDateTime.
     *
     * @return Экземпляр Gson с настройками.
     */
    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        return gsonBuilder.create();
    }
}

