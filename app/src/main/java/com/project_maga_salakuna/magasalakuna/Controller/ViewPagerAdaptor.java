package com.project_maga_salakuna.magasalakuna.Controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Lakshan on 2016-06-13.
 */
public class ViewPagerAdaptor extends FragmentPagerAdapter{
    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> tabtitles = new ArrayList<>();

    public  void addFragments(Fragment fragment, String tabtitle){
        this.fragments.add(fragment);
        this.tabtitles.add(tabtitle);
    }
    public ViewPagerAdaptor(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles.get(position);
    }
}
