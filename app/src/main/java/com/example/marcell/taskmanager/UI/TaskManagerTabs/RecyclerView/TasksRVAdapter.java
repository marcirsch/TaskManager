package com.example.marcell.taskmanager.UI.TaskManagerTabs.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.marcell.taskmanager.Data.TaskDescriptor;
import com.example.marcell.taskmanager.Data.UserPreferences;
import com.example.marcell.taskmanager.UI.TaskManagerTabs.PendingTaskFragment;
import com.example.marcell.taskmanager.R;

import java.util.List;

public class TasksRVAdapter extends RecyclerView.Adapter<TasksRVAdapter.TasksRVViewHolder> {

    private static final String TAG = PendingTaskFragment.class.getSimpleName();
    private final TasksRVAOnClickListener taskOnClickListener;
    private List<TaskDescriptor> taskDescriptors;


    public TasksRVAdapter(TasksRVAOnClickListener taskOnClickListener) {
        this.taskOnClickListener = taskOnClickListener;
    }

    public List<TaskDescriptor> getTaskDescriptors() {
        return taskDescriptors;
    }

    public void setTaskDescriptors(List<TaskDescriptor> taskDescriptors) {
        if (taskDescriptors != null) {
                this.taskDescriptors = taskDescriptors;
        }
        notifyDataSetChanged();
    }

    @Override
    public TasksRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.task_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutId, parent, false);
        return new TasksRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TasksRVViewHolder holder, int position) {
        if (position >= 0 && position < taskDescriptors.size()) {
            TaskDescriptor task = taskDescriptors.get(position);

            String taskName = task.getName();
            String taskStatus = TaskDescriptor.getStatusString(task.getTaskStatus());
            String taskPercent = String.valueOf(task.getCompletionPercentage()) + "%";
            String taskTime = String.valueOf(task.getCompletionTime()/1000) + "s";

            holder.taskNameTextView.setText(taskName);
            holder.taskStatus.setText(taskStatus);

            if(task.getTaskStatus() == TaskDescriptor.TaskStatus.IN_PROGRESS || task.getTaskStatus() == TaskDescriptor.TaskStatus.DONE){
                holder.taskPercentage.setText(taskPercent);
                holder.taskPercentage.setVisibility(View.VISIBLE);
                holder.taskCompletionTime.setText(taskTime);
                holder.taskCompletionTime.setVisibility(View.VISIBLE);
            }else{
                holder.taskPercentage.setVisibility(View.INVISIBLE);
                holder.taskCompletionTime.setVisibility(View.INVISIBLE);
            }


        } else {
            Log.d(TAG, "onBindViewHolder: invalid position: " + String.valueOf(position));
        }
    }

    @Override
    public int getItemCount() {
        if (taskDescriptors != null) {
            return taskDescriptors.size();
        } else {
            Log.d(TAG, "getItemCount: taskDescriptors is null");
            return 0;
        }
    }


    public interface TasksRVAOnClickListener {
        void onClick(TaskDescriptor task,int position, View v);
    }

    public class TasksRVViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView taskNameTextView;
        public final TextView taskStatus;
        public final TextView taskPercentage;
        public final TextView taskCompletionTime;

        public TasksRVViewHolder(View itemView) {
            super(itemView);

            taskNameTextView = (TextView) itemView.findViewById(R.id.tv_rv_task_name);
            taskStatus = (TextView) itemView.findViewById(R.id.tv_rv_status);
            taskPercentage = (TextView) itemView.findViewById(R.id.tv_rv_percentage);
            taskCompletionTime = (TextView) itemView.findViewById(R.id.tv_rv_completion_time) ;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            if (adapterPosition != RecyclerView.NO_POSITION) {
                taskOnClickListener.onClick(taskDescriptors.get(adapterPosition),adapterPosition, v);
            } else {
                Log.d(TAG, "OnClick method: NO_POSITION in RecyclerView");
            }
        }
    }
}
