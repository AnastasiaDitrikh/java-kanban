package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static tasks.Status.NEW;
import static tasks.TypeTask.*;
import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {


    protected Task task1;
    protected Epic epic1;
    protected Subtask subtask1Epic1;
    protected Subtask subtask2Epic1;

    protected HistoryManager historyManager;

    @BeforeEach
    void fillHistory() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task( 1, "Покупки", "Список покупок", NEW);
        epic1 = new Epic(2, "Большая задача1", "Нужно было описать", NEW);
        subtask1Epic1 = new Subtask(3,"Подзадача1эпик1", "у меня нет фантазии", NEW, 2);
        subtask2Epic1 = new Subtask(4,"Подзадача2эпик1", "у меня нет фантазии совсем", NEW, 2);
    }

    @Test
    void add() {
        historyManager.add(epic1);
        historyManager.add(task1);
        historyManager.add(subtask1Epic1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertTrue(history.contains(task1), "Задача не попала в историю");
        assertTrue(history.contains(epic1), "Эпик не попал в историю");
        assertTrue(history.contains(subtask1Epic1), "Подзадача не попала в историю");
        assertEquals(3, history.size(), "История не пустая.");

        //addWithDuplication()
        historyManager.add(task1);
        final List<Task> historyUpdated = historyManager.getHistory();
        assertTrue(historyUpdated.contains(task1), "Задача не попала в историю");
        assertEquals(task1, historyUpdated.get(historyUpdated.size() - 1), "Новая добавленная задача не попала в конец списка истории");
        int count = 0;
        for (Task task : historyUpdated) {
            if (task.equals(task1)) {
                count += 1;
            }
        }
        assertEquals(1, count, "В истории более одного экземпляра задачи");
    }

    @Test
    void getHistory() {
        //getEmptyHistory();
        assertEquals(0, historyManager.getHistory().size(), "Список истории не пуст");
        //наполнение истории
        List<Task> expectedListHistory = new ArrayList<>();
        historyManager.add(task1);
        expectedListHistory.add(task1);
        historyManager.add(epic1);
        expectedListHistory.add(epic1);
        historyManager.add(subtask1Epic1);
        expectedListHistory.add(subtask1Epic1);
        List<Task> listHistory = historyManager.getHistory();
        assertNotNull(listHistory, "История пустая");
        assertEquals(expectedListHistory.size(), listHistory.size(), "Размеры списков истории  не равны");
        assertTrue(expectedListHistory.containsAll(listHistory), "Списки не совпадают");
    }

    @Test
    void remove() {
        //removeWithEmptyHistory()
        historyManager.remove(task1.getId());
        assertFalse(historyManager.getHistory().contains(task1), "Из истории не удалена задача");
        // remove()
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(subtask1Epic1);
        historyManager.add(subtask2Epic1);
        // removeFromMiddle()
        historyManager.remove(subtask1Epic1.getId());
        assertFalse(historyManager.getHistory().contains(subtask1Epic1), "Из истории не удалена задача");
        assertEquals(3, historyManager.getHistory().size(), "Размеры списков не соответствуют");
        // removeFromBeginning()
        historyManager.remove(task1.getId());
        assertFalse(historyManager.getHistory().contains(task1), "Из истории не удалена задача");
        assertEquals(2, historyManager.getHistory().size(), "Размеры списков не соответствуют");
        assertEquals(epic1, historyManager.getHistory().get(0), "Из истории не удалена задача");
        // removeFromEnd()
        historyManager.remove(subtask2Epic1.getId());
        assertFalse(historyManager.getHistory().contains(subtask2Epic1), "Из истории не удалена задача");
        assertEquals(1, historyManager.getHistory().size(), "Размеры списков не соответствуют");
    }
}