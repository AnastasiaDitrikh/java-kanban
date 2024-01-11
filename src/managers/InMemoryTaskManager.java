package managers;

import managers.exceptions.TaskValidationException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

import static tasks.Status.*;

/**
 * Класс реализует управление задачами в памяти (в качестве хранилища используется Map)
 */
public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    protected Integer idGen = 1;

    /**
     * Возвращает текущее значение генератора идентификаторов.
     *
     * @return Текущее значение генератора идентификаторов.
     */
    public Integer getIdGen() {
        return idGen;
    }

    /**
     * Устанавливает новое значение для генератора идентификаторов.
     *
     * @param id Новое значение генератора идентификаторов.
     */
    public void setIdGen(Integer id) {
        this.idGen = id;
    }

    /**
     * Возвращает список задач из истории.
     *
     * @return Список задач из истории.
     */
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    /**
     * Возвращает список приоритетных задач.
     *
     * @return Список приоритетных задач.
     */
    @Override
    public List<Task> getPrioritizedTasksList() {
        return new ArrayList<>(prioritizedTasks);
    }

    /**
     * Сохраняет задачу.
     * Проверяет задачу на валидность, добавляет в список задач tasks с новым идентификатором и добавляет в список приоритетных задач prioritizedTasks.
     *
     * @param task Задача, которую нужно сохранить.
     */
    @Override
    public void saveTask(Task task) {
        validateTask(task);
        tasks.put(idGen, task);
        task.setId(idGen);
        idGen += 1;
        prioritizedTasks.add(task);
    }

    /**
     * Возвращает список всех задач.
     *
     * @return Список всех задач.
     */
    @Override
    public List<Task> getListAllTask() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Удаляет все задачи.
     * Удаляет задачи из списков tasks, historyManager и prioritizedTasks.
     */
    @Override
    public void removeAllTask() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    /**
     * Возвращает задачу по ее идентификатору.
     * Если задача существует, добавляет ее в историю.
     *
     * @param id Идентификатор задачи.
     * @return Задача с указанным идентификатором или null, если задачи не существует.
     */
    @Override
    public Task getTaskById(Integer id) {
        Task gotTask = tasks.get(id);
        if (gotTask != null) {
            historyManager.add(gotTask);
        }
        return gotTask;
    }

    /**
     * Обновляет информацию о задаче.
     * Если задача существует, проверяет ее на валидность, обновляет список приоритетных задач и список задач.
     * Если задачи с указанным идентификатором не существует, выводит сообщение "Некорректно введен ID".
     *
     * @param task Обновленная информация о задаче.
     */
    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            validateTask(task);
            prioritizedTasks.remove(tasks.get(task.getId()));
            prioritizedTasks.add(task);
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Некорректно введен ID");
        }
    }

    /**
     * Удаляет задачу по ее идентификатору.
     * Если задача существует, удаляет ее из списков задач, приоритетных задач и истории.
     * Если задачи с указанным идентификатором не существует, выводит сообщение "Некорректно введен id".
     *
     * @param id Идентификатор задачи.
     */
    @Override
    public void removeTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
        } else {
            System.out.println("Некорректно введен id");
        }
    }


    /**
     * Сохраняет эпик.
     * Добавляет эпик в список эпиков с новым идентификатором и статусом "NEW".
     * Устанавливает идентификатор и генерирует новый идентификатор.
     *
     * @param epic Эпик для сохранения.
     */
    @Override
    public void saveEpic(Epic epic) {
        epics.put(idGen, epic);
        epic.setStatus(NEW);
        epic.setId(idGen);
        idGen += 1;
    }

    /**
     * Возвращает список всех эпиков.
     *
     * @return Список всех эпиков.
     */
    @Override
    public List<Epic> getListAllEpic() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Удаляет все эпики и все их подзадачи.
     * Удаляет эпики из списков, а также удаляет все сопутствующие задачи из истории и списка приоритетных задач.
     */
    @Override
    public void removeAllEpic() {
        Set<Integer> setKeys = epics.keySet();
        for (Integer k : setKeys) {
            historyManager.remove(k);
        }
        epics.clear();
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
    }

    /**
     * Возвращает эпик по его идентификатору.
     * Если эпик существует, добавляет его в историю.
     *
     * @param id Идентификатор эпика.
     * @return Эпик с указанным идентификатором или null, если эпика не существует.
     */
    @Override
    public Epic getEpicById(Integer id) {
        Epic gotEpic = epics.get(id);
        if (gotEpic != null) {
            historyManager.add(gotEpic);
        }
        return gotEpic;
    }

    /**
     * Обновляет информацию об эпике.
     * Если эпик существует, обновляет его в списке эпиков и проверяет его статус.
     * Если эпика с указанным идентификатором не существует, выводит сообщение "Эпика по введенному id не существует".
     *
     * @param epic Обновленная информация об эпике.
     */
    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            findEpicStatus(epic);
            setEndTimeEpic(epic);
        } else {
            System.out.println("Эпика по введенному id не существует");
        }
    }

    /**
     * Удаляет эпик по его идентификатору.
     * Если эпик существует, удаляет его из списков, а также удаляет подзадачи связанные с этим эпиком из списков,
     * истории и списков приоритетных задач.
     * Если эпика с указанным идентификатором не существует, выводит сообщение "Некорректный id".
     *
     * @param id Идентификатор эпика.
     */
    @Override
    public void removeEpicById(Integer id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            List<Integer> subtaskIdList = epic.getSubtaskIdList();
            for (Integer idSubtask : subtaskIdList) {
                prioritizedTasks.remove(subtasks.get(idSubtask));
                subtasks.remove(idSubtask);
                historyManager.remove(idSubtask);
            }
            epics.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Некорректный id");
        }
    }

    /**
     * Сохраняет подзадачу.
     * Проверяет подзадачу на валидность, добавляет ее в список подзадач subtasks с новым идентификатором,
     * и связывает подзадачу с соответствующим эпиком в списке эпиков epics.
     *
     * @param subtask Подзадача для сохранения.
     */
    @Override
    public void saveSubtask(Subtask subtask) {
        validateTask(subtask);
        Epic epicWithSubtask = epics.get(subtask.getEpicId());
        subtasks.put(idGen, subtask);
        subtask.setId(idGen);
        epicWithSubtask.getSubtaskIdList().add(idGen);
        idGen += 1;
        findEpicStatus(epicWithSubtask);
        setEndTimeEpic(epicWithSubtask);
        prioritizedTasks.add(subtask);
    }

    /**
     * Возвращает список всех подзадач.
     *
     * @return Список всех подзадач.
     */
    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    /**
     * Возвращает подзадачу по ее идентификатору.
     * Если подзадача существует, добавляет ее в историю.
     *
     * @param id Идентификатор подзадачи.
     * @return Подзадача с указанным идентификатором или null, если подзадачи не существует.
     */
    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask gotSubtask = subtasks.get(id);
        if (gotSubtask != null) {
            historyManager.add(gotSubtask);
        }
        return gotSubtask;
    }

    /**
     * Обновляет информацию о подзадаче.
     * Если подзадача и связанный эпик существуют, обновляет информацию в списках подзадач
     * и связанных эпиков, а также проверяет статус эпика и устанавливает время завершения эпика.
     * Если подзадачи или эпика с указанными идентификаторами не существует,
     * выводит сообщение "Такой подзадачи или эпика не существует".
     *
     * @param subtask Обновленная информация о подзадаче.
     */
    @Override
    public void updateSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getEpicId())) {
            validateTask(subtask);
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            prioritizedTasks.add(subtask);
            subtasks.put(subtask.getId(), subtask);
            findEpicStatus(epic);
            setEndTimeEpic(epic);
        } else {
            System.out.println("Такой подзадачи или эпика не существует");
        }
    }

    /**
     * Удаляет все подзадачи.
     * Удаляет подзадачи из списка по эпикам и устанавливает нулевые значения информации об эпиках.
     * Удаляет подзадачи из истории и списка приоритетных задач.
     */
    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskIdList().clear();
            findEpicStatus(epic);
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(0);
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
    }

    /**
     * Удаляет подзадачу по ее идентификатору.
     * Если подзадача существует, удаляет ее из списков, истории и связанного эпика.
     * Обновляет статус эпика и устанавливает время завершения эпика.
     * Если подзадачи с указанным идентификатором не существует, выводит сообщение "Некорректно введен id".
     *
     * @param id Идентификатор подзадачи.
     */
    @Override
    public void removeSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIdList().remove(id);
            prioritizedTasks.remove(subtask);
            subtasks.remove(id);
            historyManager.remove(id);
            findEpicStatus(epic);
            setEndTimeEpic(epic);
        } else {
            System.out.println("Неккоректно введен id");
        }
    }

    /**
     * Возвращает список подзадач, связанных с указанным эпиком.
     *
     * @param epicId Идентификатор эпика.
     * @return Список подзадач, связанных с указанным эпиком.
     */
    @Override
    public List<Subtask> getEpicSubtasks(Integer epicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer subtaskId : epics.get(epicId).getSubtaskIdList()) {
            epicSubtasks.add(subtasks.get(subtaskId));
        }
        return epicSubtasks;
    }

    /**
     * Находит статус эпика на основе статусов его подзадач.
     *
     * @param epic Эпик, для которого нужно найти статус.
     */
    private void findEpicStatus(Epic epic) {
        if (epic.getSubtaskIdList().isEmpty()) {
            epic.setStatus(NEW);
            return;
        }
        int counterDone = 0;
        int counterNew = 0;
        for (Integer id : epic.getSubtaskIdList()) {
            Status status = subtasks.get(id).getStatus();
            if (status == NEW) {
                counterNew += 1;
            } else if (status == DONE) {
                counterDone += 1;
            } else {
                epic.setStatus(IN_PROGRESS);
                return;
            }
        }
        if (counterDone == epic.getSubtaskIdList().size()) {
            epic.setStatus(DONE);
        } else if (counterNew == epic.getSubtaskIdList().size()) {
            epic.setStatus(NEW);
        } else {
            epic.setStatus(IN_PROGRESS);
        }
    }

    /**
     * Устанавливает время завершения эпика на основе времени завершения его подзадач.
     *
     * @param epic Эпик, для которого нужно установить время завершения.
     */
    protected void setEndTimeEpic(Epic epic) {
        long duration = 0L;
        LocalDateTime EpicStartTime = null;
        LocalDateTime EpicEndTime = null;
        if (epic.getSubtaskIdList() != null) {
            for (Integer idSubTask : epic.getSubtaskIdList()) {
                Subtask subtask = subtasks.get(idSubTask);
                duration += subtask.getDuration();
                if (subtask.getStartTime() != null &&
                        (EpicStartTime == null || subtask.getStartTime().isBefore(EpicStartTime))) {
                    EpicStartTime = subtask.getStartTime();
                }
                if (subtask.getEndTime() != null &&
                        (EpicEndTime == null || subtask.getEndTime().isAfter(EpicEndTime))) {
                    EpicEndTime = subtask.getEndTime();
                }
            }
        }
        epic.setStartTime(EpicStartTime);
        epic.setEndTime(EpicEndTime);
        epic.setDuration(duration);
    }

    /**
     * Проверяет наличие пересечений между задачами.
     * Если задачи пересекаются, выбрасывается исключение TaskValidationException с информацией о пересечении.
     *
     * @param task Задача, которую нужно проверить на пересечения.
     * @throws TaskValidationException Если задача пересекается с другой задачей.
     */
    protected void validateTask(Task task) {
        if (prioritizedTasks.isEmpty() || task.getStartTime() == null) {
            return;
        }
        for (Task taskFromList : prioritizedTasks) {
            if (taskFromList.getId().equals(task.getId()) || taskFromList.getStartTime() == null) {
                continue;
            }
            LocalDateTime taskFromListStart = taskFromList.getStartTime();
            LocalDateTime taskAddStart = task.getStartTime();
            LocalDateTime taskFromListEnd = taskFromList.getEndTime();
            LocalDateTime taskAddEnd = task.getEndTime();
            if (!taskAddEnd.isAfter(taskFromListStart)) {
                continue;
            }
            if (!taskAddStart.isBefore(taskFromListEnd)) {
                continue;
            }
            throw new TaskValidationException("Задача пересекается с id=" + taskFromList.getId() + " c " +
                    taskFromList.getStartTime() + " по " + taskFromList.getEndTime());
        }
    }

}


