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
    private static final Gson gson = Managers.getGson();
    private final KVClient client;

    public HttpTaskManager(String url) {
        this(url, false);
    }

    public HttpTaskManager(String url, boolean load) {
        super(null);
        client = new KVClient(url);
        if (load) {
            load();
        }
    }

    protected void addTasks(List<? extends Task> tasks) {
        if (tasks==null){
            return;
        }
        for (Task task : tasks) {
            int key = task.getId()+1;
            if (key > idGen) {
                idGen = key;
            }
            TypeTask typeTask = task.getType();
            switch (typeTask) {
                case TASK -> {
                    this.tasks.put(task.getId(), task);
                    prioritizedTasks.add(task);
                }
                case SUBTASK -> {
                    subtasks.put(task.getId(), (Subtask) task);
                    prioritizedTasks.add(task);
                }
                case EPIC -> {
                    epics.put(task.getId(), (Epic) task);
                }
            }
        }
    }

    private void load() {

        List<Task> tasks = gson.fromJson(client.load("tasks"), new TypeToken<ArrayList<Task>>() {
        }.getType());
        addTasks(tasks);

        List<Epic> epics = gson.fromJson(client.load("epics"), new TypeToken<ArrayList<Epic>>() {
        }.getType());
        addTasks(epics);

        List<Subtask> subtasks = gson.fromJson(client.load("subtasks"), new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        addTasks(subtasks);


        List<Integer> history = gson.fromJson(client.load("history"), new TypeToken<ArrayList<Integer>>() {
        }.getType());
        if (history!=null){
            for (Integer taskId : history) {
                if (this.tasks.containsKey(taskId)) {
                    historyManager.add(this.tasks.get(taskId));
                } else if (this.epics.containsKey(taskId)) {
                    historyManager.add(this.epics.get(taskId));
                } else if (this.subtasks.containsKey(taskId)) {
                    historyManager.add(this.subtasks.get(taskId));
                }
            }
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
