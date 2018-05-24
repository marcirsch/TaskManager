package com.example.marcell.taskmanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.marcell.taskmanager.Data.TaskDescriptor;
import com.example.marcell.taskmanager.Data.TaskDescriptorList;
import com.example.marcell.taskmanager.Utils.TabAdapter;
import com.example.marcell.taskmanager.Utils.TasksRVAdapter;

public class PendingTaskFragment extends Fragment implements TasksRVAdapter.TasksRVAOnClickListener {
    private static final String TAG = PendingTaskFragment.class.getSimpleName();

    private RecyclerView tasksRecyclerView;
    private TasksRVAdapter tasksRVAdapter;

    private ProcessActivity parentActivity;

    private TaskDescriptorList pendingTasks;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab1_pending_tasks, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.tv_section_label);
        textView.setText("Tab1 onCreateView");
        Log.d(TAG, "onCreateVew TextView Inflate OK");

        parentActivity = ((ProcessActivity) getActivity());
        pendingTasks = new TaskDescriptorList();


        //Initialize RecyclerView
        tasksRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_task_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false);


        tasksRecyclerView.setLayoutManager(layoutManager);
        tasksRecyclerView.setHasFixedSize(true);

        tasksRVAdapter = new TasksRVAdapter(this);
        tasksRecyclerView.setAdapter(tasksRVAdapter);


        parentActivity.addTabsOnDataUpdateListener(new ProcessActivity.TabsOnDataUpdateListener() {
            @Override
            public void onDataUpdate() {

                pendingTasks = filterData(parentActivity.getTaskDescriptorList());
                setRVData(pendingTasks);

                Log.d(TAG, "OnDataListener");
                if (pendingTasks.getLength() != 0) {
                    Log.d(TAG, "Name of first element:" + pendingTasks.getTaskDescriptors()[0].getName());
                }


            }
        });

        return rootView;
    }

    private TaskDescriptorList filterData(TaskDescriptorList taskDescriptorList) {
        TaskDescriptorList filteredTasks = new TaskDescriptorList();
        //iterate through tasks
        for (TaskDescriptor task : taskDescriptorList.getTaskDescriptors()) {

            if (task.getTaskStatus() == TaskDescriptor.TaskStatus.PENDING) {
                filteredTasks.append(task);
            }
        }
        return filteredTasks;
    }

    private void setRVData(TaskDescriptorList taskDescriptors) {
        tasksRVAdapter.setTaskDescriptors(taskDescriptors);
    }

    private TaskDescriptorList getTaskDescriptor() {
        return tasksRVAdapter.getTaskDescriptors();
    }

    @Override
    public void onClick(TaskDescriptor task, int position, View v) {
        parentActivity.handleTaskClick(task);
    }

}
