package managers;

import tasks.Task;

import java.util.List;

/**
 * Интерфейс для управления историей
 */

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();

    void remove(Integer id);
}