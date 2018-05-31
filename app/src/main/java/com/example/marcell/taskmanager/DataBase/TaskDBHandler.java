package com.example.marcell.taskmanager.DataBase;

import android.content.Context;

import com.example.marcell.taskmanager.Data.TaskDescriptor;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class TaskDBHandler implements Serializable{

    private static TaskDBHandler instance;

    private static TaskDBHelper taskDBHelper = null;
    private static RuntimeExceptionDao<TaskDescriptor,Integer> taskDBDao;


    private TaskDBHandler(Context context) {
        if(taskDBHelper == null) {
            taskDBHelper = (TaskDBHelper) OpenHelperManager.getHelper(context, TaskDBHelper.class);
            taskDBDao = taskDBHelper.getExceptionDao();
        }
    }

    public static synchronized TaskDBHandler getInstance(Context context){
        if(instance == null){
            instance = new TaskDBHandler(context);
        }

        return instance;
    }

    public static TaskDescriptor getTask(int ID){
        return taskDBDao.queryForId(ID);
    }

    public static List<TaskDescriptor> getAllTasks(){
        List<TaskDescriptor> taskDescriptors;

        taskDescriptors = taskDBDao.queryForAll();
        return taskDescriptors;
    }

    public static List<TaskDescriptor> getTasks(TaskDescriptor.TaskStatus taskStatus){
        List<TaskDescriptor> returnList = new ArrayList<>();
        List<TaskDescriptor> taskDescriptors = getAllTasks();

        for(TaskDescriptor task : taskDescriptors){
            if(task.getTaskStatus() == taskStatus){
                returnList.add(task);
            }
        }

        return returnList;
    }

    public static List<TaskDescriptor> getTasksExcept(TaskDescriptor.TaskStatus exceptTaskStatus){
        List<TaskDescriptor> returnList = new ArrayList<>();
        List<TaskDescriptor> taskDescriptors = getAllTasks();

        for(TaskDescriptor task : taskDescriptors){
            if(task.getTaskStatus() != exceptTaskStatus){
                returnList.add(task);
            }
        }

        return returnList;
    }

    public void addTask(TaskDescriptor item){
        taskDBDao.createOrUpdate(item);
    }

    public void removeItem(TaskDescriptor task){
            taskDBDao.delete(task);
    }

    public synchronized static void close(){
        if(instance != null) {
            instance = null;
            OpenHelperManager.releaseHelper();
            taskDBHelper = null;
            taskDBDao = null;
        }
    }
}
