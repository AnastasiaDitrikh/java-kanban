package managers.tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Epic extends Task {


    public List<Integer> subtaskIdList = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }
    public Epic(Integer id, String name, String description, Status status) {
        super(id, name, description, status);}
    public Epic(Integer id, TypeTask type, String name, String description, Status status) {
        super(id, type, name, description, status);
    }
    public Epic(Integer id, TypeTask type, String name, String description, Status status, LocalDateTime startTime,long duration) {
        super(id, type, name, description, status, startTime, duration);
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

    @Override
    public String toString() {
        return  super.toString();
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
}
