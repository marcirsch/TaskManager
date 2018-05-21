package com.example.marcell.taskmanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Tab2TerminatedTasks extends Fragment {
    private static final String TAG = Tab2TerminatedTasks.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_terminated_tasks, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText("Tab2 OnCreateView");

        Log.d(TAG,"onCreateView Inflate OK");
        return rootView;
    }
}
