package com.example.marcell.taskmanager.Cloud;

import android.content.Context;
import android.util.Log;

import com.dropbox.core.v2.files.FileMetadata;
import com.example.marcell.taskmanager.DataBase.TaskDBHandler;
import com.example.marcell.taskmanager.Data.TaskDescriptor;
import com.example.marcell.taskmanager.Data.TaskUpdateEvent;
import com.example.marcell.taskmanager.Data.UserPreferences;

public final class CloudHandler {
    private static final String TAG = CloudHandler.class.getSimpleName();

    private static CloudHandler instance;

    private static Context context;
    private static DropboxUtil dropbox;

    private CloudHandler(final Context context) {
        this.context = context;
        String accessToken = UserPreferences.getUserToken(context);
        Log.d(TAG, "token: " + accessToken);
        dropbox = new DropboxUtil(accessToken);

        authenticateUser();
    }

    public synchronized static CloudHandler getInstance(Context context) {
        if (instance == null) {
            instance = new CloudHandler(context);

        }
        return instance;
    }

    private void updateTaskInDB(TaskDescriptor task) {
        TaskDBHandler dbHandler = TaskDBHandler.getInstance(context);
        dbHandler.addTask(task);
    }



    public void upload(int ID, int delay) {
        TaskDBHandler dbHandler = TaskDBHandler.getInstance(context);

        TaskDescriptor task = dbHandler.getTask(ID);
        task.start(delay);
        updateTaskInDB(task);

        upload(task, UserPreferences.getRemoteFolder(context), delay);
    }


    public void upload(final TaskDescriptor task, String remoteFolder, int delay) {

        task.start(delay);
        updateTaskInDB(task);
        TaskUpdateEvent.notifyOnDataUpdate(task);

        DropboxUtil.UploadFileTask uploadFileTask = dropbox.new UploadFileTask(delay, new DropboxUtil.OnAsyncTaskEventListener<FileMetadata>() {
            @Override
            public void onStart() {
                task.start(0);
                updateTaskInDB(task);

                TaskUpdateEvent.notifyOnDataUpdate(task);

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

        uploadFileTask.execute(task.getFilePath(), remoteFolder);
    }


    public void authenticateUser() {
        DropboxUtil.GetUserNameTask userNameTask = dropbox.new GetUserNameTask(new DropboxUtil.OnAsyncTaskEventListener<String>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(String object) {
//                showToast("Dropbox authentication successful. Name:" + object);
                Log.i(TAG, "Successful Dropbox authentication, user name: " + object);
                UserPreferences.setAuthenticationSuccessful(true);
            }

            @Override
            public void onFailed(Exception e) {
                Log.i(TAG, "Dropbox authentication failed");
                e.printStackTrace();
                UserPreferences.setAuthenticationSuccessful(true);
            }

            @Override
            public void onProgress(int percentage) {
            }
        });
        userNameTask.execute();
    }
}
