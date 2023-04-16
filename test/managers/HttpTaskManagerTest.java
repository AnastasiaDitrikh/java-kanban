package managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskManager;

import servers.KVServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private KVServer kvServer;

    @BeforeEach
    void startServerAndPrepareData() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = new HttpTaskManager("http://localhost:8078/", true);
        super.prepareData();
    }

    @AfterEach
    void stopServer() {
        kvServer.stop();
    }

    @Test
    public void historyTest() {
        taskManager.getSubtaskById(subtask2Epic1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());

        final List<Task> tasks = taskManager.getListAllTask();
        assertNotNull(tasks, "Пустой список");
        assertEquals(1, tasks.size(), "Размеры списков задач не соответствуют");
        final List<Epic> epics = taskManager.getListAllEpic();
        assertNotNull(epics, "Пустой список");
        assertEquals(1, epics.size(), "Размеры списков эпиков не соответствуют");
        final List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Пустой список");
        assertEquals(2, subtasks.size(), "Размеры списков подзадач не соответствуют");
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "Пустой список");
        assertEquals(3, history.size(), "Размеры списков истории не соответствуют");
    }

    @Test
    public void checkLoadByNewHttpTaskManagerTest() {
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1Epic1.getId());
        HttpTaskManager httpTaskManager = new HttpTaskManager("http://localhost:8078/", true);

        List<Task> tasksFromTaskManager= taskManager.getListAllTask();
        assertEquals(1, tasksFromTaskManager.size());
        assertEquals(tasksFromTaskManager, httpTaskManager.getListAllTask(),"Список задач после выгрузки не совпададает");

        List<Epic> epicsFromTaskManager= taskManager.getListAllEpic();
        assertEquals(1, epicsFromTaskManager.size());
        assertEquals(epicsFromTaskManager, httpTaskManager.getListAllEpic(),"Список эпиков после выгрузки не совпададает");

        List<Subtask> subtasksFromTaskManager= taskManager.getSubtasks();
        assertEquals(2, subtasksFromTaskManager.size());
        assertEquals(subtasksFromTaskManager, httpTaskManager.getSubtasks(),"Список подзадач после выгрузки не совпададает");


        List<Task> historyFromTaskManager= taskManager.getHistory();
        assertEquals(2, historyFromTaskManager.size());
        assertEquals(historyFromTaskManager, httpTaskManager.getHistory(),"Список истории после выгрузки не совпададает");

        List<Task> priorityFromTaskManager= taskManager.getPrioritizedTasksList();
        assertEquals(3,  priorityFromTaskManager.size());
        assertEquals( priorityFromTaskManager, httpTaskManager.getPrioritizedTasksList(),"Список задач после выгрузки не совпададает");

        assertEquals(5,taskManager.getIdGen());
        assertEquals(taskManager.getIdGen(), httpTaskManager.getIdGen(), "Поле idGen не совпадает после выгрузки");
    }
}