package com.hotbitmapgg.moequest.module.douban;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.base.RxBaseFragment;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;

/**
 * Created by hcc on 16/6/25 19:48
 * 100332338@qq.com
 * <p/>
 * 豆瓣妹子
 */
public class DoubanMeiziFragment extends RxBaseFragment
{

    @Bind(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    private List<String> titles = Arrays.asList("大胸妹", "小翘臀", "黑丝袜", "美图控", "高颜值");

    private List<Integer> cids = Arrays.asList(2, 6, 7, 3, 4);


    public static DoubanMeiziFragment newInstance()
    {

        return new DoubanMeiziFragment();
    }

    @Override
    public int getLayoutId()
    {

        return R.layout.fragment_douban_meizi;
    }

    @Override
    public void initViews()
    {

        initFragments();
    }

    private void initFragments()
    {

        mViewPager.setAdapter(new DoubanMeiziPageAdapter(getChildFragmentManager()));
        mViewPager.setOffscreenPageLimit(1);
        mSlidingTabLayout.setViewPager(mViewPager);
    }


    private class DoubanMeiziPageAdapter extends FragmentStatePagerAdapter
    {


        public DoubanMeiziPageAdapter(FragmentManager fm)
        {

            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {

            return DoubanSimpleMeiziFragment.newInstance(cids.get(position), position);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {

            return titles.get(position);
        }

        @Override
        public int getCount()
        {

            return titles.size();
        }
    }
}
