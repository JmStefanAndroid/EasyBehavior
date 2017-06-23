package me.stefan.easybehavior.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxy on 16/6/24.
 */
public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments = new ArrayList<>(0);
    private String[] names;

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments, String[] names) {
        super(fm);
        this.fragments = fragments;
        this.names = names;
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
        if (names != null){
            return names[position];
        }else {
            return "";
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
    }
}
