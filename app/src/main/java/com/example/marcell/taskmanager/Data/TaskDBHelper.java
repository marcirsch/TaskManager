package com.example.marcell.taskmanager.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.marcell.taskmanager.R;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public final class TaskDBHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = TaskDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "taskDescriptor.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<TaskDescriptor, Integer> taskDBDao = null;
    private RuntimeExceptionDao<TaskDescriptor, Integer> taskDBRuntimeDao = null;
    private ConnectionSource connectionSource = null;

    public TaskDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.task_database_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(TAG,"onCreate");
            TableUtils.createTableIfNotExists(connectionSource, TaskDescriptor.class);
        } catch (SQLException e) {
            Log.e(TAG, "onCreate: Could not create table");
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            if (oldVersion != newVersion) {
                TableUtils.dropTable(connectionSource, TaskDescriptor.class, true);
                onCreate(database, connectionSource);
            }
        } catch (SQLException e) {
            Log.e(TAG, "onUpgrade: Could not drop table");
            e.printStackTrace();
        }
    }

    public Dao<TaskDescriptor, Integer> getTaskDBDao() throws SQLException {
        if (taskDBDao == null) {
            taskDBDao = getDao(TaskDescriptor.class);
        }
        return taskDBDao;
    }

    public RuntimeExceptionDao<TaskDescriptor, Integer> getExceptionDao() {
        if (taskDBRuntimeDao == null) {
            taskDBRuntimeDao = getRuntimeExceptionDao(TaskDescriptor.class);
        }
        return taskDBRuntimeDao;
    }

    @Override
    public ConnectionSource getConnectionSource() {
        if (connectionSource == null) {
            connectionSource = super.getConnectionSource();
        }
        return connectionSource;
    }

    @Override
    public void close() {
        super.close();

        taskDBRuntimeDao = null;
        taskDBDao = null;
    }
}
