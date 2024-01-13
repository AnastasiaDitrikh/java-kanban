package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс Subtask представляет подзадачу как частную форму задачи.
 * Подзадача является задачей, связанной с определенным эпиком, и имеет дополнительный атрибут - идентификатор эпика.
 * Атрибуты:
 * - epicId: идентификатор эпика, к которому относится подзадача
 */
public class Subtask extends Task {

    private Integer epicId;

    public Subtask(String name, String description, Status status) {
        super(name, description, status);
        this.type = TypeTask.SUBTASK;
    }

    public Subtask(Integer id, String name, String description, Status status, Integer epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
        this.type = TypeTask.SUBTASK;
    }

    public Subtask(Integer id, String name, String description, Status status, Integer epicId, LocalDateTime startTime, long duration) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
        this.type = TypeTask.SUBTASK;
    }

    public Subtask(String name, String description, LocalDateTime startTime, long duration, Integer epicId) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
        this.type = TypeTask.SUBTASK;
    }

    public Subtask() {
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return id + "," + TypeTask.SUBTASK + "," + name + "," + status + "," + description + "," +
                startTime + "," + duration + "," + epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
