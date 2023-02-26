package managers.tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {


    private Integer id;
    private TypeTask type;
    private String name;
    private String description;
    private Status status;

    private long duration;


    private LocalDateTime startTime;
    private LocalDateTime endTime;



    public Task(String name, String description, LocalDateTime startTime, long duration) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Integer id, TypeTask type, String name, String description, Status status) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @Override
    public String toString() {
        return id + "," + type + "," + name + "," + status + "," + description + ","+startTime+","+duration+",";
    }

    public Task(Integer id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() { //исправлено getname на getName
        return name;
    }

    public void setName(String name) { //исправлено setname на setName
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) { //исправлено setdescription на setDescription
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public TypeTask getTypeTask() {
        return type;
    }

    public void setTypeTask(TypeTask typeTask) {
        this.type = typeTask;
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

    public Task(Integer id, TypeTask type, String name, String description, Status status, long duration, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Task(Integer id, TypeTask type, String name, String description, Status status, LocalDateTime startTime, long duration) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return duration == task.duration && Objects.equals(id, task.id) && type == task.type && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status && Objects.equals(startTime, task.startTime) && Objects.equals(endTime, task.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, name, description, status, duration, startTime, endTime);
    }
}

