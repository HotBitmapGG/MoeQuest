package com.hotbitmapgg.moequest.module.meizitu;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.base.RxBaseActivity;
import com.hotbitmapgg.moequest.entity.meizitu.MeiziTu;
import com.hotbitmapgg.moequest.module.commonality.MeiziDetailsFragment;
import com.hotbitmapgg.moequest.rx.RxBus;
import com.hotbitmapgg.moequest.utils.ConstantUtil;
import com.hotbitmapgg.moequest.utils.GlideDownloadImageUtil;
import com.hotbitmapgg.moequest.utils.ImmersiveUtil;
import com.hotbitmapgg.moequest.utils.ShareUtil;
import com.hotbitmapgg.moequest.widget.DepthTransFormes;
import com.jakewharton.rxbinding.view.RxMenuItem;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by hcc on 16/8/13 13:02
 * 100332338@qq.com
 * <p/>
 * 妹子图pager界面
 */
public class MeiziTuPageActivity extends RxBaseActivity
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

    private String type;

    private boolean isHide = false;

    private String url;

    private RealmResults<MeiziTu> meizis;

    private MeiziPagerAdapter mPagerAdapter;

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
            type = intent.getStringExtra(EXTRA_TYPE);
        }
        realm = Realm.getDefaultInstance();
        meizis = realm.where(MeiziTu.class)
                .equalTo("type", type)
                .findAll();

        mPagerAdapter = new MeiziPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
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

                mToolbar.setTitle(meizis.get(position).getTitle());
                currenIndex = position;
                url = meizis.get(currenIndex).getImageurl();
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

                        hideOrShowToolbar();
                    }
                }, new Action1<Throwable>()
                {

                    @Override
                    public void call(Throwable throwable)
                    {


                    }
                });


        setEnterSharedElementCallback(new SharedElementCallback()
        {

            @Override
            public void onMapSharedElements(List<String> names, Map<String,View> sharedElements)
            {

                MeiziTu meiziTu = meizis.get(mViewPager.getCurrentItem());
                MeiziDetailsFragment fragment = (MeiziDetailsFragment)
                        mPagerAdapter.instantiateItem(mViewPager, currenIndex);
                sharedElements.clear();
                sharedElements.put(meiziTu.getImageurl(), fragment.getSharedElement());
            }
        });
    }

    @Override
    public void supportFinishAfterTransition()
    {

        Intent data = new Intent();
        data.putExtra("index", currenIndex);
        RxBus.getInstance().post(data);
        super.supportFinishAfterTransition();
    }

    @Override
    public void initToolBar()
    {

        mToolbar.setTitle(meizis.get(currenIndex).getTitle());
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
        url = meizis.get(currenIndex).getImageurl();
    }

    public static Intent luanch(Activity activity, int index, String type)
    {

        Intent mIntent = new Intent(activity, MeiziTuPageActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.putExtra(EXTRA_INDEX, index);
        mIntent.putExtra(EXTRA_TYPE, type);
        return mIntent;
    }


    /**
     * 保存图片到本地
     */
    private void saveImage()
    {

        RxMenuItem.clicks(mToolbar.getMenu().findItem(R.id.action_fuli_save))
                .compose(this.<Void>bindToLifecycle())
                .compose(RxPermissions.getInstance(MeiziTuPageActivity.this)
                        .ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
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

                        return GlideDownloadImageUtil.saveImageToLocal(MeiziTuPageActivity.this, url);
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

                        Toast.makeText(MeiziTuPageActivity.this, s, Toast.LENGTH_SHORT).show();
                    }
                }, new Action1<Throwable>()
                {

                    @Override
                    public void call(Throwable throwable)
                    {

                        Toast.makeText(MeiziTuPageActivity.this, "保存失败,请重试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 分享图片
     */
    public void shareImage()
    {

        RxMenuItem.clicks(mToolbar.getMenu().findItem(R.id.action_fuli_share))
                .compose(this.<Void>bindToLifecycle())
                .compose(RxPermissions.getInstance(MeiziTuPageActivity.this)
                        .ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
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

                        return GlideDownloadImageUtil.saveImageToLocal(MeiziTuPageActivity.this, url);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .retry()
                .subscribe(new Action1<Uri>()
                {

                    @Override
                    public void call(Uri uri)
                    {

                        ShareUtil.sharePic(uri, meizis.get(currenIndex).getTitle(), MeiziTuPageActivity.this);
                    }
                }, new Action1<Throwable>()
                {

                    @Override
                    public void call(Throwable throwable)
                    {

                        Toast.makeText(MeiziTuPageActivity.this, "分享失败,请重试", Toast.LENGTH_SHORT).show();
                    }
                });
    }


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

    @Override
    public void onBackPressed()
    {

        supportFinishAfterTransition();
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

            return MeiziDetailsFragment.
                    newInstance(meizis.get(position).getImageurl());
        }


        @Override
        public int getCount()
        {

            return meizis.size();
        }
    }
}
