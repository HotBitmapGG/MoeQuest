package com.hotbitmapgg.moequest.module.meizitu;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.base.RxBaseFragment;
import com.hotbitmapgg.moequest.utils.ConstantUtil;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;

/**
 * Created by hcc on 16/7/19 20:39
 * 100332338@qq.com
 * <p/>
 * 妹子图
 */
public class MeiziTuFragment extends RxBaseFragment
{

    @Bind(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    private List<String> titles = Arrays.asList("自拍", "热门", "推荐", "清纯", "台湾", "日本", "性感");

    private List<String> types = Arrays.asList(
            ConstantUtil.ZIPAI_MEIZI,
            ConstantUtil.HOT_MEIZI,
            ConstantUtil.TUIJIAN_MEIZI,
            ConstantUtil.QINGCHUN_MEIZI,
            ConstantUtil.TAIWAN_MEIZI,
            ConstantUtil.JAPAN_MEIZI,
            ConstantUtil.XINGGAN_MEIZI);


    public static MeiziTuFragment newInstance()
    {

        return new MeiziTuFragment();
    }

    @Override
    public int getLayoutId()
    {

        return R.layout.fragment_meizitu;
    }

    @Override
    public void initViews()
    {

        initFragments();
    }

    private void initFragments()
    {

        mViewPager.setAdapter(new MeiziTuPageAdapter(getChildFragmentManager()));
        mViewPager.setOffscreenPageLimit(1);
        mSlidingTabLayout.setViewPager(mViewPager);
    }


    private class MeiziTuPageAdapter extends FragmentStatePagerAdapter
    {


        public MeiziTuPageAdapter(FragmentManager fm)
        {

            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {

            if (position == 0)
                return ZiPaiMeiziFragment.newInstance(types.get(0));
            else
                return MeiziTuSimpleFragment.newInstance(types.get(position));
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
