package tasks;

import java.time.LocalDateTime;

import java.util.Objects;

public class Task {


    protected Integer id;
    protected String name;
    protected String description;
    protected Status status;
    protected long duration;
    protected LocalDateTime startTime;

    protected TypeTask type;



    public Task(String name, String description, LocalDateTime startTime, long duration) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.status = Status.NEW;
        this.type = TypeTask.TASK;
    }

    public Task(Integer id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TypeTask.TASK;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TypeTask.TASK;
    }

    public Task(Integer id, String name, String description, Status status, long duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.type = TypeTask.TASK;
    }

    public Task(Integer id, String name, String description, Status status, LocalDateTime startTime, long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.type = TypeTask.TASK;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.type = TypeTask.TASK;
    }

    public Task() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        if (!(startTime == null) && duration != 0) {
            return startTime.plusMinutes(duration);
        }
        return null;
    }

    public TypeTask getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return duration == task.duration && Objects.equals(id, task.id) && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status, duration, startTime);
    }

    @Override
    public String toString() {
        return id + "," + TypeTask.TASK + "," + name + "," + status + "," + description + "," +
                startTime + "," + duration + ",";
    }

}

