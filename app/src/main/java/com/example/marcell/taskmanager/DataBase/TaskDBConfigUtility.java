package com.example.marcell.taskmanager.DataBase;

import com.example.marcell.taskmanager.Data.TaskDescriptor;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

public class TaskDBConfigUtility extends OrmLiteConfigUtil {

    private static final Class<?>[] classes = new Class[]{
            //Array of classes to be stored in database
            TaskDescriptor.class
    };

    public static void main(String[] args) throws IOException, SQLException {
        //Generate task_database_config.xml configuration file
        writeConfigFile("task_database_config.txt", classes);
    }
}
