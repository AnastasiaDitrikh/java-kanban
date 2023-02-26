package tasks;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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
                + description + "," +  getStartTime() + "," + getDuration() + ",";

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIdList, epic.subtaskIdList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIdList);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
