package com.example.icsp.checklist;
/**
 * ChecklistModel Model Class
 * <p>
 * Structure of every checklist task:
 * Each task has the actual task, deadline, volunteer, time, id, recipient and status whether it is complete or not
 * Also has the necessary getters and setters.
 */
public class ChecklistModel{
    private String task, deadline, volunteer, time, taskId, recipient;
    private int status;

    //Required empty constructor for Firebase -- DO NOT remove
    public ChecklistModel() {
    }
    public ChecklistModel(String task, String deadline, String volunteer, String time, String taskId, String recipient, int status) {
        this.task = task;
        this.deadline = deadline;
        this.volunteer = volunteer;
        this.time = time;
        this.status = status;
        this.taskId = taskId;
        this.recipient = recipient;
    }

    public String getTask() {
        return task;
    }
    public String getDeadline() {
        return deadline;
    }
    public String getVolunteer() {
        return volunteer;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public int getStatus() {
        return status;
    }
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getRecipient() {
        return recipient;
    }
}
