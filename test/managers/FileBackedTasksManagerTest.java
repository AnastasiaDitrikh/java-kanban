package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tasks.Status.NEW;


class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private File file;

    @BeforeEach
    void fill() {
        taskManager = new FileBackedTasksManager("test/managers/filesTest/test.csv");
        super.prepareData();
    }

    @Test
    void shouldRestoreId() {
        file = new File("test/managers/filesTest/dataHistoryTest.csv");
        taskManager = FileBackedTasksManager.loadFromFile(file.toString());
        Set<Integer> idSet = FileBackedTasksManager.getAllTasksMap().keySet();
        Integer idMax = 1;
        for (Integer id : idSet) {
            if (id > idMax) {
                idMax = id;
            }
        }
        assertEquals(idMax, taskManager.getIdGen(), "Поле Id не обновилось");
    }

    @Test
    void shouldRestoreTasks() {
        List<Task> expectedList = taskManager.getListAllTask();
        file = new File("test/managers/filesTest/dataHistoryTest.csv");
        taskManager = FileBackedTasksManager.loadFromFile(file.toString());
        List<Task> actualListFromFile = taskManager.getListAllTask();
        assertEquals(expectedList, actualListFromFile, "Список задач после выгрузки не совпададает");
    }

    @Test
    void shouldRestoreSubtasksAndEpics() {
        List<Epic> expectedListEpics = taskManager.getListAllEpic();
        List<Subtask> expectedListSubtasks = taskManager.getSubtasks();
        file = new File("test/managers/filesTest/dataHistoryTest.csv");
        taskManager = FileBackedTasksManager.loadFromFile(file.toString());
        List<Epic> actualListEpicFromFile = taskManager.getListAllEpic();
        List<Subtask> actualListSubtasksFromFile = taskManager.getSubtasks();
        assertEquals(expectedListSubtasks, actualListSubtasksFromFile, "Список задач после выгрузки не совпададает");
        assertEquals(expectedListEpics, actualListEpicFromFile, "Список задач после выгрузки не совпададает");
    }


    @Test
    void shouldRestoreHistory() {
        taskManager.getSubtaskById(subtask1Epic1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask2Epic1.getId());
        List<Task> expectedHistory = taskManager.getHistory();
        file = new File("test/managers/filesTest/dataHistoryTest.csv");
        taskManager = FileBackedTasksManager.loadFromFile(file.toString());
        List<Task> actualHistory = taskManager.getHistory();
        assertEquals(expectedHistory, actualHistory, "Список задач после выгрузки не совпададает");
    }

    @Test
    void shouldRestorePrioritizedTasks() {
        List<Task> expectedPriority = taskManager.getPrioritizedTasksList();
        file = new File("test/managers/filesTest/dataHistoryTest.csv");
        taskManager = FileBackedTasksManager.loadFromFile(file.toString());
        List<Task> actualPriority = taskManager.getPrioritizedTasksList();
        assertEquals(expectedPriority, actualPriority, "Список задач после выгрузки не совпададает");
    }

    @Test
    void shouldRestoreTaskManagerWithSomeTasks() {
        file = new File("test/managers/filesTest/dataHistoryTest.csv");
        taskManager = FileBackedTasksManager.loadFromFile(file.toString());
        final Task task = new Task(1, "Покупки", "Список покупок", NEW, LocalDateTime.of(2023, 2, 19, 17, 40), 60);
        task.getEndTime();
        assertEquals(task, taskManager.getTaskById(1), "Не соответствует");
        final Epic epic = new Epic(2, "Большая задача1", "Нужно было описать", NEW, LocalDateTime.of(2023, 2, 18, 17, 40), 120);
        epic.addSubtask(3);
        epic.addSubtask(4);
        epic.setEndTime(LocalDateTime.of(2023, 3, 19, 21, 40));
        epic.setDuration(120);
        assertEquals(epic, taskManager.epics.get(2), "Не соответствует");
        final Subtask subtask = new Subtask(4, "Подзадача2эпик1", "у меня нет фантазии совсем", NEW, 2, LocalDateTime.of(2023, 3, 19, 20, 40), 60);
        subtask.getEndTime();
        assertEquals(subtask, taskManager.getSubtaskById(4), "Не соответствует");
    }

    @Test
    void shouldEmptyFile() {
        file = new File("test/managers/filesTest/emptyData.csv");
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
        file = new File("test/managers/filesTest/dataWithoutHistoryTest.csv");
        taskManager = FileBackedTasksManager.loadFromFile(file.toString());
        final int historySize = taskManager.getHistory().size();
        assertEquals(0, historySize);
    }


    @Test
    void shouldRestoreEpicWithNoSubtasks() {
        file = new File("test/managers/filesTest/dataEpicWithoutSubtasks.csv");
        taskManager = FileBackedTasksManager.loadFromFile(file.toString());
        final Epic epic = new Epic(3, "Большая задача1", "Нужно было описать", NEW, LocalDateTime.of(2023, 2, 18, 17, 40), 1500);
        assertEquals(epic, taskManager.getEpicById(3), "Не соответствует");
    }
}