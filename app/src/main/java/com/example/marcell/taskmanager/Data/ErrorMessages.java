package com.example.marcell.taskmanager.Data;

import android.content.Context;

import com.example.marcell.taskmanager.R;

public final class ErrorMessages {
    public static final int INVALID_NAME = -2;
    public static final int INVALID_FILE_PATH = -3;
    public static final int INVALID_KEYWORD = -4;

    public static String getErrorMessage(Context context, int msgType) {
        String errorMessage;

        switch (msgType) {
            case INVALID_NAME:
                errorMessage = context.getString(R.string.edit_task_invalid_name);
                break;
            case INVALID_FILE_PATH:
                errorMessage = context.getString(R.string.edit_task_invalid_filepath);
                break;
            case INVALID_KEYWORD:
                errorMessage = context.getString(R.string.edit_task_invalid_keywords);
                break;
            default:
                errorMessage = "Unknown error";
                break;
        }

        return errorMessage;
    }
}
