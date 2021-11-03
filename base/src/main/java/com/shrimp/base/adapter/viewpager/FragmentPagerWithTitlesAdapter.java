package com.shrimp.base.adapter.viewpager;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Created by chasing on 2018/1/24.
 */
public class FragmentPagerWithTitlesAdapter extends BaseFragmentPagerAdapter {
    private String[] mTitles;

    public FragmentPagerWithTitlesAdapter(FragmentManager fm, List<Fragment> datas, String[] titles) {
        super(fm, datas);
        mTitles = titles;
    }

    public void resetPagerTitles(String[] titles){
        mTitles = titles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
