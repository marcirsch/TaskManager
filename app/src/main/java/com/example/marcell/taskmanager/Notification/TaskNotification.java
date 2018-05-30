package com.example.marcell.taskmanager.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.marcell.taskmanager.Data.TaskDescriptor;
import com.example.marcell.taskmanager.Data.UserPreferences;
import com.example.marcell.taskmanager.R;
import com.example.marcell.taskmanager.UI.TaskManagerActivity;


public final class TaskNotification {
    private static final String CHANNEL_ID = "TaskManagerNotifications";

    public static NotificationCompat.Builder createBuilder(Context context, TaskDescriptor task) {

        //safe to repeat, needed forOreo compatibility
        createNotificationChannel(context);

        PendingIntent pendingIntent = getPendingIntent(context, TaskManagerActivity.class);

        String taskStatusString = TaskDescriptor.getStatusString(task.getTaskStatus());

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(task.getName())
                .setContentText(taskStatusString)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (task.getTaskStatus() == TaskDescriptor.TaskStatus.IN_PROGRESS) {
            notification.setProgress(100, task.getCompletionPercentage(), false);
            notification.setOngoing(true);
        }

        return notification;
    }


    public static void show(NotificationCompat.Builder notification, TaskDescriptor task) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(notification.mContext);
        notificationManager.notify(task.getId(), notification.build());
    }

    private static PendingIntent getPendingIntent(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(cls);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}