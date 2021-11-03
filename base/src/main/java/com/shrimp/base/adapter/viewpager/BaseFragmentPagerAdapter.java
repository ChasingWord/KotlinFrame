package com.shrimp.base.adapter.viewpager;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by chasing on 2017/9/15.
 */
public class BaseFragmentPagerAdapter extends FragmentPageAdapter {
    private List<Fragment> datas;
    private final FragmentManager fm;
    private int mChildCount = 0;
    private int mViewPagerId;

    public BaseFragmentPagerAdapter(FragmentManager fm, List<Fragment> datas) {
        super(fm);
        this.fm = fm;
        this.datas = datas;
    }

    public void setViewPagerId(int viewPagerId) {
        mViewPagerId = viewPagerId;
    }

    public void setDatas(List<Fragment> datas) {
        mChildCount = getCount();
        if (this.datas != null && this.datas.size() > 0 && mViewPagerId > 0) { //只有移除原来的Fragment，新添加的Fragment才能展示出来
            for (int i = 0; i < this.datas.size(); i++) {
                String frgTag = makeFragmentName(mViewPagerId, i);
                Fragment fragment = fm.findFragmentByTag(frgTag);
                if (fragment == null) continue;
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment);
                ft.commit();
                fm.executePendingTransactions();
            }
        }
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        // 最简单解决 notifyDataSetChanged() 页面不刷新问题的方法
        if (mChildCount > 0) {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {
        return datas.get(position);
    }

    @Override
    public int getCount() {
        return datas.size();
    }
}
