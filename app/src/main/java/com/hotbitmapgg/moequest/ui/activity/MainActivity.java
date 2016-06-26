package com.hotbitmapgg.moequest.ui.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.base.RxBaseActivity;
import com.hotbitmapgg.moequest.ui.fragment.DoubanMeiziFragment;
import com.hotbitmapgg.moequest.ui.fragment.GankMeiziFragment;
import com.hotbitmapgg.moequest.ui.fragment.HuaBanMeiziFragment;
import com.hotbitmapgg.moequest.ui.fragment.TaoFemaleFragment;
import com.hotbitmapgg.moequest.utils.AlarmManagerUtils;
import com.hotbitmapgg.moequest.utils.ShareUtil;
import com.hotbitmapgg.moequest.widget.CircleImageView;

import java.util.Random;

import butterknife.Bind;

public class MainActivity extends RxBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    private Fragment[] fragments;

    private int currentTabIndex;

    private int index;

    private Random random = new Random();

    private int[] avatars = new int[]{
            R.drawable.ic_avatar1,
            R.drawable.ic_avatar2,
            R.drawable.ic_avatar3,
            R.drawable.ic_avatar4,
            R.drawable.ic_avatar5,
            R.drawable.ic_avatar6,
            R.drawable.ic_avatar7,
            R.drawable.ic_avatar8,
            R.drawable.ic_avatar9,
            R.drawable.ic_avatar10,
            R.drawable.ic_avatar11,
            };


    @Override
    public int getLayoutId()
    {

        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {

        setNavigationView();
        initFragment();
        AlarmManagerUtils.register(this);
    }

    private void initFragment()
    {

        GankMeiziFragment gankMeiziFragment = GankMeiziFragment.newInstance();
        DoubanMeiziFragment doubanMeiziFragment = DoubanMeiziFragment.newInstance();
        HuaBanMeiziFragment huaBanMeiziFragment = HuaBanMeiziFragment.newInstance();
        TaoFemaleFragment taoFemaleFragment = TaoFemaleFragment.newInstance();
        fragments = new Fragment[]{gankMeiziFragment, taoFemaleFragment, doubanMeiziFragment, huaBanMeiziFragment};
        getFragmentManager().beginTransaction().replace(R.id.content, gankMeiziFragment).commit();
    }

    private void setNavigationView()
    {

        mNavigationView.setNavigationItemSelectedListener(this);
        View headerView = mNavigationView.inflateHeaderView(R.layout.nav_header_main);
        CircleImageView mCircleImageView = (CircleImageView) headerView.findViewById(R.id.nav_head_avatar);
        int randomNum = random.nextInt(avatars.length);
        mCircleImageView.setImageResource(avatars[randomNum]);
    }

    @Override
    public void initToolBar()
    {

        mToolbar.setTitle("首页");
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed()
    {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {


        switch (item.getItemId())
        {
            case R.id.nav_home:
                changIndex(0, getResources().getString(R.string.gank_meizi), item);
                return true;

            case R.id.nav_tao:
                changIndex(1, getResources().getString(R.string.tao_female), item);
                return true;

            case R.id.nav_douban:
                changIndex(2, getResources().getString(R.string.douban_meizi), item);
                return true;

            case R.id.nav_huaban:
                changIndex(3, getResources().getString(R.string.huaban_meizi), item);
                return true;

            case R.id.nav_share:

                ShareUtil.shareLink("", "萌妹,每日更新妹子福利", MainActivity.this);
                return true;

            default:
                break;
        }
        return true;
    }


    public void changIndex(int changNum, String title, MenuItem item)
    {

        index = changNum;
        switchFragment(fragments[changNum]);
        item.setChecked(true);
        mToolbar.setTitle(title);
        mDrawerLayout.closeDrawers();
    }


    public void switchFragment(Fragment fragment)
    {

        FragmentTransaction trx = getFragmentManager().beginTransaction();
        trx.hide(fragments[currentTabIndex]);
        if (!fragments[index].isAdded())
        {
            trx.add(R.id.content, fragments[index]);
        }
        trx.show(fragments[index]).commit();
        currentTabIndex = index;
    }
}
