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

    private CloudHandler(final Context context) {
        this.context = context;
        String accessToken = UserPreferences.getUserToken(context);
        Log.d(TAG, "token: " + accessToken);

        authenticateUser();
    }

    public synchronized static CloudHandler getInstance(Context context) {
        if (instance == null) {
            instance = new CloudHandler(context);

        }
        return instance;
    }

    public void upload(int ID, int delay) {
        TaskDBHandler dbHandler = TaskDBHandler.getInstance(context);
        TaskDescriptor task = dbHandler.getTask(ID);

        upload(task, UserPreferences.getRemoteFolder(context), delay);
    }


    public void upload(TaskDescriptor task, String remoteFolder, int delay) {
        CloudBindHelper cloudBindHelper = CloudBindHelper.getInstance();
        if (cloudBindHelper.isBound()) {
            cloudBindHelper.getCloudService().upload(task, remoteFolder, delay);
        } else {
            Log.e(TAG, "CloudService not bound");
        }
    }


    public void authenticateUser() {
        DropboxUtil dropbox = new DropboxUtil(context);
        DropboxUtil.GetUserNameTask userNameTask = dropbox.new GetUserNameTask(new DropboxUtil.OnAsyncTaskEventListener<String>() {
            @Override
            public void onStart() {
            }

            @Override
            public void onPause() {
                //cannot pause
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
