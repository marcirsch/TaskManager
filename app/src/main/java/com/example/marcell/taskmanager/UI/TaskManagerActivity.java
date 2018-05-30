package com.example.marcell.taskmanager.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.marcell.taskmanager.Cloud.CloudHandler;
import com.example.marcell.taskmanager.DataBase.TaskDBHandler;
import com.example.marcell.taskmanager.Data.TaskUpdateEvent;
import com.example.marcell.taskmanager.Data.UserPreferences;
import com.example.marcell.taskmanager.Notification.TaskNotificationService;
import com.example.marcell.taskmanager.R;
import com.example.marcell.taskmanager.UI.TaskManagerTabs.TabUtilities.CustomViewPager;
import com.example.marcell.taskmanager.UI.TaskManagerTabs.TabUtilities.TabAdapter;

import org.greenrobot.eventbus.EventBus;


public class TaskManagerActivity extends AppCompatActivity {
    public Toast toast;
    private TabAdapter mTabAdapter;
    private CustomViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskmanager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupTabs();

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.fab) {
                    Intent intent = new Intent(TaskManagerActivity.this, EditTaskActivity.class);
                    startActivityForResult(intent, EditTaskActivity.REQUEST_CODE);
                }
            }
        });


        //Start notification service
        startService(new Intent(TaskManagerActivity.this, TaskNotificationService.class));

        CloudHandler.getInstance(this).authenticateUser();

        this.requestPermission();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EditTaskActivity.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            if (data.hasExtra("TaskID")) {
                EventBus.getDefault().post(new TaskUpdateEvent(data.getIntExtra("TaskID", 0)));
            }
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            } else {
                UserPreferences.setStoragePermissionGranted();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("permission granted");
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_process, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(TaskManagerActivity.this, SettingsActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TaskDBHandler.close();
    }


    public void showToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void setupTabs() {
        mTabAdapter = new TabAdapter(getSupportFragmentManager());

        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mTabAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }
}