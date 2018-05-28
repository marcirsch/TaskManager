package com.example.marcell.taskmanager.Data;

public final class UserPreferences {

    private static String dBxUploadFolder = "/UploadFolder";
    private static String DROPBOX_ACCESS_TOKEN = "RPemIELM5LAAAAAAAAAABiQtW5mXhNaXPiESSLBfk322sGonRQB3GK-YtTmq2Thb";

    private static UserPreferences instance;

    private UserPreferences() {
    }

    public static UserPreferences getInstance() {
        if(instance == null){
            instance = new UserPreferences();
        }
        return instance;
    }

    public static String getdBxUploadFolder() {
        return dBxUploadFolder;
    }

    public static void setdBxUploadFolder(String dBxUploadFolder) {
        UserPreferences.dBxUploadFolder = dBxUploadFolder;
    }

    public static String getDropboxAccessToken() {
        return DROPBOX_ACCESS_TOKEN;
    }

    public static void setDropboxAccessToken(String dropboxAccessToken) {
        DROPBOX_ACCESS_TOKEN = dropboxAccessToken;
    }
}
