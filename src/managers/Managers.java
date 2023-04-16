package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import servers.HttpTaskManager;
import servers.LocalDateTimeTypeAdapter;

import java.time.LocalDateTime;

public class Managers {
    private static final String KVSERVER_URL="http://localhost:8078/";

    public static TaskManager getDefault() {
        return new HttpTaskManager(KVSERVER_URL);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        return gsonBuilder.create();
    }
}

