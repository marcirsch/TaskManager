package com.example.marcell.taskmanager.Cloud;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dropbox.core.v2.files.FileMetadata;
import com.example.marcell.taskmanager.Data.TaskDescriptor;
import com.example.marcell.taskmanager.Data.TaskUpdateEvent;
import com.example.marcell.taskmanager.DataBase.TaskDBHandler;

public class CloudService extends Service {
    private static final String TAG = CloudService.class.getSimpleName();

    private final IBinder binder = new CloudBinder();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void updateTaskInDB(TaskDescriptor task) {
        TaskDBHandler dbHandler = TaskDBHandler.getInstance(this);
        dbHandler.addTask(task);
    }

    public void upload(DropboxUtil dropbox, final TaskDescriptor task, String remoteFolder, int delay) {
        task.start(delay);
        updateTaskInDB(task);

        DropboxUtil.UploadFile uploadFile = dropbox.new UploadFile(delay, new DropboxUtil.OnAsyncTaskEventListener<FileMetadata>() {
            @Override
            public void onStart() {
                task.start(0);
                updateTaskInDB(task);

                TaskUpdateEvent.notifyOnDataUpdate(task);
                Log.i(TAG, "Starting upload: " + task.getName());
            }

            @Override
            public void onSuccess(FileMetadata object) {
                task.completed();
                updateTaskInDB(task);

                TaskUpdateEvent.notifyOnDataUpdate(task);
                Log.i(TAG, "Successful upload!");
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();

                task.failed();
                updateTaskInDB(task);

                TaskUpdateEvent.notifyOnDataUpdate(task);
            }

            @Override
            public void onProgress(int percentage) {
                task.progress(percentage);
                updateTaskInDB(task);

                TaskUpdateEvent.notifyOnDataUpdate(task);
            }
        });

        uploadFile.execute(task.getFilePath(), remoteFolder);


        //TODO chunked upload
        Log.i(TAG, "Upload task with id: " + task.getId());
    }

    public class CloudBinder extends Binder {
        CloudService getService() {
            return CloudService.this;
        }
    }
}