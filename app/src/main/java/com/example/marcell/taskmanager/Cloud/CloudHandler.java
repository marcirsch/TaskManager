package com.example.marcell.taskmanager.Cloud;

import android.content.Context;
import android.util.Log;

import com.dropbox.core.v2.files.FileMetadata;
import com.example.marcell.taskmanager.Data.UserPreferences;
import com.example.marcell.taskmanager.Data.TaskDBHandler;
import com.example.marcell.taskmanager.Data.TaskDescriptor;

public final class CloudHandler {
    private static final String TAG = CloudHandler.class.getSimpleName();

    private static CloudHandler instance;

    private static Context context;
    private static DropboxUtil dropbox;

    private CloudHandler(final Context context){
        this.context = context;
        dropbox = new DropboxUtil(UserPreferences.getDropboxAccessToken());

        authenticateUser();
    }

    public synchronized static CloudHandler getInstance(Context context) {
        if(instance == null){
            instance = new CloudHandler(context);

        }
        return instance;
    }

    public static void upload(int ID){
        TaskDBHandler dbHandler = TaskDBHandler.getInstance(context);

        TaskDescriptor task = TaskDBHandler.getTask(ID);
        upload(task, UserPreferences.getdBxUploadFolder());
    }



    public static void upload(final TaskDescriptor task, String remoteFolder) {
        task.setTaskStatus(TaskDescriptor.TaskStatus.IN_PROGRESS);
        updateTaskInDB(task);

        DropboxUtil.UploadFileTask uploadFileTask = dropbox.new UploadFileTask(context, new DropboxUtil.OnAsyncTaskEventListener<FileMetadata>() {
            @Override
            public void OnSuccess(FileMetadata object) {
                task.setTaskStatus(TaskDescriptor.TaskStatus.DONE);
                task.setCompletionPercentage(100);

                updateTaskInDB(task);
                Log.i(TAG,"Successful upload!");

                //TODO Eventbus notification
            }

            @Override
            public void OnFailure(Exception e) {
                e.printStackTrace();
                task.setTaskStatus(TaskDescriptor.TaskStatus.FAILED);

                updateTaskInDB(task);

                //TODO Eventbus notification
            }

            @Override
            public void OnProgress(int percentage) {
                task.setCompletionPercentage(percentage);

                updateTaskInDB(task);

                //TODO Eventbus notification
            }
        });

        uploadFileTask.execute(task.getFilePath(), remoteFolder);
    }

    private static void updateTaskInDB(TaskDescriptor task){
        TaskDBHandler dbHandler = TaskDBHandler.getInstance(context);
        dbHandler.addTask(task);
    }


    public static void authenticateUser(){
        DropboxUtil.GetUserNameTask userNameTask = dropbox.new GetUserNameTask(context, new DropboxUtil.OnAsyncTaskEventListener<String>() {
            @Override
            public void OnSuccess(String object) {
//                showToast("Dropbox authentication successful. Name:" + object);
                Log.i(TAG,"Successful Dropbox authentication, user name: " + object);
            }

            @Override
            public void OnFailure(Exception e) {
                Log.i(TAG,"Dropbox authentication failed");
                e.printStackTrace();
            }

            @Override
            public void OnProgress(int percentage) {
            }
        });
        userNameTask.execute();
    }
}
