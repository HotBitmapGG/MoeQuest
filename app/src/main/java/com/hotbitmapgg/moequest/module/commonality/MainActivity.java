package com.hotbitmapgg.moequest.module.commonality;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.base.RxBaseActivity;
import com.hotbitmapgg.moequest.module.douban.DoubanMeiziFragment;
import com.hotbitmapgg.moequest.module.gank.GankMeiziFragment;
import com.hotbitmapgg.moequest.module.huaban.HuaBanMeiziFragment;
import com.hotbitmapgg.moequest.module.jiandan.JianDanMeiziFragment;
import com.hotbitmapgg.moequest.module.meizitu.MeiziTuFragment;
import com.hotbitmapgg.moequest.module.taogirl.TaoFemaleFragment;
import com.hotbitmapgg.moequest.utils.AlarmManagerUtils;
import com.hotbitmapgg.moequest.utils.ShareUtil;
import com.hotbitmapgg.moequest.utils.SnackbarUtil;
import com.hotbitmapgg.moequest.widget.CircleImageView;

import java.util.Random;

import butterknife.Bind;

/**
 * Created by hcc on 16/7/30 12:57
 * 100332338@qq.com
 * <p/>
 * 萌妹纸主界面
 */
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

    private long exitTime;


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

        // 初始化Fragment
        GankMeiziFragment gankMeiziFragment = GankMeiziFragment.newInstance();
        MeiziTuFragment meiziTuFragment = MeiziTuFragment.newInstance();
        DoubanMeiziFragment doubanMeiziFragment = DoubanMeiziFragment.newInstance();
        HuaBanMeiziFragment huaBanMeiziFragment = HuaBanMeiziFragment.newInstance();
        TaoFemaleFragment taoFemaleFragment = TaoFemaleFragment.newInstance();
        JianDanMeiziFragment jianDanMeiziFragment = JianDanMeiziFragment.newInstance();

        fragments = new Fragment[]{
                gankMeiziFragment,
                meiziTuFragment,
                taoFemaleFragment,
                doubanMeiziFragment,
                huaBanMeiziFragment,
                jianDanMeiziFragment
        };

        //显示第一个 gank妹子
        getSupportFragmentManager().beginTransaction().replace(R.id.content, gankMeiziFragment).commit();
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

        mToolbar.setTitle("MoeQuest");
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
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

            case R.id.nav_meizitu:
                changIndex(1, getResources().getString(R.string.meizitu), item);
                return true;

            case R.id.nav_tao:
                changIndex(2, getResources().getString(R.string.tao_female), item);
                return true;

            case R.id.nav_douban:
                changIndex(3, getResources().getString(R.string.douban_meizi), item);
                return true;

            case R.id.nav_huaban:
                changIndex(4, getResources().getString(R.string.huaban_meizi), item);
                return true;

            case R.id.nav_jiandan:
                changIndex(5, getResources().getString(R.string.jiandan_meizi), item);
                return true;


            case R.id.nav_about:

                startActivity(new Intent(MainActivity.this, AppAboutActivity.class));
                return true;

            case R.id.nav_share:

                ShareUtil.shareLink(getString(R.string.project_link), "萌妹纸,每日更新妹子福利", MainActivity.this);
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

        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        trx.hide(fragments[currentTabIndex]);
        if (!fragments[index].isAdded())
        {
            trx.add(R.id.content, fragments[index]);
        }
        trx.show(fragments[index]).commit();
        currentTabIndex = index;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            logoutApp();
        }

        return true;
    }

    private void logoutApp()
    {

        if (System.currentTimeMillis() - exitTime > 2000)
        {
            SnackbarUtil.showMessage(mDrawerLayout, getString(R.string.back_message));

            exitTime = System.currentTimeMillis();
        } else
        {
            finish();
        }
    }
}
