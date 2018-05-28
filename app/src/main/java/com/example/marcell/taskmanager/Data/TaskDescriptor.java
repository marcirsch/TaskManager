package com.example.marcell.taskmanager.Data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@DatabaseTable(tableName = "task_descriptor")
public class TaskDescriptor implements Serializable {

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
    private String completionTime;

    @DatabaseField
    private String startTime;

    @DatabaseField
    private int completionPercentage;

    public TaskDescriptor() {
        this.name = "task";
        this.description = "description";
        this.filePath = null;
        this.keywordList = null;
        this.taskStatus = TaskStatus.PENDING;
        this.completionPercentage = 0;
    }

    public int getId() {

        return id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(String completionTime) {
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

    public void setKeywordList(ArrayList<String> keywordList) {
        this.keywordList = (SerializedList<String>) keywordList;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public enum TaskStatus {
        PENDING,
        IN_PROGRESS,
        DONE,
        FAILED
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

    public static class SerializedList<E> extends ArrayList<E> implements Serializable {
    }
}
