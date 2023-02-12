package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Epic extends Task {

//Я решила все методы связанные с удалением, сохранением и очисткой списка подзадач оставить в менеджере, т.к.
// эти методы удаляют не только в эпике, но и в мапе подзадач и обновляют статус эпика при любой операции с подзадачей

    public List<Integer> subtaskIdList = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }
    public Epic(Integer id, String name, String description, Status status) {
        super(id, name, description, status);}
    public Epic(Integer id, TypeTask type, String name, String description, Status status) {
        super(id, type, name, description, status);
    }

    public List<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void setSubtaskIdList(List<Integer> subtaskIdList) {
        this.subtaskIdList = subtaskIdList;
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
        return subtaskIdList.equals(epic.subtaskIdList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIdList);
    }
}
