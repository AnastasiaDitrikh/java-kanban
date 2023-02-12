package model;

import java.util.Objects;

public class Subtask extends Task {

    Integer epicId;

    public Subtask(String name, String description, Status status) {
        super(name, description, status);
    }

   public Subtask(Integer id, String name, String description, Status status) {
       super(id, name, description, status);
    }

    public Subtask(Integer id, TypeTask type, String name, String description, Status status) {
        super(id, type, name, description, status);
    }
public Subtask(Integer id, TypeTask type, String name, String description, Status status, Integer epicId) {
            super(id, type, name, description, status);
            this.epicId=epicId;
    }

    @Override
    public String toString() {
        return  super.toString()+getEpicId();
    }
    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId.equals(subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
