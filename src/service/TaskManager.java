package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {


    //Task
    List<Task> getHistory();

    void add(Task task);

    void saveTask(Task task);

    List<Task> getListAllTask();

    void removeAllTask();

    Task getTaskById(Integer id);

    void updateTask(Task task);

    void removeTaskById(Integer id);

    //Epic
    void saveEpic(Epic epic);

    List<Epic> getListAllEpic();

    void removeAllEpic();

    Epic getEpicById(Integer id);

    void updateEpic(Epic epic);

    void removeEpicById(Integer id);

    //Subtask
    void saveSubtask(Integer epicId, Subtask subtask);

    Subtask getSubtaskById(Integer id);

    List<Subtask> getSubtasks();

    void updateSubtask(Subtask subtask);

    void deleteSubtasks();

    void removeSubtaskById(Integer id);

    List<Subtask> getEpicSubtasks(Integer EpicId);

}
