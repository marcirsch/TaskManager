package com.example.marcell.taskmanager.Data;

import org.greenrobot.eventbus.EventBus;

public class TaskUpdateEvent {
    public int ID;
    public TaskUpdateEvent(int ID) {
        this.ID = ID;
    }

    public static void notifyOnDataUpdate(TaskDescriptor task){
        EventBus.getDefault().post(new TaskUpdateEvent(task.getId()));
    }
}
