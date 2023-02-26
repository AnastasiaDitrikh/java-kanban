package Tests;

import model.*;

import static model.Status.*;
import static model.TypeTask.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task1;
    protected Epic epic1;
    protected Subtask subtask1Epic1;
    protected Subtask subtask2Epic1;

    //https://yandex.ru/video/preview/13059811921362556273 - Хорошие видео по тестам для меня), чтобы не потерять)
    void prepareData() {
        task1 = new Task("Покупки", "Список покупок", LocalDateTime.of(2023,2,19,17,40),60);
        taskManager.saveTask(task1);
        epic1 = new Epic("Большая задача1", "Нужно было описать");
        taskManager.saveEpic(epic1);
        subtask1Epic1 = new Subtask("Подзадача1эпик1", "у меня нет фантазии", LocalDateTime.of(2023,2,18,17,40),60);
        subtask2Epic1 = new Subtask("Подзадача2эпик1", "у меня нет фантазии совсем", LocalDateTime.of(2023,2,19,17,40),60);
        taskManager.saveSubtask(epic1.getId(), subtask1Epic1);
        taskManager.saveSubtask(epic1.getId(), subtask2Epic1);
    }
    @Test
    void saveTask() {
        Task task3 = new Task("Тестовая задача", "Описание тестовой задачи",LocalDateTime.of(2023,2,19,17,40),60);
        taskManager.saveTask(task3);
        Task savedTask = taskManager.getTaskById(task3.getId());
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task3, savedTask, "Задачи не совпадают");
        final List<Task> tasksList = taskManager.getListAllTask();
        assertNotNull(tasksList, "Задачи не возвращаются");
        assertEquals(2, tasksList.size(), "Неверное количество задач");
        assertTrue(tasksList.contains(task3), "Задачи нет в списке задач");
    }
    @Test
    void getListAllTask() {
        List<Task> expectedListTask = new ArrayList<>();
        expectedListTask.add(task1);
        List<Task> listAllTask = taskManager.getListAllTask();
        assertEquals(expectedListTask.size(), listAllTask.size(), "Размеры списков задач не равны");
        assertTrue(expectedListTask.containsAll(listAllTask), "Списки не совпадают");
    }

    @Test
    void removeAllTask() {
        taskManager.getTaskById(task1.getId());
        taskManager.removeAllTask();
        assertEquals(0, taskManager.getListAllTask().size(), "Список задач не пуст");
        //проверяем историю
        assertTrue(taskManager.getHistory().isEmpty(), "История не пуста");
    }

    @Test
    void getTaskById() {
        Task gotTask = taskManager.getTaskById(task1.getId());
        assertNotNull(gotTask.getId(), "Некорректный id");
        assertEquals(task1, gotTask, "Получена не та задача");
        assertTrue(taskManager.getHistory().contains(gotTask), "Задача не попала в историю");
    }


    @Test
    void getHistory() {
        List<Task> expectedListHistory = new ArrayList<>();
        taskManager.getTaskById(task1.getId());
        expectedListHistory.add(task1);
        taskManager.getEpicById(epic1.getId());
        expectedListHistory.add(epic1);
        taskManager.getSubtaskById(subtask2Epic1.getId());
        expectedListHistory.add(subtask2Epic1);
        List<Task> listHistory = taskManager.getHistory();
        assertEquals(expectedListHistory.size(), listHistory.size(), "Размеры списков  не равны");
        assertTrue(expectedListHistory.containsAll(listHistory), "Списки не совпадают");
    }

    // Тесты задач




    @Test
    void updateTask() {
        task1.setStatus(IN_PROGRESS);
        task1.setDescription("Изменение описание задачи");
        taskManager.updateTask(task1);
        assertNotNull(task1.getId(), "Некорректный id");
        Task expectedUpdatedTask = new Task(1, TASK, "Покупки", "Изменение описание задачи", IN_PROGRESS, LocalDateTime.of(2023,2,19,17,40),60);
        expectedUpdatedTask.setEndTime(expectedUpdatedTask.getStartTime().plusMinutes(expectedUpdatedTask.getDuration()));
        assertEquals(expectedUpdatedTask, task1, "Обновление задачи не произошло");
        assertTrue(taskManager.getListAllTask().contains(expectedUpdatedTask), "Обновление задачи в списке не произошло");
    }

    @Test
    void removeTaskById() {
        taskManager.removeTaskById(task1.getId());
        assertNotNull(task1.getId(), "Некорректный id");
        assertFalse(taskManager.getListAllTask().contains(task1), "Задача не удалена");
        assertFalse(taskManager.getHistory().contains(task1), "Задача не удалена из истории");
    }

    // Тесты эпиков
    @Test
    void saveEpic() {
        Epic epic2 = new Epic("Большая задача2", "Просто нужно что-то написать", NEW);
        taskManager.saveEpic(epic2);
        Task savedEpic = taskManager.getEpicById(epic2.getId());
        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic2, savedEpic, "Эпики не совпадают");
        final List<Epic> epicList = taskManager.getListAllEpic();
        assertNotNull(epicList, "Эпик не возвращается");
        assertEquals(2, epicList.size(), "Неверное количество эпиков");
        assertTrue(epicList.contains(epic2), "Эпика нет в списке эпиков");
    }

    @Test
    void getListAllEpic() {
        List<Epic> expectedListEpics = new ArrayList<>();
        expectedListEpics.add(epic1);
        List<Epic> listAllEpic = taskManager.getListAllEpic();
        assertEquals(expectedListEpics.size(), listAllEpic.size(), "Размеры списков эпиков не равны");
        assertTrue(expectedListEpics.containsAll(listAllEpic), "Списки не совпадают");
    }

    @Test
    void removeAllEpic() {
        taskManager.getEpicById(epic1.getId()); //заполнили историю
        taskManager.removeAllEpic();
        assertTrue(taskManager.getListAllEpic().isEmpty(), "Хранилие эпиков не очистилось");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Хранилие подзадач не очистилось");
        //проверка истории
        assertFalse(taskManager.getHistory().contains(epic1), "История не пуста");
    }

    @Test
    void getEpicById() {
        Epic gotEpic = taskManager.getEpicById(epic1.getId());
        assertNotNull(gotEpic, "Эпик не найден");
        assertNotNull(gotEpic.getId(), "Некорректный id");
        assertEquals(epic1, gotEpic, "Получен не тот эпик");
        assertTrue(taskManager.getHistory().contains(gotEpic), "Эпик не сохранился в истории");
    }

    @Test
    void updateEpic() {
        epic1.setDescription("Изменение описание эпика");
        epic1.setName("Изменение название эпика");
        taskManager.updateEpic(epic1);
        taskManager.deleteSubtasks();
        Epic expectedUpdatedEpic = new Epic(2, EPIC, "Изменение название эпика", "Изменение описание эпика", NEW);
        assertNotNull(epic1.getId(), "Некорректный id");
        assertEquals(expectedUpdatedEpic, epic1, "Обновление задачи не произошло");
        assertTrue(taskManager.getListAllEpic().contains(expectedUpdatedEpic), "Обновление задачи в списке не произошло");
    }

    @Test
    void removeEpicById() {
        taskManager.removeEpicById(epic1.getId());
        assertFalse(taskManager.getListAllEpic().contains(epic1), "Эпик не удален");
        assertFalse(taskManager.getSubtasks().contains(subtask1Epic1), "При удалении эпика подзадача эпика не удалилась из хранилища");
        assertFalse(taskManager.getHistory().contains(epic1), "Эпик не удален из истории");
        assertNotNull(epic1.getId(), "Некорректный id");
    }

    // Тесты подзадач
    @Test
    void saveSubtask() {
        Subtask subtask3Epic1 = new Subtask("Подзадача3эпик1", "Ох уж эти тесты(", LocalDateTime.of(2023,2,19,17,40),60);
        taskManager.saveSubtask(epic1.getId(), subtask3Epic1);
        Task savedSubtask = taskManager.getSubtaskById(subtask3Epic1.getId());
        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask3Epic1, savedSubtask, "Подзадачи не совпадают");
        final List<Subtask> subtasksList = taskManager.getSubtasks();
        assertNotNull(subtasksList, "Подзадача не возвращается");
        assertEquals(3, subtasksList.size(), "Неверное количество Подзадач");
        assertTrue(subtasksList.contains(subtask3Epic1), "Подзадачи нет в списке подзадач");
        assertTrue(taskManager.getEpicSubtasks(epic1.getId()).contains(subtask3Epic1), "В лист подзадач эпика не добавилась новая подзадача");
        assertEquals(epic1.getId(), subtask3Epic1.getEpicId(), "Подзадача не соответствует Эпику");

        assertEquals(NEW, epic1.getStatus(), "Статус эпика изменился, а не должен был");
    }

    @Test
    void getSubtasks() {
        List<Subtask> expectedListSubtask = new ArrayList<>();
        expectedListSubtask.add(subtask1Epic1);
        expectedListSubtask.add(subtask2Epic1);
        List<Subtask> listAllSubtask = taskManager.getSubtasks();
        assertEquals(expectedListSubtask.size(), listAllSubtask.size(), "Размеры списков подзадач не равны");
        assertTrue(expectedListSubtask.containsAll(listAllSubtask), "Списки не совпадают");
    }

    @Test
    void getSubtaskById() {
        Subtask gotSubtask = taskManager.getSubtaskById(subtask1Epic1.getId());
        assertNotNull(subtask1Epic1.getId(), "Некорректно введен id");
        assertEquals(subtask1Epic1, gotSubtask, "Получена не та подзадача");
        assertTrue(taskManager.getHistory().contains(gotSubtask), "Подзадача не попала в историю");
    }

    @Test
    void updateSubtask() {
        subtask1Epic1.setStatus(IN_PROGRESS);
        subtask1Epic1.setDescription("Изменение описание подзадачи");
        taskManager.updateSubtask(subtask1Epic1);
        Subtask expectedUpdatedSubtask = new Subtask(3, SUBTASK, "Подзадача1эпик1", "Изменение описание подзадачи", IN_PROGRESS, 2, LocalDateTime.of(2023,2,18,17,40), 60);
        expectedUpdatedSubtask.setEndTime(expectedUpdatedSubtask.getStartTime().plusMinutes(expectedUpdatedSubtask.getDuration()));
        assertNotNull(subtask1Epic1.getId(), "Некорректно введен id");
        assertEquals(expectedUpdatedSubtask, subtask1Epic1, "Обновление задачи не произошло");
        assertTrue(taskManager.getSubtasks().contains(expectedUpdatedSubtask), "Обновление подзадачи в списке не произошло");

        //обновилась подзадача, проверка статуса эпика (статус подзадач IN_PROGRESS и NEW)
        Epic updatedEpic = taskManager.getEpicById(subtask1Epic1.getEpicId());
        assertEquals(IN_PROGRESS, updatedEpic.getStatus(), "Статус эпика не обновился");

        //проверка статуса эпика (статус подзадач DONE и NEW)
        subtask1Epic1.setStatus(DONE);
        taskManager.updateSubtask(subtask1Epic1);
        assertEquals(IN_PROGRESS, updatedEpic.getStatus(), "Статус эпика не обновился");

        //Все подзадачи со статусом NEW
        subtask1Epic1.setStatus(NEW);
        taskManager.updateSubtask(subtask1Epic1);
        assertEquals(NEW, updatedEpic.getStatus(), "Статус эпика не обновился");

        //Все подзадачи со статусом DONE
        subtask1Epic1.setStatus(DONE);
        taskManager.updateSubtask(subtask1Epic1);
        subtask2Epic1.setStatus(DONE);
        taskManager.updateSubtask(subtask2Epic1);
        assertEquals(DONE, updatedEpic.getStatus(), "Статус эпика не обновился");
    }

    @Test
    void deleteSubtasks() {
        subtask1Epic1.setStatus(IN_PROGRESS);
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask2Epic1.getId());
        taskManager.updateEpic(epic1);
        taskManager.deleteSubtasks();
        assertTrue(taskManager.getSubtasks().isEmpty(), "Хранилище подзадач не очистилось");
        //Проверка статуса эпика, при пустом списке подзадач
        assertEquals(NEW, epic1.getStatus(), "Статус эпика не обновился");
        //Проверка истории
        assertFalse(taskManager.getHistory().contains(subtask2Epic1), "Из истории не удалилась подзадача");
    }

    @Test
    void removeSubtaskById() {
        subtask2Epic1.setStatus(DONE);
        taskManager.updateSubtask(subtask2Epic1);
        taskManager.removeSubtaskById(subtask1Epic1.getId());
        assertFalse(taskManager.getEpicSubtasks(epic1.getId()).contains(subtask1Epic1), "Подзадача в списке эпика не удален");
        assertFalse(taskManager.getSubtasks().contains(subtask1Epic1), "Подзадача не удалилась из хранилища");
        //Проверка статуса эпика
        assertEquals(DONE, epic1.getStatus(), "Статус эпика не обновился");
        assertNotNull(subtask1Epic1.getId(), "Некорректно введен id");
        assertFalse(taskManager.getHistory().contains(subtask1Epic1), "Подзадача не удалена из истории");
    }

    @Test
    void getEpicSubtasks() {
        List<Subtask> expectedListSubtaskEpic1 = new ArrayList<>();
        expectedListSubtaskEpic1.add(subtask1Epic1);
        expectedListSubtaskEpic1.add(subtask2Epic1);
        List<Subtask> actualListSubtaskEpic1 = taskManager.getEpicSubtasks(epic1.getId());
        assertEquals(expectedListSubtaskEpic1.size(), actualListSubtaskEpic1.size(), "Размеры списков подзадач не равны");
        assertTrue(expectedListSubtaskEpic1.containsAll(actualListSubtaskEpic1), "Списки не совпадают");
    }
}