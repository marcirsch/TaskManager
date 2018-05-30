package com.example.marcell.taskmanager.UI.TaskManagerTabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.marcell.taskmanager.Cloud.CloudHandler;
import com.example.marcell.taskmanager.DataBase.TaskDBHandler;
import com.example.marcell.taskmanager.Data.TaskDescriptor;
import com.example.marcell.taskmanager.Data.TaskUpdateEvent;
import com.example.marcell.taskmanager.Data.UserPreferences;
import com.example.marcell.taskmanager.R;
import com.example.marcell.taskmanager.UI.EditTaskActivity;
import com.example.marcell.taskmanager.UI.TaskManagerActivity;
import com.example.marcell.taskmanager.UI.TaskManagerTabs.TaskRecyclerView.SwipeController;
import com.example.marcell.taskmanager.UI.TaskManagerTabs.TaskRecyclerView.TasksRVAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProgressTaskFragment extends Fragment implements TasksRVAdapter.TasksRVAOnClickListener {
    private static final String TAG = ProgressTaskFragment.class.getSimpleName();

    private TasksRVAdapter tasksRVAdapter;
    private RecyclerView tasksRecyclerView;

    private TaskManagerActivity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_progress_tasks, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.tv_section_label);
        textView.setText(getString(R.string.progress_view_title));

        Log.d(TAG,"onCreateView Inflate OK");

        parentActivity = ((TaskManagerActivity) getActivity());

        //Initialize RecyclerView
        tasksRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_task_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL,false);

        tasksRecyclerView.setLayoutManager(layoutManager);
        tasksRecyclerView.setHasFixedSize(true);

        tasksRVAdapter = new TasksRVAdapter(this);
        tasksRecyclerView.setAdapter(tasksRVAdapter);


        SwipeController swipeController = new SwipeController(new SwipeController.SwipeListener() {
            @Override
            public void onRightSwipe(int position) {
                if (position >= 0 && position < tasksRVAdapter.getTaskDescriptors().size()) {
                    TaskDescriptor rightSwipedTask = tasksRVAdapter.getTaskDescriptors().get(position);
                    parentActivity.showToast("Upload task: " + rightSwipedTask.getName());

                    CloudHandler cloudHandler = CloudHandler.getInstance(parentActivity);
                    cloudHandler.upload(rightSwipedTask.getId(), 0);
                }
            }

            @Override
            public void onLeftSwipe(int position) {
                if (position >= 0 && position < tasksRVAdapter.getTaskDescriptors().size()) {
                    TaskDescriptor leftSwipedTask = tasksRVAdapter.getTaskDescriptors().get(position);
                    parentActivity.showToast("Delayed task for " + UserPreferences.getDelayedTaskTime(parentActivity) + "seconds");

                    CloudHandler cloudHandler = CloudHandler.getInstance(parentActivity);
                    cloudHandler.upload(leftSwipedTask.getId(), UserPreferences.getDelayedTaskTime(parentActivity));
                }
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        EventBus.getDefault().register(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRVData();
    }


    @Override
    public void onClick(TaskDescriptor task, int position, View v) {
        Intent intent = new Intent(parentActivity, EditTaskActivity.class);
        intent.putExtra(TaskDescriptor.class.getSimpleName(),task.getId());
        startActivityForResult(intent, EditTaskActivity.REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EditTaskActivity.REQUEST_CODE && resultCode == RESULT_OK && data != null){
            if(data.hasExtra("TaskID")){
                EventBus.getDefault().post(new TaskUpdateEvent(data.getIntExtra("TaskID",0)));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TaskUpdateEvent taskUpdateEvent){
        List<TaskDescriptor> taskDescriptors = tasksRVAdapter.getTaskDescriptors();
        Log.d(TAG, "update on EventBus");

        updateRVData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void updateRVData(){
        TaskDBHandler dbHandler = TaskDBHandler.getInstance(parentActivity);
        List<TaskDescriptor> taskDescriptors = dbHandler.getTasksExcept(TaskDescriptor.TaskStatus.PENDING);
        tasksRVAdapter.setTaskDescriptors(taskDescriptors);
    }
}