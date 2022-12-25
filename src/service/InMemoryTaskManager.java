package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static model.Status.*;


public class InMemoryTaskManager implements TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    Integer id = 0;

    //Task
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void saveTask(Task task) {
        tasks.put(id, task);
        task.setStatus(NEW);
        task.setId(id);
        id += 1;
    }

    @Override
    public List<Task> getListAllTask() {
        List<Task> listAllTask = new ArrayList<>(tasks.values());
        return listAllTask;
    }

    @Override
    public void removeAllTask() {
        Set<Integer> setKeys = tasks.keySet();
        for (Integer k : setKeys) { //проверка contains перенесена из всех методов по удалению в historyManager.remove,
            // это действительно упростило код во всех методах здесь, жаль, что я вчера не догадалась,
            //мало того, выяснилось, что методы contains здесь работали неккоректно, сейчас проблемы устранены
            historyManager.remove(k);
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(Integer id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
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
        historyManager.remove(id);
        tasks.remove(id);
    }


    //Epic
    @Override
    public void saveEpic(Epic epic) {
        epics.put(id, epic);
        epic.setStatus(NEW);
        epic.setId(id);
        id += 1;
    }

    @Override
    public List<Epic> getListAllEpic() {
        List<Epic> listAllEpic = new ArrayList<>(epics.values());
        return listAllEpic;
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
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            findEpicStatus(epic);
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

    @Override
    public void removeEpicById(Integer id) {
        Epic epic = epics.get(id);
        List<Integer> subtaskIdList = epic.getSubtaskIdList();
        for (int i : subtaskIdList) {
            subtasks.remove(i);
            historyManager.remove(i);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    //Subtask
    @Override
    public void saveSubtask(Integer epicId, Subtask subtask) {
        Epic epicWithSubtask = epics.get(epicId);
        subtask.setStatus(NEW);
        subtasks.put(id, subtask);
        subtask.setId(id);
        subtask.setEpicId(epicId);
        epicWithSubtask.subtaskIdList.add(id);
        id += 1;
        findEpicStatus(epicWithSubtask);
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> listAllSubtasks = new ArrayList<>(subtasks.values());
        return listAllSubtasks;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId()) && epics.containsKey(subtask.getEpicId())) {
            subtasks.put(subtask.getId(), subtask);
            findEpicStatus(epics.get(subtask.getEpicId()));
        } else {
            System.out.println("Такой подзадачи или эпика не существует");
        }
    }

    @Override
    public void deleteSubtasks() {
        for (Epic epic : epics.values()) {
            epic.subtaskIdList.clear();
            findEpicStatus(epic);
        }
        Set<Integer> setKeys = subtasks.keySet();
        for (Integer k : setKeys) {
            historyManager.remove(k);
        }
        subtasks.clear();
    }

    @Override
    public void removeSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtaskIdList().remove(Integer.valueOf(id));
        subtasks.remove(id);
        historyManager.remove(id);
        findEpicStatus(epic);
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


