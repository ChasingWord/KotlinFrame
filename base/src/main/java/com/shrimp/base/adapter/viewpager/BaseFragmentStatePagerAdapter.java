package com.shrimp.base.adapter.viewpager;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * Created by chasing on 2017/9/15.
 * 如果需要对ViewPager进行重新setAdapter，则使用该adapter
 * 部分情况会出现：因为FragmentPagerAdapter只要加载过，fragment中的视图就一直在内存中，在这个过程中无论怎么刷新，清除都是无用的
 * 需要注意：该adapter会使fragment进行onDestroy
 * 使用该adapter有些情况又会出现：Fragment already added
 * 所以视情况使用
 * 待测试：
 * aty里面的vp则需要用该adapter
 * frg里面的vp则使用{@link BaseFragmentPagerAdapter}
 */
public class BaseFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> datas;

    public BaseFragmentStatePagerAdapter(FragmentManager fm, List<Fragment> datas) {
        super(fm, BEHAVIOR_SET_USER_VISIBLE_HINT);
        this.datas = datas;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return datas.get(position);
    }

    @Override
    public int getCount() {
        return datas.size();
    }
}
