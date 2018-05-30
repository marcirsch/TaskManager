package com.example.marcell.taskmanager.Notification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.marcell.taskmanager.DataBase.TaskDBHandler;
import com.example.marcell.taskmanager.Data.TaskDescriptor;
import com.example.marcell.taskmanager.Data.TaskUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class TaskNotificationService extends Service {
    private static final String TAG = TaskNotification.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        EventBus.getDefault().register(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TaskUpdateEvent taskUpdateEvent) {
        int ID = taskUpdateEvent.ID;

        Log.d(TAG, "Eventbus: Update  notification with ID: " + ID);

        TaskDBHandler dbHandler = TaskDBHandler.getInstance(this);
        TaskDescriptor task = dbHandler.getTask(ID);

        NotificationCompat.Builder notification = TaskNotification.createBuilder(this, task);
        TaskNotification.show(notification, task);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
