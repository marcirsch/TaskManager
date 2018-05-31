package com.example.marcell.taskmanager.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.marcell.taskmanager.R;

public final class UserPreferences {


    private static String dBxUploadFolder = "/UploadFolder";
    private static String dBxAccessToken = "RPemIELM5LAAAAAAAAAABiQtW5mXhNaXPiESSLBfk322sGonRQB3GK-YtTmq2Thb";


    private static boolean storagePermissionGranted = false;
    private static boolean authenticationSuccessful = false;
    private static int taskDelayedStartDelay = 5; //seconds

    private static SharedPreferences sharedPreferences;

    private static void getSharedPreferences(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getUserToken(Context context){
        getSharedPreferences(context);
        return sharedPreferences.getString("dropbox_access_token", context.getString(R.string.preferences_dropbox_token_default));
    }

    public static int getDelayedTaskTime(Context context){
        getSharedPreferences(context);
        return Integer.parseInt(sharedPreferences.getString("delayed_upload_time", context.getString(R.string.preferences_upload_default)));
    }

    public static String getRemoteFolder(Context context){
        getSharedPreferences(context);
        String folder= sharedPreferences.getString("dropbox_remote_folder", context.getString(R.string.preferences_dropbox_folder_default));
        if(folder.length() != 0) {
            if (folder.charAt(0) != '/') {
                folder = "/" + folder;
            }
        }
        Log.d("UserPreferences","Remote folder: " + folder );
        return folder;
    }



    public static boolean isAuthenticationSuccessful() {
        return authenticationSuccessful;
    }

    public static void setAuthenticationSuccessful(boolean authenticationSuccessful) {
        UserPreferences.authenticationSuccessful = authenticationSuccessful;
    }

    public static boolean isStoragePermissionGranted() {
        return storagePermissionGranted;
    }

    public static void setStoragePermissionGranted() {
        UserPreferences.storagePermissionGranted = true;
    }

    public static void resetStoragePermissionGranted() {
        UserPreferences.storagePermissionGranted = false;
    }



}
