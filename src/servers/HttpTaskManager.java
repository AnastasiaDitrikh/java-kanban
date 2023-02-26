package servers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.FileBackedTasksManager;
import managers.Managers;
import tasks.*;
import tasks.TypeTask;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final Gson gson;
    private final KVClient client;

    public HttpTaskManager(int port) {
        this(port, false);
    }

    public HttpTaskManager(int port, boolean load) {
        super(null);
        gson = Managers.getGson();
        client = new KVClient(port);
        if (load) {
            load();
        }
    }

    protected void addTasks(List<? extends Task> tasks) {
       if (tasks==null){
           System.out.println("Cписок задач для добавления пуст");
       }
        for (Task task : tasks) {
            int key=task.getId();
            if (key>idGen){
                idGen=key;
            }
            TypeTask typeTask=task.getType();
            switch (typeTask){
                case TASK -> {
                    this.tasks.put(key, task);
                    prioritizedTasks.add(task);
                }
                case SUBTASK -> {
                    subtasks.put(key,(Subtask) task);
                    prioritizedTasks.add(task);
                }
                case EPIC -> {
                    epics.put(key,(Epic) task);
                }
            }
        }
    }

    private void load() {
        ArrayList<Task> tasks = gson.fromJson(client.load("tasks"), new TypeToken<ArrayList<Task>>() {
        }.getType());
        addTasks(tasks);

        ArrayList<Epic> epics = gson.fromJson(client.load("epics"), new TypeToken<ArrayList<Epic>>() {
        }.getType());
        addTasks(epics);

        ArrayList<Subtask> subtasks = gson.fromJson(client.load("subtasks"), new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        addTasks(subtasks);

        List<Integer> history = gson.fromJson(client.load("history"), new TypeToken<ArrayList<Integer>>() {
        }.getType());
        for (Integer taskId : history) {
            historyManager.add(tasks.get(taskId));
        }
    }

    @Override
    protected void save() {
        String jsonTasks = gson.toJson((new ArrayList<>(tasks.values())));
        client.put("tasks", jsonTasks);
        String jsonSubtasks = gson.toJson((new ArrayList<>(subtasks.values())));
        client.put("subtasks", jsonSubtasks);
        String jsonEpics = gson.toJson((new ArrayList<>(epics.values())));
        client.put("epics", jsonEpics);

        String jsonHistory = gson.toJson(historyManager.getHistory().stream().map(Task::getId).collect(Collectors.toList()));
        client.put("history", jsonHistory);
    }
}
