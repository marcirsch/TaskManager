package com.example.marcell.taskmanager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment returnItem = null;
        switch (position){
            case 0:
                returnItem = new Tab1PendingTasks();
                break;
            case 1:
                returnItem = new Tab2TerminatedTasks();
                break;
            default:
                break;
        }

        return returnItem;

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence returnCharSeq = null;
        switch (position){
            case 0:
                returnCharSeq = "PENDING";
                break;
            case 1:
                returnCharSeq = "TERMINATED";
                break;
            default:
                break;
        }
        return returnCharSeq;
    }
}