package com.example.marcell.taskmanager.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskDescriptorList implements Serializable{
    private List<TaskDescriptor> taskDescriptors;

    public TaskDescriptorList() {
        this.taskDescriptors = new ArrayList<>();
    }

    public TaskDescriptor[] getTaskDescriptors() {
        return taskDescriptors.toArray(new TaskDescriptor[taskDescriptors.size()]);
    }

    public void setTaskDescriptors(TaskDescriptor[] taskDescriptors) {
        this.taskDescriptors = new ArrayList<TaskDescriptor>(Arrays.asList(taskDescriptors));
    }

    public int getLength(){
        return taskDescriptors.size();
    }
    public void append(TaskDescriptor item){
        taskDescriptors.add(item);
    }

    public void removeItem(int index){
        if(index > 0 && index < taskDescriptors.size()){
            taskDescriptors.remove(index);
        }
    }
}
