package managers;

import managers.exceptions.TaskValidationException;
import tasks.*;


import java.time.LocalDateTime;
import java.util.*;

import static tasks.Status.*;


public class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    protected Integer idGen = 1;

    public Integer getIdGen() {
        return idGen;
    }

    public void setIdGen(Integer id) {
        this.idGen = id;
    }

    //Task
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    @Override
    public List<Task> getPrioritizedTasksList() {
        return new ArrayList<>(prioritizedTasks);
    }


    @Override
    public void saveTask(Task task) {
        validateTask(task);
        tasks.put(idGen, task);
        task.setId(idGen);
        idGen += 1;
        prioritizedTasks.add(task);
    }

    @Override
    public List<Task> getListAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTask() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
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
            validateTask(task);
            prioritizedTasks.remove(tasks.get(task.getId()));
            prioritizedTasks.add(task);
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Некорректно введен ID");
        }
    }

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


    //Epic
    @Override
    public void saveEpic(Epic epic) {
        epics.put(idGen, epic);
        epic.setStatus(NEW);
        epic.setId(idGen);
        idGen += 1;
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
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
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
            findEpicStatus(epic);
            setEndTimeEpic(epic);
        } else {
            System.out.println("Эпика по введенному id не существует");
        }
    }


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

    //Subtask
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

    @Override
    public List<Subtask> getEpicSubtasks(Integer EpicId) {
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer subtaskId : epics.get(EpicId).getSubtaskIdList()) {
            epicSubtasks.add(subtasks.get(subtaskId));
        }
        return epicSubtasks;
    }

    private void findEpicStatus(Epic epic) {
        if (epic.getSubtaskIdList().size() == 0) {
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


