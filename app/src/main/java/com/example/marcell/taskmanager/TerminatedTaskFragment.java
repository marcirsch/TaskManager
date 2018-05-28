package com.example.marcell.taskmanager;

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

import com.example.marcell.taskmanager.Data.TaskDBHandler;
import com.example.marcell.taskmanager.Data.TaskDescriptor;
import com.example.marcell.taskmanager.Utils.SwipeController;
import com.example.marcell.taskmanager.Utils.TasksRVAdapter;

import java.util.List;

public class TerminatedTaskFragment extends Fragment implements TasksRVAdapter.TasksRVAOnClickListener {
    private static final String TAG = TerminatedTaskFragment.class.getSimpleName();

    private TasksRVAdapter tasksRVAdapter;
    private RecyclerView tasksRecyclerView;

    private ProcessActivity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_terminated_tasks, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.tv_section_label);
        textView.setText("Tab2 OnCreateView");

        Log.d(TAG,"onCreateView Inflate OK");

        parentActivity = ((ProcessActivity) getActivity());

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
                TaskDescriptor rightSwipedTask = tasksRVAdapter.getTaskDescriptors().get(position);
                parentActivity.handleTaskRightSwipe(rightSwipedTask);
            }

            @Override
            public void onLeftSwipe(int position) {
                TaskDescriptor leftSwipedTask = tasksRVAdapter.getTaskDescriptors().get(position);
                parentActivity.handleTaskRightSwipe(leftSwipedTask);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);


        return rootView;
    }

    //TODO EventBus update

    @Override
    public void onResume() {
        super.onResume();
        updateRVData();
    }

    @Override
    public void onClick(TaskDescriptor task, int position, View v) {
        parentActivity.handleTaskClick(task);
    }

    @Override
    public void onPause() {
        super.onPause();
        TaskDBHandler dbHandler = TaskDBHandler.getInstance(parentActivity);
    }

    private void updateRVData(){
        TaskDBHandler dbHandler = TaskDBHandler.getInstance(parentActivity);
        List<TaskDescriptor> taskDescriptors = dbHandler.getTasksExcept(TaskDescriptor.TaskStatus.PENDING);
        tasksRVAdapter.setTaskDescriptors(taskDescriptors);
    }
}