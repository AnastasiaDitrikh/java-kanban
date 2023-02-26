package managers;

import managers.exceptions.ManagerSaveException;
import tasks.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;


public class FileBackedTasksManager extends InMemoryTaskManager {


    private String dataHistoryTask = "src/dataHistoryTask.csv";
    private static final String HEADER = "id,type,name,status,description,start,duration,epic";

    public static Map<Integer, Task> getAllTasksMap() {
        return allTasksMap;
    }

    protected static Map<Integer, Task> allTasksMap = new HashMap<>();

    public FileBackedTasksManager(String dataHistoryTask) {
        this.dataHistoryTask = dataHistoryTask;
    }


    public static FileBackedTasksManager loadFromFile(String path) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(path);
        List<String> content = readFileContentsOrNull(path); //  содержимое файла "src/dataHistoryTask.csv"
        String historyLine;
        List<Integer> historyFromString = Collections.emptyList();
        if (content.size() < 3) {
            historyLine = null;
        } else {
            historyLine = content.get(content.size() - 1);
            historyFromString = historyFromString(historyLine);
        }

        for (int i = 1; i < content.size() - 2; i++) {
            String line = content.get(i);
            String[] parts = line.split(",");
            Integer id = Integer.parseInt(parts[0]);
            TypeTask type = TypeTask.valueOf(parts[1]);
            String name = parts[2];
            Status status = Status.valueOf(parts[3]);
            String description = parts[4];
            LocalDateTime startTime = null;
            if (!(parts[5].equals("null"))) {
                startTime = LocalDateTime.parse(parts[5]);
            }

            long duration = Long.parseLong(parts[6]);

            switch (type) {
                case TASK:
                    Task task = new Task(id, name, description, status, startTime, duration);
                    fileBackedTasksManager.tasks.put(id, task);
                    fileBackedTasksManager.prioritizedTasks.add(task);
                    allTasksMap.put(id, task);
                    break;
                case EPIC:
                    Epic epic = new Epic(id, name, description, status, startTime, duration);
                    fileBackedTasksManager.epics.put(id, epic);
                    allTasksMap.put(id, epic);
                    break;
                case SUBTASK:
                    Integer epicId = Integer.parseInt(parts[7]);
                    Subtask subtask = new Subtask(id, name, description, status, epicId, startTime, duration);
                    fileBackedTasksManager.subtasks.put(id, subtask);
                    fileBackedTasksManager.prioritizedTasks.add(subtask);
                    Epic epicOfSubtask = fileBackedTasksManager.epics.get(epicId);
                    fileBackedTasksManager.epics.get(epicId).addSubtask(id);
                    fileBackedTasksManager.setEndTimeEpic(epicOfSubtask);
                    allTasksMap.put(id, subtask);
                    break;
                default:
                    throw new IllegalArgumentException();
            }

        }
        if (!historyFromString.isEmpty()) {
            for (Integer oneId : historyFromString) {
                if (allTasksMap.containsKey(oneId)) {
                    fileBackedTasksManager.historyManager.add(allTasksMap.get(oneId));
                }
            }
        }

        int idCounter = 1;
        for (Integer id : allTasksMap.keySet()) {
            if (idCounter<id){
                idCounter=id;
            }
        }
        fileBackedTasksManager.setIdGen(idCounter);
        return fileBackedTasksManager;
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> historyFromStringList = new ArrayList<>();
        if (!value.isBlank()) {
            String[] valueSplitted = value.split(",");
            for (String num : valueSplitted) {
                historyFromStringList.add(Integer.parseInt(num));
            }
        }
        return historyFromStringList;
    }


    protected void save() {
        final String historyInString = historyToString(historyManager);
        try (FileWriter fileWriter = new FileWriter(String.valueOf(dataHistoryTask))) {
            fileWriter.write(HEADER);
            fileWriter.write("\n");
            for (Task task : tasks.values()) {
                fileWriter.write(task.toString());
                fileWriter.write("\n");
            }
            for (Epic epic : epics.values()) {
                fileWriter.write(epic.toString());
                fileWriter.write("\n");
            }
            for (Subtask subtask : subtasks.values()) {
                fileWriter.write(subtask.toString());
                fileWriter.write("\n");
            }
            fileWriter.write("\n");
            fileWriter.write(historyInString);
        } catch (IOException e) {
            throw new ManagerSaveException("Данные не сохранены");
        }
    }


    private static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder sb = new StringBuilder();
        String firstDelim = "";
        for (Task task : history) {
            sb.append(firstDelim);
            sb.append(task.getId());
            firstDelim = ",";
        }
        return sb.toString();
    }

    private static List<String> readFileContentsOrNull(String path) {
        try {
            return Files.readAllLines(Path.of(path));
        } catch (IOException e) {
            System.out.println("Невозможно прочитать файл.");
            return new ArrayList<>();
        }
    }


    @Override
    public void saveTask(Task task) {
        super.saveTask(task);
        save();
    }


    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    @Override
    public Task getTaskById(Integer id) {
        Task taskById = super.getTaskById(id);
        save();
        return taskById;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTaskById(Integer id) {
        super.removeTaskById(id);
        save();
    }


    //Epic
    @Override
    public void saveEpic(Epic epic) {
        super.saveEpic(epic);
        save();
    }


    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }


    @Override
    public Epic getEpicById(Integer id) {
        Epic epicById = super.getEpicById(id);
        save();
        return epicById;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }


    @Override
    public void removeEpicById(Integer id) {
        super.removeEpicById(id);
        save();
    }

    //Subtask
    @Override
    public void saveSubtask(Subtask subtask) {
        super.saveSubtask(subtask);
        save();
    }


    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtaskById = super.getSubtaskById(id);
        save();
        return subtaskById;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void removeSubtaskById(Integer id) {
        super.removeSubtaskById(id);
        save();
    }


    public static void main(String[] args) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager("src/dataHistoryTask.csv");
        Task task1 = new Task( "Покупки", "Список покупок", LocalDateTime.of(2023, 2, 19, 17, 40), 60);
        fileBackedTasksManager.saveTask(task1);
        Task task2 = new Task( "Тренировка", "Программа", LocalDateTime.of(2023, 2, 9, 11, 30), 1000);
        fileBackedTasksManager.saveTask(task2);
        Epic epic1 = new Epic("Большая задача1", "Нужно было описать");
        fileBackedTasksManager.saveEpic(epic1);
        Subtask subtask1Epic1 = new Subtask("Подзадача1эпик1", "у меня нет фантазии", LocalDateTime.of(2023, 2, 18, 17, 40), 60, 3);
        Subtask subtask2Epic1 = new Subtask("Подзадача2эпик1", "у меня нет фантазии совсем", LocalDateTime.of(2023, 4, 19, 17, 40), 60, 3);
        fileBackedTasksManager.saveSubtask(subtask1Epic1);
        fileBackedTasksManager.saveSubtask(subtask2Epic1);
        fileBackedTasksManager.getTaskById(task1.getId());
        fileBackedTasksManager.getTaskById(task2.getId());
        fileBackedTasksManager.getEpicById(epic1.getId());
        fileBackedTasksManager.getSubtaskById(subtask2Epic1.getId());
        fileBackedTasksManager.getSubtaskById(subtask1Epic1.getId());
        fileBackedTasksManager.getTaskById(task1.getId());
        fileBackedTasksManager.removeTaskById(task2.getId());
        System.out.println(fileBackedTasksManager.getHistory());
        //fileBackedTasksManager.loadFromFile("src/dataHistoryTask.csv");
    }
}

