import model.*;
import model.Subtask;
import model.Epic;
import service.*;

import static model.Status.*;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();//изменено



        Task task1 = new Task("Покупки", "Список покупок", NEW);
        taskManager.saveTask(task1);
        Task task2 = new Task("Тренировка", "Программа", NEW);
        taskManager.saveTask(task2);
        Epic epic1 = new Epic("Большая задача1", "Нужно было описать", NEW);
        taskManager.saveEpic(epic1);
        Subtask subtask1Epic1 = new Subtask("Подзадача1эпик1", "у меня нет фантазии", NEW);
        Subtask subtask2Epic1 = new Subtask("Подзадача2эпик1", "у меня нет фантазии совсем", NEW);
        Subtask subtask3Epic1 = new Subtask("Подзадача3эпик1", "тест", NEW);
        taskManager.saveSubtask(epic1.getId(), subtask1Epic1);
        taskManager.saveSubtask(epic1.getId(), subtask2Epic1);
        taskManager.saveSubtask(epic1.getId(), subtask3Epic1);
        Epic epic2 = new Epic("Большая задача2", "Просто нужно что-то написать", NEW);
        taskManager.saveEpic(epic2);
        taskManager.getTaskById(task1.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(task2.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(subtask3Epic1.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(epic2.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask2Epic1.getId());
        taskManager.getSubtaskById(subtask1Epic1.getId());
        taskManager.getTaskById(task1.getId());







        taskManager.removeTaskById(task1.getId());
        taskManager.removeSubtaskById(subtask2Epic1.getId());
        taskManager.removeEpicById(epic1.getId());
        System.out.println("Получение истории");
        System.out.println(taskManager.getHistory());



    }
}