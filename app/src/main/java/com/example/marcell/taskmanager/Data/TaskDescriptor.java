package com.example.marcell.taskmanager.Data;

import android.net.Uri;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class TaskDescriptor implements Serializable{
    private String name;
    private String description;
    private String filePath;
    private List<String> keywordList;
    private TaskStatus taskStatus;
    private String completionTime;
    private int completionPercentage;

    public TaskDescriptor()  {
        this.name = "task";
        this.description = "description";
        this.filePath = null;
        this.keywordList = null;
        this.taskStatus = TaskStatus.PENDING;
        this.completionPercentage = 0;
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

    public void setKeywordList(List<String> keywordList) {
        this.keywordList = keywordList;
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
}
