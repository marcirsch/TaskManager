package com.example.marcell.taskmanager.UI.TaskManagerTabs.TabUtilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {
    private boolean swipeEnabled;

    public CustomViewPager(@NonNull Context context) {
        super(context);
        this.swipeEnabled = false;
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.swipeEnabled = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(this.swipeEnabled) {
            return super.onTouchEvent(ev);
        }
        else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(this.swipeEnabled) {
            return super.onInterceptTouchEvent(ev);
        }else {
            return false;
        }
    }

    public void setSwipe(boolean enable){
        this.swipeEnabled = enable;
    }
}
