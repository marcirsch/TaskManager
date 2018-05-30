package com.example.marcell.taskmanager.Cloud;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class CloudBindHelper {

    private static CloudService cloudService;
    private static boolean bound = false;

    private static CloudBindHelper instance;
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CloudService.CloudBinder binder = (CloudService.CloudBinder) service;
            cloudService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    private CloudBindHelper(){
    }

    public static CloudService getCloudService() {
        return cloudService;
    }

    public static CloudBindHelper getInstance() {
        if(instance == null){
            instance = new CloudBindHelper();
        }
        return instance;
    }

    public void bindToService(Context context){
        Intent intent = new Intent(context, CloudService.class);
        context.bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }

    public void unbindFromService(Context context){
        bound = false;
        context.unbindService(connection);
    }

    public boolean isBound() {
        return bound;
    }
}
