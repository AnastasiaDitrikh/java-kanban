package service;

import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static model.Status.*;


public class FileBackedTasksManager extends InMemoryTaskManager {


    static String dataHistoryTask = "src/dataHistoryTask.csv";

    public FileBackedTasksManager(String dataHistoryTask) {
        this.dataHistoryTask = dataHistoryTask;
    }

    static List<Integer> historyFromString(String value) {
        String[] valueSplitted = value.split(",");
        List<Integer> historyFromStringList = new ArrayList<>();
        for (String num : valueSplitted) {
            historyFromStringList.add(Integer.parseInt(num));
        }
        return historyFromStringList;
    }


    private void save() {
        final String historyInString = historyToString(getHistoryManager());
        try (FileWriter fileWriter = new FileWriter(String.valueOf(dataHistoryTask))) {
            fileWriter.write("id,type,name,status,description,epic");
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


    static String historyToString(HistoryManager manager) {
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


    static FileBackedTasksManager loadFromFile(String path) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(path);
        String content = readFileContentsOrNull(path); //  содержимое файла "src/dataHistoryTask.csv"
        String[] lines = content.split("\r?\n");
        String historyLine = lines[lines.length - 1];
        List<Integer> historyFromString = historyFromString(historyLine);
        HashMap<Integer, Task> allTasksMap = new HashMap<>();

        for (int i = 1; i < lines.length - 2; i++) {
            String line = lines[i];
            String[] parts = line.split(",");
            Integer id = Integer.parseInt(parts[0]);
            TypeTask type = TypeTask.valueOf(parts[1]);
            String name = parts[2];
            Status status = Status.valueOf(parts[3]);
            String description = parts[4];

            switch (type) {
                case TASK:
                    Task task = new Task(id, type, name, description, status);
                    fileBackedTasksManager.tasks.put(id, task);
                    allTasksMap.put(id, task);
                    break;
                case EPIC:
                    Epic epic = new Epic(id, type, name, description, status);
                    fileBackedTasksManager.epics.put(id, epic);
                    allTasksMap.put(id, epic);
                    break;
                case SUBTASK:
                    Integer epicId = Integer.parseInt(parts[5]);
                    Subtask subtask = new Subtask(id, type, name, description, status, epicId);
                    fileBackedTasksManager.subtasks.put(id, subtask);
                    allTasksMap.put(id, subtask);
                    break;
                default:
                    throw new IllegalArgumentException();
            }

        }
        for (Integer oneId : historyFromString) {
            if (allTasksMap.containsKey(oneId)) {
                fileBackedTasksManager.add(allTasksMap.get(oneId));
            }
        }
        return fileBackedTasksManager;
    }


    private static String readFileContentsOrNull(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            System.out.println("Невозможно прочитать файл.");
            return null;
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = super.getHistory();
        save();
        return history;
    }

    @Override
    public void add(Task task) {
        super.add(task);
    }

    @Override
    public void saveTask(Task task) {
        super.saveTask(task);
        save();
    }

    @Override
    public List<Task> getListAllTask() {
        List<Task> getListAllTask = super.getListAllTask();
        save();
        return getListAllTask;
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
    public List<Epic> getListAllEpic() {
        List<Epic> listAllEpic = super.getListAllEpic();
        save();
        return listAllEpic;
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
    public void saveSubtask(Integer epicId, Subtask subtask) {
        super.saveSubtask(epicId, subtask);
        save();
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> listSubtasks = super.getSubtasks();
        save();
        return listSubtasks;
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

    @Override
    public List<Subtask> getEpicSubtasks(Integer EpicId) {
        List<Subtask> epicSubtasks = super.getEpicSubtasks(EpicId);
        save();
        return epicSubtasks;
    }

    public static void main(String[] args) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(dataHistoryTask);
        Task task1 = new Task("Покупки", "Список покупок", NEW);
        fileBackedTasksManager.saveTask(task1);
        Task task2 = new Task("Тренировка", "Программа", NEW);
        fileBackedTasksManager.saveTask(task2);
        Epic epic1 = new Epic("Большая задача1", "Нужно было описать", NEW);
        fileBackedTasksManager.saveEpic(epic1);
        Subtask subtask1Epic1 = new Subtask("Подзадача1эпик1", "у меня нет фантазии", NEW);
        Subtask subtask2Epic1 = new Subtask("Подзадача2эпик1", "у меня нет фантазии совсем", NEW);
        Subtask subtask3Epic1 = new Subtask("Подзадача3эпик1", "тест", NEW);
        fileBackedTasksManager.saveSubtask(epic1.getId(), subtask1Epic1);
        fileBackedTasksManager.saveSubtask(epic1.getId(), subtask2Epic1);
        fileBackedTasksManager.saveSubtask(epic1.getId(), subtask3Epic1);
        Epic epic2 = new Epic("Большая задача2", "Просто нужно что-то написать", NEW);
        fileBackedTasksManager.saveEpic(epic2);
        fileBackedTasksManager.getTaskById(task1.getId());
        fileBackedTasksManager.getTaskById(task2.getId());
        fileBackedTasksManager.getSubtaskById(subtask3Epic1.getId());
        fileBackedTasksManager.getEpicById(epic2.getId());
        fileBackedTasksManager.getEpicById(epic1.getId());
        fileBackedTasksManager.getSubtaskById(subtask2Epic1.getId());
        fileBackedTasksManager.getSubtaskById(subtask1Epic1.getId());
        fileBackedTasksManager.getTaskById(task1.getId());
        //System.out.println(fileBackedTasksManager.getHistory());
        fileBackedTasksManager.removeTaskById(task2.getId());
        //System.out.println("Получение истории");
        System.out.println(fileBackedTasksManager.getHistory());
        fileBackedTasksManager.loadFromFile("src/dataHistoryTask.csv");
        //System.out.println(fileBackedTasksManager.getHistory());

    }
}
