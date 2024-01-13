package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Класс Epic представляет эпик как частную форму задачи (большая задача).
 * Эпик состоит из подзадач и имеет дополнительные атрибуты, такие как список идентификаторов подзадач
 * и дата окончания.
 * Атрибуты:
 * - subtaskIdList: список идентификаторов подзадач, связанных с эпиком
 * - endTime: дата и время окончания эпика
 * Примечание: Класс Epic наследует атрибуты и методы от класса Task.
 */
public class Epic extends Task {

    private List<Integer> subtaskIdList = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        this.type = TypeTask.EPIC;
    }

    public Epic(Integer id, String name, String description, Status status) {
        super(id, name, description, status);
        this.type = TypeTask.EPIC;
    }

    public Epic(Integer id, String name, String description, Status status, LocalDateTime startTime, long duration) {
        super(id, name, description, status, startTime, duration);
        this.type = TypeTask.EPIC;
    }

    public Epic() {
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        this.type = TypeTask.EPIC;
    }

    public List<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void setSubtaskIdList(List<Integer> subtaskIdList) {
        this.subtaskIdList = subtaskIdList;
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubtask(Integer id) {
        subtaskIdList.add(id);
    }

    @Override
    public String toString() {
        return id + "," + TypeTask.EPIC + "," + name + "," + status + ","
                + description + "," + getStartTime() + "," + getDuration() + ",";

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIdList, epic.subtaskIdList) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIdList, endTime);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}