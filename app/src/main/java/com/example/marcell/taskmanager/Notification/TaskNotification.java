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
import com.example.marcell.taskmanager.R;
import com.example.marcell.taskmanager.UI.TaskManagerActivity;


public final class TaskNotification {
    public static final String ACTION_PAUSE = "Pause upload";
    public static final String NOTIFICATION_ID = "Notification ID";
    public static final String ACTION_RESUME = "Resume upload";
    public static final String MSG_KEY_ID = "msg ID key";
    public static final String MSG_KEY_ACTION = "msg ID key";
    private static final String CHANNEL_ID = "TaskManagerNotifications";

    public static NotificationCompat.Builder createBuilder(Context context, TaskDescriptor task) {

        //safe to repeat, needed forOreo compatibility
        createNotificationChannel(context);

        PendingIntent tapIntent = getPendingIntent(context, TaskManagerActivity.class);
        PendingIntent resumeIntent = getResumeIntent(context, TaskNotificationService.class, task.getId());
        PendingIntent pauseIntent = getPauseIntent(context, TaskNotificationService.class, task.getId());

        String taskStatusString = TaskDescriptor.getStatusString(task.getTaskStatus());

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(task.getName())
                .setContentText(taskStatusString)
                .setContentIntent(tapIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (task.getTaskStatus() == TaskDescriptor.TaskStatus.IN_PROGRESS) {
            notification.setProgress(100, task.getCompletionPercentage(), false);
            notification.setOngoing(true);
        }

        if(task.getTaskStatus() == TaskDescriptor.TaskStatus.PAUSED || task.getTaskStatus() == TaskDescriptor.TaskStatus.IN_PROGRESS){
            notification.addAction(R.drawable.ic_add_white_24dp, "Pause", pauseIntent);
            notification.addAction(R.drawable.ic_add_white_24dp, "Resume", resumeIntent);
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

    private static PendingIntent getPauseIntent(Context context, Class<?> cls, int notificationId) {
        Intent pauseIntent = new Intent(context, cls);
        pauseIntent.setAction(ACTION_PAUSE);
        pauseIntent.putExtra(NOTIFICATION_ID, notificationId);

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    private static PendingIntent getResumeIntent(Context context, Class<?> cls, int notificationId) {
        Intent resumeIntent = new Intent(context, cls);
        resumeIntent.setAction(ACTION_RESUME);
        resumeIntent.putExtra(NOTIFICATION_ID, notificationId);


        PendingIntent pendingIntent = PendingIntent.getService(context, 0, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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