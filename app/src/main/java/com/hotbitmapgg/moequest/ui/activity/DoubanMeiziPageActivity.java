package com.hotbitmapgg.moequest.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.base.RxBaseActivity;
import com.hotbitmapgg.moequest.rx.RxBus;
import com.hotbitmapgg.moequest.model.douban.DoubanMeizi;
import com.hotbitmapgg.moequest.ui.fragment.DoubanMeiziDetailsFragment;
import com.hotbitmapgg.moequest.utils.ConstantUtil;
import com.hotbitmapgg.moequest.utils.GlideDownloadImageUtil;
import com.hotbitmapgg.moequest.utils.ImmersiveUtil;
import com.hotbitmapgg.moequest.utils.LogUtil;
import com.hotbitmapgg.moequest.utils.ShareUtil;
import com.hotbitmapgg.moequest.widget.DepthTransFormes;
import com.jakewharton.rxbinding.view.RxMenuItem;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;

import butterknife.Bind;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DoubanMeiziPageActivity extends RxBaseActivity
{

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    private static final String EXTRA_INDEX = "extra_index";

    private static final String EXTRA_TYPE = "extra_type";

    private int currenIndex;

    private Realm realm;

    private int type;

    private boolean isHide = false;

    private String url;

    private RealmResults<DoubanMeizi> doubanMeizis;

    @Override
    public int getLayoutId()
    {

        return R.layout.activity_meizi_pager;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {

        Intent intent = getIntent();
        if (intent != null)
        {
            currenIndex = intent.getIntExtra(EXTRA_INDEX, -1);
            type = intent.getIntExtra(EXTRA_TYPE, -1);
        }
        realm = Realm.getDefaultInstance();
        doubanMeizis = realm.where(DoubanMeizi.class)
                .equalTo("type", type)
                .findAll();

        mViewPager.setAdapter(new MeiziPagerAdapter(getFragmentManager()));
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

                mToolbar.setTitle(doubanMeizis.get(position).getTitle());
                currenIndex = position;
                url = doubanMeizis.get(currenIndex).getUrl();

                //切换ViewPager时隐藏ToolBar
                ImmersiveUtil.enter(DoubanMeiziPageActivity.this);
                mAppBarLayout.animate()
                        .translationY(-mAppBarLayout.getHeight())
                        .setInterpolator(new DecelerateInterpolator(2))
                        .start();
                isHide = true;
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        RxBus.getInstance().toObserverable(String.class)
                .subscribe(new Action1<String>()
                {

                    @Override
                    public void call(String s)
                    {

                        LogUtil.all("隐藏toolbar");
                        hideOrShowToolbar();
                    }
                }, new Action1<Throwable>()
                {

                    @Override
                    public void call(Throwable throwable)
                    {

                        LogUtil.all("接收失败");
                    }
                });
    }

    @Override
    public void initToolBar()
    {

        mToolbar.setTitle(doubanMeizis.get(currenIndex).getTitle());
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                onBackPressed();
            }
        });
        mToolbar.inflateMenu(R.menu.menu_meizi);


        //设置toolbar的颜色
        mAppBarLayout.setAlpha(0.5f);
        mToolbar.setBackgroundResource(R.color.black_90);
        mAppBarLayout.setBackgroundResource(R.color.black_90);

        //menu的点击事件
        saveImage();
        shareImage();
    }

    @Override
    protected void onResume()
    {

        super.onResume();
        mViewPager.setCurrentItem(currenIndex);
        url = doubanMeizis.get(currenIndex).getUrl();
    }

    public static void luanch(Activity activity, int index, int type)
    {

        Intent mIntent = new Intent(activity, DoubanMeiziPageActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.putExtra(EXTRA_INDEX, index);
        mIntent.putExtra(EXTRA_TYPE, type);
        activity.startActivity(mIntent);
    }


    /**
     * 保存图片到本地
     */
    private void saveImage()
    {

        RxMenuItem.clicks(mToolbar.getMenu().findItem(R.id.action_fuli_save))
                .compose(bindToLifecycle())
                .compose(RxPermissions.getInstance(DoubanMeiziPageActivity.this).ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .observeOn(Schedulers.io())
                .filter(new Func1<Boolean,Boolean>()
                {

                    @Override
                    public Boolean call(Boolean aBoolean)
                    {

                        return aBoolean;
                    }
                })
                .flatMap(new Func1<Boolean,Observable<Uri>>()
                {

                    @Override
                    public Observable<Uri> call(Boolean aBoolean)
                    {

                        return GlideDownloadImageUtil.saveImageToLocal(DoubanMeiziPageActivity.this, url);
                    }
                })
                .map(new Func1<Uri,String>()
                {

                    @Override
                    public String call(Uri uri)
                    {

                        String msg = String.format("图片已保存至 %s 文件夹",
                                new File(Environment.getExternalStorageDirectory(), ConstantUtil.FILE_DIR)
                                        .getAbsolutePath());
                        return msg;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .subscribe(new Action1<String>()
                {

                    @Override
                    public void call(String s)
                    {

                        Toast.makeText(DoubanMeiziPageActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                }, new Action1<Throwable>()
                {

                    @Override
                    public void call(Throwable throwable)
                    {

                        Toast.makeText(DoubanMeiziPageActivity.this, "保存失败,请重试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 分享图片
     */
    public void shareImage()
    {

        RxMenuItem.clicks(mToolbar.getMenu().findItem(R.id.action_fuli_share))
                .compose(bindToLifecycle())
                .compose(RxPermissions.getInstance(DoubanMeiziPageActivity.this).ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .observeOn(Schedulers.io())
                .filter(new Func1<Boolean,Boolean>()
                {

                    @Override
                    public Boolean call(Boolean aBoolean)
                    {

                        return aBoolean;
                    }
                })
                .flatMap(new Func1<Boolean,Observable<Uri>>()
                {

                    @Override
                    public Observable<Uri> call(Boolean aBoolean)
                    {

                        return GlideDownloadImageUtil.saveImageToLocal(DoubanMeiziPageActivity.this, url);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .subscribe(new Action1<Uri>()
                {

                    @Override
                    public void call(Uri uri)
                    {

                        ShareUtil.sharePic(uri, doubanMeizis.get(currenIndex).getTitle(), DoubanMeiziPageActivity.this);
                    }
                }, new Action1<Throwable>()
                {

                    @Override
                    public void call(Throwable throwable)
                    {

                        Toast.makeText(DoubanMeiziPageActivity.this, "分享失败,请重试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    /**
//     * 分享图片
//     *
//     * @param uri
//     */
//    private void share(Uri uri)
//    {
//
//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        shareIntent.setType("image/jpeg");
//        startActivity(Intent.createChooser(shareIntent, doubanMeizis.get(currenIndex).getTitle()));
//    }


    protected void hideOrShowToolbar()
    {

        if (isHide)
        {
            //显示
            ImmersiveUtil.exit(this);
            mAppBarLayout.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(2))
                    .start();
            isHide = false;
        } else
        {
            //隐藏
            ImmersiveUtil.enter(this);
            mAppBarLayout.animate()
                    .translationY(-mAppBarLayout.getHeight())
                    .setInterpolator(new DecelerateInterpolator(2))
                    .start();
            isHide = true;
        }
    }


    private class MeiziPagerAdapter extends FragmentStatePagerAdapter
    {

        public MeiziPagerAdapter(FragmentManager fm)
        {

            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {

            return DoubanMeiziDetailsFragment.
                    newInstance(doubanMeizis.get(position).getUrl());
        }


        @Override
        public int getCount()
        {

            return doubanMeizis.size();
        }
    }
}
