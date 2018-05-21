package com.example.marcell.taskmanager;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Tab1PendingTasks extends Fragment {
    private static final String TAG = Tab1PendingTasks.class.getSimpleName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab1_pending_tasks, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText("Tab1 onCreateView");

        Log.d(TAG,"onCreateVew Inflate OK");
        return rootView;
    }


}
