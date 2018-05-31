package com.example.marcell.taskmanager.Data;

import android.util.Log;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@DatabaseTable(tableName = "task_descriptor")
public class TaskDescriptor implements Serializable {
    private static final String TAG = TaskDescriptor.class.getSimpleName();

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String description;

    @DatabaseField
    private String filePath;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private SerializedList<String> keywordList;

    @DatabaseField
    private TaskStatus taskStatus;

    @DatabaseField
    private long completionTime;

    @DatabaseField
    private long startTime;

    @DatabaseField
    private int completionPercentage;

    public TaskDescriptor() {
        this.name = "";
        this.description = "";
        this.filePath = "";
        this.keywordList = new SerializedList<>();
        this.taskStatus = TaskStatus.PENDING;
        this.completionPercentage = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<String> getKeywordList() {
        return keywordList;
    }

    public void setKeywordList(SerializedList<String> keywordList) {
        if (keywordList != null) {
            this.keywordList = keywordList;
        }
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        return "TaskDescriptor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", filePath='" + filePath + '\'' +
                ", keywordList=" + keywordList +
                ", taskStatus=" + taskStatus +
                ", completionTime='" + completionTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", completionPercentage=" + completionPercentage +
                '}';
    }

    public void start(int delay) {
        this.startTime = System.currentTimeMillis() + (long) delay;
        if (delay > 0) {
            this.taskStatus = TaskStatus.POSTPONED;
        } else if (delay == 1) {
            this.taskStatus = TaskStatus.IN_PROGRESS;
        } else {
            Log.e(TAG, "Delay cannot be less than 0");
        }

        this.completionPercentage = 0;
        this.completionTime = 0;
    }

    public void progress(int percentage) {
        this.completionPercentage = percentage;
        this.completionTime = System.currentTimeMillis() - this.startTime;
        this.taskStatus = TaskStatus.IN_PROGRESS;
    }

    public void pause() {
        this.taskStatus = TaskStatus.PAUSED;
    }

    public void failed() {
        this.taskStatus = TaskStatus.FAILED;
    }

    public void complete() {
        this.taskStatus = TaskStatus.DONE;
        this.completionTime = System.currentTimeMillis() - this.startTime;
        this.completionPercentage = 100;
    }


    public enum TaskStatus {
        PENDING,
        POSTPONED,
        PAUSED,
        IN_PROGRESS,
        DONE,
        FAILED
    }

    public static String getStatusString(TaskDescriptor.TaskStatus status) {
        String statusString = "";
        switch (status) {
            case PENDING:
                statusString = "Pending";
                break;
            case POSTPONED:
                statusString = "Postponed";
                break;
            case PAUSED:
                statusString = "Paused";
                break;
            case IN_PROGRESS:
                statusString = "In progress";
                break;
            case DONE:
                statusString = "Completed";
                break;
            case FAILED:
                statusString = "Failed";
            default:
                break;
        }
        return statusString;
    }

    public static class SerializedList<E> extends ArrayList<E> implements Serializable {
    }
}
