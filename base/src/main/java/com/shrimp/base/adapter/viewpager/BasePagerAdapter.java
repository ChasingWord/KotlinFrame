package com.shrimp.base.adapter.viewpager;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

/**
 * Created by chasing on 2018/1/19.
 * viewpager--views
 */
public class BasePagerAdapter extends PagerAdapter {
    private List<? extends View> data;

    public BasePagerAdapter(List<? extends View> data) {
        this.data = data;
    }

    public void setData(List<? extends View> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(data.get(position), 0);//添加页卡
        return data.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (position < data.size())
            container.removeView(data.get(position));//删除页卡
    }
}
