package pl.michaloruba.todolist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TodoItem {
    private String id;
    private String description;
    private TodoStatus status;
    private Date createDate;
    private Date realizationDate;
    private TodoPriority priority;
    private List<String> attachments;

    public TodoItem(String id, String description, Date createDate, Date realizationDate, TodoPriority priority, TodoStatus status) {
        this.id = id;
        this.description = description;
        this.status = TodoStatus.NEW;
        this.createDate = createDate;
        this.realizationDate = realizationDate;
        this.priority = priority;
        this.status = status;
        this.attachments = new ArrayList<>();
    }

    public Long getId() {
        return Long.parseLong(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TodoStatus getStatus() {
        return status;
    }

    public void setStatus(TodoStatus status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getRealizationDate() {
        return realizationDate;
    }

    public void setRealizationDate(Date realizationDate) {
        this.realizationDate = realizationDate;
    }

    public TodoPriority getPriority() {
        return priority;
    }

    public void setPriority(TodoPriority priority) {
        this.priority = priority;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoItem todoItem = (TodoItem) o;
        return status == todoItem.status &&
                Objects.equals(createDate, todoItem.createDate) &&
                Objects.equals(realizationDate, todoItem.realizationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, createDate, realizationDate);
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "description='" + description + '\'' +
                ", status=" + status +
                ", createDate=" + createDate +
                ", realizationDate=" + realizationDate +
                ", priority=" + priority +
                ", attachments=" + attachments +
                '}';
    }

    public void toggleStatus() {
        if (TodoStatus.NEW.equals(status)){
            status = TodoStatus.DONE;
        }
        else {
            status = TodoStatus.NEW;
        }
    }
}
