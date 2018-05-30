package com.example.marcell.taskmanager.UI.TaskManagerTabs.TabUtilities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.example.marcell.taskmanager.Data.TaskDescriptor;
import com.example.marcell.taskmanager.UI.TaskManagerTabs.PendingTaskFragment;
import com.example.marcell.taskmanager.UI.TaskManagerTabs.ProgressTaskFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class TabAdapter extends FragmentPagerAdapter {
    SparseArray<Fragment> registeredFragments;

    public interface OnClickListener{
        void onClick(TaskDescriptor task);
    }


    public TabAdapter(FragmentManager fm) {
        super(fm);
        registeredFragments = new SparseArray<Fragment>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position,fragment);

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position){
        return registeredFragments.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment returnItem = null;
        switch (position){
            case 0:
                returnItem = new PendingTaskFragment();
                break;
            case 1:
                returnItem = new ProgressTaskFragment();
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