package com.hotbitmapgg.moequest.module.huaban;


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
 * Created by hcc on 16/8/13 12:50
 * 100332338@qq.com
 * <p/>
 * 花瓣妹子接口对应type:
 * 大胸妹=34
 * 小清新=35
 * 文艺范=36
 * 性感妹=37
 * 大长腿=38
 * 黑丝袜=39
 * 小翘臀=40
 */
public class HuaBanMeiziFragment extends RxBaseFragment
{

    @Bind(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    private List<String> titles = Arrays.asList("大胸妹", "小清新", "文艺范", "性感妹", "大长腿", "黑丝袜", "小翘臀");

    private List<Integer> cids = Arrays.asList(34, 35, 36, 37, 38, 39, 40);


    public static HuaBanMeiziFragment newInstance()
    {

        return new HuaBanMeiziFragment();
    }

    @Override
    public int getLayoutId()
    {

        return R.layout.fragment_huaban_meizi;
    }

    @Override
    public void initViews()
    {

        mViewPager.setAdapter(new HuaBanMeiziPageAdapter(getChildFragmentManager()));
        mSlidingTabLayout.setViewPager(mViewPager);
    }


    private class HuaBanMeiziPageAdapter extends FragmentStatePagerAdapter
    {


        public HuaBanMeiziPageAdapter(FragmentManager fm)
        {

            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {

            return HuaBanMeiziSimpleFragment
                    .newInstance(cids.get(position), position);
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
