package com.hotbitmapgg.moequest.module.taogirl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.base.RxBaseActivity;
import com.hotbitmapgg.moequest.module.commonality.MeiziDetailsFragment;
import com.hotbitmapgg.moequest.widget.DepthTransFormes;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by hcc on 16/6/26 14:56
 * 100332338@qq.com
 * <p/>
 * 淘女郎大图浏览界面
 */
public class TaoFemalePagerActivity extends RxBaseActivity
{

    @Bind(R.id.tv_index)
    TextView mIndex;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    private static final String IMGLIST_KEY = "image_list_key";

    private static final String POS_KEY = "pos_key";

    private ArrayList<String> imgList;

    private int pos;

    @Override
    public int getLayoutId()
    {

        return R.layout.activity_tao_pager;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {

        Intent intent = getIntent();
        if (intent != null)
        {
            imgList = intent.getStringArrayListExtra(IMGLIST_KEY);
            pos = intent.getIntExtra(POS_KEY, -1);
        }

        mIndex.setText((pos + 1) + " / " + imgList.size());
        mViewPager.setAdapter(new TaoFemalePagerAdapter(getSupportFragmentManager()));
        mViewPager.setPageTransformer(true, new DepthTransFormes());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {

                mIndex.setText((position + 1) + " / " + imgList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
    }

    @Override
    public void initToolBar()
    {

    }

    @Override
    protected void onResume()
    {

        super.onResume();
        mViewPager.setCurrentItem(pos);
    }

    public static void luancher(Activity activity, ArrayList<String> imageList, int pos)
    {

        Intent mIntent = new Intent(activity, TaoFemalePagerActivity.class);
        mIntent.putStringArrayListExtra(IMGLIST_KEY, imageList);
        mIntent.putExtra(POS_KEY, pos);
        activity.startActivity(mIntent);
    }


    public class TaoFemalePagerAdapter extends FragmentStatePagerAdapter
    {


        public TaoFemalePagerAdapter(FragmentManager fm)
        {

            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {

            return MeiziDetailsFragment.newInstance(imgList.get(position));
        }

        @Override
        public int getCount()
        {

            return imgList.size();
        }
    }
}
