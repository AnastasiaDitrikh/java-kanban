import model.*;
import model.Subtask;
import model.Epic;
import service.*;

import static model.Status.*;

public class Main {
    public static void main(String[] args) {

        checkMethods();
        checkHistory();
    }

    public static void checkMethods() {
        TaskManager taskManager = Managers.getDefault();
        System.out.println("Проверка на сoхранение задачи");
        Task task1 = new Task("Покупки", "Список покупок", NEW);
        taskManager.saveTask(task1);
        Task task2 = new Task("Тренировка", "Программа", NEW);
        taskManager.saveTask(task2);
        System.out.println("Получение списка задач");
        System.out.println(taskManager.getListAllTask());

        System.out.println("Проверка установки статуса");
        task1.setStatus(IN_PROGRESS);
        task2.setStatus(DONE);
        System.out.println(taskManager.getListAllTask());

        System.out.println("Получение задачи по ID");
        System.out.println(taskManager.getTaskById(task2.getId()));

        System.out.println("Обновление  задачи");
        taskManager.updateTask(new Task(task2.getId(), "Обучение", "План", NEW));
        System.out.println(taskManager.getTaskById(task2.getId()));

        System.out.println("Удаление задачи по ID. Новый список задач:");
        taskManager.removeTaskById(task2.getId());
        System.out.println(taskManager.getListAllTask());

        System.out.println("Очистка списка задач. Вывод пустого списка:");
        taskManager.removeAllTask();
        System.out.println(taskManager.getListAllTask());


        //эпики и подзадачи
        System.out.println("Проверка на сoхранение Эпика");
        Epic epic1 = new Epic("Большая задача1", "Нужно было описать", NEW);
        taskManager.saveEpic(epic1);
        Subtask subtask1Epic1 = new Subtask("Подзадача1эпик1", "у меня нет фантазии", NEW);
        Subtask subtask2Epic1 = new Subtask("Подзадача2эпик1", "у меня нет фантазии совсем", NEW);
        taskManager.saveSubtask(epic1.getId(), subtask1Epic1);
        taskManager.saveSubtask(epic1.getId(), subtask2Epic1);
        subtask1Epic1.setStatus(DONE);
        taskManager.updateSubtask(subtask1Epic1);


        Epic epic2 = new Epic("Большая задача2", "Просто нужно что-то написать", NEW);
        taskManager.saveEpic(epic2);
        Subtask subtask1Epic2 = new Subtask("Подзадача1эпик2", "описание", NEW);
        taskManager.saveSubtask(epic2.getId(), subtask1Epic2);
        subtask1Epic2.setStatus(DONE);
        taskManager.updateSubtask(subtask1Epic2);

        System.out.println("Получение сабтаски по id");
        System.out.println(taskManager.getSubtaskById(subtask2Epic1.getId()));


        System.out.println("Получение списка подзадач");
        System.out.println(taskManager.getSubtasks());
        System.out.println("Получение списка эпиков");
        System.out.println(taskManager.getListAllEpic());
        System.out.println("Получение списка подзадач эпиков");
        System.out.println(epic1.subtaskIdList);
        System.out.println(taskManager.getEpicSubtasks(epic1.getId()));
        System.out.println(epic2.subtaskIdList);
        System.out.println(taskManager.getEpicSubtasks(epic2.getId()));

        System.out.println("Удаление подзадач по id");
        taskManager.removeSubtaskById(subtask1Epic1.getId());
        taskManager.updateEpic(epic1);
        System.out.println(epic1.subtaskIdList);
        System.out.println(epic1);

        System.out.println("Получение Эпика по ID");
        System.out.println(taskManager.getEpicById(5));

        System.out.println("Обновление  эпика");
        Epic epic = new Epic(epic1.getId(), "Большая задача обновлена", "Новое описание", NEW);
        taskManager.updateEpic(epic);
        System.out.println(taskManager.getEpicById(epic.getId()));

        System.out.println("Удаление всех подзадач");
        taskManager.deleteSubtasks();
        System.out.println("Получение списка подзадач эпиков");
        System.out.println(taskManager.getEpicById(epic1.getId()).subtaskIdList);
        System.out.println(taskManager.getEpicSubtasks(epic.getId()));
        System.out.println(taskManager.getEpicById(epic2.getId()).subtaskIdList);
        System.out.println(taskManager.getEpicSubtasks(epic2.getId()));

        System.out.println("Смотрю статус эпиков");
        System.out.println(taskManager.getListAllEpic());

        System.out.println("Удаление Эпика по ID. Новый список Эпиков:");
        taskManager.removeEpicById(epic1.getId());
        System.out.println(taskManager.getListAllEpic());

        System.out.println("Очистка списка эпиков. Вывод пустого списка Эпиков:");
        taskManager.removeAllEpic();
        System.out.println(taskManager.getListAllEpic());

    }

    public static void checkHistory() {
        TaskManager taskManager = Managers.getDefault();
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
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(subtask3Epic1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask2Epic1.getId());
        taskManager.getSubtaskById(subtask1Epic1.getId());
        taskManager.getTaskById(task1.getId());
        System.out.println(taskManager.getHistory());
        taskManager.removeTaskById(task1.getId());
        taskManager.removeAllEpic();
        taskManager.removeAllTask();
        System.out.println("Получение истории");
        System.out.println(taskManager.getHistory());
    }

}