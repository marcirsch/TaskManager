package com.example.marcell.taskmanager;

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

import com.example.marcell.taskmanager.Data.TaskDBHandler;
import com.example.marcell.taskmanager.Data.TaskDBHelper;
import com.example.marcell.taskmanager.Data.TaskDescriptor;
import com.example.marcell.taskmanager.Utils.CustomViewPager;
import com.example.marcell.taskmanager.Utils.TabAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProcessActivity extends AppCompatActivity {

    private static final int EDIT_TASK_DESCRIPTOR_REQUEST = 1;
    private static final int ADD_TASK_DESCRIPTOR_REQUEST = 2;
    private static final String ON_SAVE_INSTANCE_TASK_LIST_KEY = "onSaveInstanceTaskList";
    private static final String ON_SAVE_INSTANCE_SELECTEDBUNDLE_KEY = "onSaveInstanceSelectedBundle";


    public Toast toast;


    private TabAdapter mTabAdapter;
    private CustomViewPager mViewPager;

    //Handler for fragment callback
    private List<TabsOnDataUpdateListener> tabListeners;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Notify tabs on new data
        tabListeners = new ArrayList<>();

        mTabAdapter = new TabAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mTabAdapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.fab) {
                    startAddActivity();
                }
            }
        });

        this.requestPermission();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TaskDBHandler.close();
    }

    //    public static TaskDescriptorList loadTaskData() {
//        TaskDescriptorList tdl = new TaskDescriptorList(this);
//
//        TaskDescriptor[] dummydata = new TaskDescriptor[10];
//        for (int i = 0; i < dummydata.length; i++) {
//            dummydata[i] = new TaskDescriptor();
//            dummydata[i].setName("Task " + String.valueOf(i));
//            if (i % 2 == 0) {
//                dummydata[i].setTaskStatus(TaskDescriptor.TaskStatus.PENDING);
//            } else {
//                dummydata[i].setTaskStatus(TaskDescriptor.TaskStatus.DONE);
//            }
//        }
//        tdl.setTaskDescriptors(dummydata);
//        return tdl;
//    }

    protected void handleTaskClick(TaskDescriptor task) {
        showToast("TODO: Edit task ");
    }

    protected void handleTaskRightSwipe(TaskDescriptor task) {
//        showToast("Right swipe on task: " + task.getName());

    }

    protected void handleTaskLeftSwipe(TaskDescriptor task) {
        showToast("TODO: Postpone task");
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
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


    private void startAddActivity() {
        Intent intent = new Intent(ProcessActivity.this, AddEditTaskActivity.class);
        startActivityForResult(intent, ADD_TASK_DESCRIPTOR_REQUEST);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == ADD_TASK_DESCRIPTOR_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
//            if (data.hasExtra("TaskDescriptor")) {
//                TaskDescriptor taskDescriptor = (TaskDescriptor) data.getExtras().getSerializable("TaskDescriptor");
//                taskDescriptorList.addOrUpdate(taskDescriptor);
//                //send bundle to RecycleViews
//                notifyTabsOnDataUpdate();
////                taskDataUpdate.notifyTabs(bundle);
//            }
//        }
    }


    public void notifyTabsOnDataUpdate() {
        for (TabsOnDataUpdateListener tabs : tabListeners) {
            tabs.onDataUpdate();
        }
    }

    //TODO save instance state
//    @Override
//    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
////        logAndAppend()
////        outState.putSerializable(ON_SAVE_INSTANCE_TASK_LIST_KEY, taskDescriptorList);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_process, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }



    /**
     * Interface used to notify tabs on new data
     */
    public interface TabsOnDataUpdateListener {
        void onDataUpdate();
    }
}