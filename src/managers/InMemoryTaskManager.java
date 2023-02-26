package service;

import model.*;
import org.opentest4j.AssertionFailedError;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static model.Status.*;
import static model.TypeTask.*;


public class InMemoryTaskManager implements TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }


    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    Integer id = 1;

    protected HistoryManager getHistoryManager() {
        return historyManager;
    }


    //Task
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    @Override
    public void add(Task task) {
        historyManager.add(task);
    }


    @Override
    public List<Task> getPrioritizedTasksList() {
        List<Task> prioritizedTasksList = new ArrayList<>();
        prioritizedTasksList.addAll(prioritizedTasks);
        return prioritizedTasksList;
    }

    public void validateTask (Task task) {
        List<Task> prioritizedTasksList = new ArrayList<>();
        prioritizedTasksList.addAll(prioritizedTasks);
        for (Task t : prioritizedTasksList) {
            LocalDateTime taskFromList = t.getStartTime();
            LocalDateTime taskAdd = task.getStartTime();
            try {
                if (!taskFromList.isEqual(taskAdd)) {
                    prioritizedTasks.add(task);
                    return;
                }
            }catch (Exception E){
                System.out.println("Пересечение задач!");
            }
        }
    }
    public void validateTask2 (Task task) throws AssertionFailedError {
        List<Task> prioritizedTasksList = new ArrayList<>();
        prioritizedTasksList.addAll(prioritizedTasks);
        for (Task t : prioritizedTasksList) {
            LocalDateTime taskFromList = t.getStartTime();
            LocalDateTime taskAdd = task.getStartTime();
            if (!taskFromList.isEqual(taskAdd)) {
                    prioritizedTasks.add(task);
                    throw new AssertionFailedError();
                }
        }
    }


    @Override
    public void saveTask(Task task) {
        tasks.put(id, task);
        task.setStatus(NEW);
        task.setTypeTask(TASK);
        task.setId(id);
        task.setEndTime(task.getStartTime().plusMinutes(task.getDuration()));
        id += 1;
        prioritizedTasks.add(task);
    }

    @Override
    public List<Task> getListAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTask() {
        Set<Integer> setKeys = tasks.keySet();
        for (Integer k : setKeys) {
            historyManager.remove(k);
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(Integer id) {
        Task gotTask = tasks.get(id);
        if (gotTask != null) {
            historyManager.add(gotTask);
        }
        return gotTask;
    }


    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Некорректно введен ID");
        }
    }

    @Override
    public void removeTaskById(Integer id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            tasks.remove(id);
        } else {
            System.out.println("Некорректно введен id");
        }
    }


    //Epic
    @Override
    public void saveEpic(Epic epic) {
        epics.put(id, epic);
        epic.setStatus(NEW);
        epic.setTypeTask(EPIC);
        epic.setId(id);
        id += 1;
    }

    @Override
    public List<Epic> getListAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpic() {
        Set<Integer> setKeys = epics.keySet();
        for (Integer k : setKeys) {
            historyManager.remove(k);
        }
        epics.clear();
        Set<Integer> setKeysSubtasks = subtasks.keySet();
        for (Integer key : setKeysSubtasks) {
            historyManager.remove(key);
        }
        subtasks.clear();
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic gotEpic = epics.get(id);
        if (gotEpic != null) {
            historyManager.add(gotEpic);
        }
        return gotEpic;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            epic.setTypeTask(EPIC);
            findEpicStatus(epic);
            setEndTimeEpic(epic);
        } else {
            System.out.println("Эпика по введенному id не существует");
        }
    }

    private void findEpicStatus(Epic epic) {
        if (epic.subtaskIdList.size() == 0) {
            epic.setStatus(NEW);
            return;
        }
        int counterDone = 0;
        int counterNew = 0;
        for (Integer id : epic.subtaskIdList) {
            Status status = subtasks.get(id).getStatus();
            if (status == NEW) {
                counterNew += 1;
            } else if (status == DONE) {
                counterDone += 1;
            }
        }
        if (counterDone == epic.subtaskIdList.size()) {
            epic.setStatus(DONE);
        } else if (counterNew == epic.subtaskIdList.size()) {
            epic.setStatus(NEW);
        } else {
            epic.setStatus(IN_PROGRESS);
        }
    }

    public void setEndTimeEpic(Epic epic) {
        LocalDateTime startTime = LocalDateTime.MAX;
        LocalDateTime endTime = LocalDateTime.MIN;
        Duration durationEpic = null;
        for (Subtask subtask : subtasks.values()) {
            LocalDateTime subtaskStartTime = subtask.getStartTime();
            if (subtaskStartTime == null) {
                epic.setDuration(0);
                return;
            }
            LocalDateTime subtaskEndTime = subtask.getEndTime();
            if (subtaskStartTime.isBefore(startTime)) {
                startTime = subtaskStartTime;
            } else if (subtaskEndTime.isAfter(endTime)) {
                endTime = subtaskEndTime;
            }
        }
        durationEpic = Duration.between(startTime, endTime);
        epic.setDuration(durationEpic.toMinutes());
        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
    }

    @Override
    public void removeEpicById(Integer id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            List<Integer> subtaskIdList = epic.getSubtaskIdList();
            for (int i : subtaskIdList) {
                subtasks.remove(i);
                historyManager.remove(i);
            }
            epics.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Некорректный id");
        }
    }

    //Subtask
    @Override
    public void saveSubtask(Integer epicId, Subtask subtask) {
        Epic epicWithSubtask = epics.get(epicId);
        subtask.setStatus(NEW);
        subtask.setTypeTask(SUBTASK);
        subtask.setEndTime(subtask.getStartTime().plusMinutes(subtask.getDuration()));
        subtasks.put(id, subtask);
        subtask.setId(id);
        subtask.setEpicId(epicId);
        epicWithSubtask.subtaskIdList.add(id);
        id += 1;
        findEpicStatus(epicWithSubtask);
        setEndTimeEpic(epicWithSubtask);
        prioritizedTasks.add(subtask);
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask gotSubtask = subtasks.get(id);
        if (gotSubtask != null) {
            historyManager.add(gotSubtask);
        }
        return gotSubtask;
    }


    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getEpicId())) {
            subtasks.put(subtask.getId(), subtask);
            subtask.setEndTime(subtask.getStartTime().plusMinutes(subtask.getDuration()));
            findEpicStatus(epics.get(subtask.getEpicId()));
            setEndTimeEpic(epics.get(subtask.getEpicId()));
        } else {
            System.out.println("Такой подзадачи или эпика не существует");
        }
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.subtaskIdList.clear();
            findEpicStatus(epic);
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(0);
        }
        Set<Integer> setKeys = subtasks.keySet();
        for (Integer k : setKeys) {
            historyManager.remove(k);
        }
        subtasks.clear();
    }

    @Override
    public void removeSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());
            epic.getSubtaskIdList().remove(id);
            subtasks.remove(id);
            historyManager.remove(id);
            findEpicStatus(epic);
            setEndTimeEpic(epic);
        } else {
            System.out.println("Неккоректно введен id");
        }
    }

    @Override
    public List<Subtask> getEpicSubtasks(Integer EpicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (int i = 0; i < epics.get(EpicId).getSubtaskIdList().size(); i++) {
            Integer idSubtask = epics.get(EpicId).getSubtaskIdList().get(i);
            epicSubtasks.add(subtasks.get(idSubtask));
        }
        return epicSubtasks;
    }
}


