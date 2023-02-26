package Tests;

import model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTasksManager;


import static model.Status.*;
import static model.TypeTask.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.time.LocalDateTime;


class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private File file;

    @BeforeEach
    void fill() {
        taskManager = new FileBackedTasksManager("src/Tests/filesTest/test.csv");
        super.prepareData();
    }

    @Test
    void shouldRestoreTaskManagerWithSomeTasks() {
        file = new File("src/Tests/filesTest/dataHistoryTest.csv");
        taskManager = FileBackedTasksManager.loadFromFile(file.toString());
        final Task task = new Task(1, TASK, "Покупки", "Список покупок", NEW, LocalDateTime.of(2023, 2, 19, 17, 40), 60);
        task.setEndTime(task.getStartTime().plusMinutes(task.getDuration()));
        assertEquals(task, taskManager.getTaskById(1), "Не соответствует");
        final Epic epic = new Epic(3, EPIC, "Большая задача1", "Нужно было описать",NEW, LocalDateTime.of(2023,02,18,17,40),1500);//TODO
        epic.setEndTime(LocalDateTime.of(2023,2,19,18,40));
        assertEquals(epic, taskManager.getEpicById(3), "Не соответствует");
        final Subtask subtask = new Subtask(5, SUBTASK, "Подзадача2эпик1", "у меня нет фантазии совсем", NEW, 3, LocalDateTime.of(2023, 2, 19, 17, 40), 60);
        subtask.setEndTime(subtask.getStartTime().plusMinutes(subtask.getDuration()));
        assertEquals(subtask, taskManager.getSubtaskById(5), "Не соответствует");
    }

    @Test
    void shouldEmptyFile() {
        file = new File("src/Tests/filesTest/emptyData.csv");
        taskManager = FileBackedTasksManager.loadFromFile(file.toString());
        taskManager.getListAllTask();
        taskManager.getListAllEpic();
        taskManager.getSubtasks();
        assertEquals(0, taskManager.getListAllTask().size(), "Файл не пуст");
        assertEquals(0, taskManager.getListAllEpic().size(), "Файл не пуст");
        assertEquals(0, taskManager.getSubtasks().size(), "Файл не пуст");
    }


    @Test
    void shouldRestoreTasksWithoutHistory() {
        file = new File("src/Tests/filesTest/dataWithoutHistoryTest.csv");
        taskManager = FileBackedTasksManager.loadFromFile(file.toString());
        final int historySize = taskManager.getHistory().size();
        assertEquals(0, historySize);
    }


    @Test
    void shouldRestoreEpicWithNoSubtasks() {
        file = new File("src/Tests/filesTest/dataEpicWithoutSubtasks.csv");
        taskManager = FileBackedTasksManager.loadFromFile(file.toString());
        final Epic epic = new Epic(3, EPIC, "Большая задача1", "Нужно было описать", NEW,LocalDateTime.of(2023,02,18,17,40),1500);
        assertEquals(epic, taskManager.getEpicById(3), "Не соответствует");
    }
}


