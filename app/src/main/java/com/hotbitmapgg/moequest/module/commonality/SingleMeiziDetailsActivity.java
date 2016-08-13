package com.hotbitmapgg.moequest.module.commonality;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.base.RxBaseActivity;
import com.hotbitmapgg.moequest.utils.ConstantUtil;
import com.hotbitmapgg.moequest.utils.GlideDownloadImageUtil;
import com.hotbitmapgg.moequest.utils.ImmersiveUtil;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;

import butterknife.Bind;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by hcc on 16/8/13 12:14
 * 100332338@qq.com
 * <p/>
 * 单个妹子大图浏览界面
 */
public class SingleMeiziDetailsActivity extends RxBaseActivity
{


    @Bind(R.id.meizi)
    ImageView mImageView;

    @Bind(R.id.tv_image_error)
    TextView mImageError;

    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    private static final String EXTRA_URL = "extra_url";

    private static final String EXTRA_TITLE = "extra_title";

    private String url;

    private String title;

    private boolean isHide = false;

    private PhotoViewAttacher mPhotoViewAttacher;

    @Override
    public int getLayoutId()
    {

        return R.layout.activity_meizi_pic;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {

        Intent intent = getIntent();
        if (intent != null)
        {
            url = intent.getStringExtra(EXTRA_URL);
            title = intent.getStringExtra(EXTRA_TITLE);
        }

        Glide.with(this).load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade(0)
                .listener(new RequestListener<String,GlideDrawable>()
                {

                    @Override
                    public boolean onException(Exception e, String model,
                                               Target<GlideDrawable> target, boolean isFirstResource)
                    {

                        mImageError.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model,
                                                   Target<GlideDrawable> target, boolean isFromMemoryCache,
                                                   boolean isFirstResource)
                    {

                        mImageView.setImageDrawable(resource);
                        mPhotoViewAttacher = new PhotoViewAttacher(mImageView);
                        mImageError.setVisibility(View.GONE);
                        setUpPhotoAttacher();
                        return false;
                    }
                })
                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
    }

    @Override
    public void initToolBar()
    {

        mToolBar.setTitle(title);
        setSupportActionBar(mToolBar);
        ActionBar supportActionBar = getSupportActionBar();
        mToolBar.setNavigationOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                onBackPressed();
            }
        });
        if (supportActionBar != null)
        {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mAppBarLayout.setAlpha(0.5f);
        mToolBar.setBackgroundResource(R.color.black_90);
        mAppBarLayout.setBackgroundResource(R.color.black_90);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        getMenuInflater().inflate(R.menu.menu_meizi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int itemId = item.getItemId();
        switch (itemId)
        {
            case R.id.action_fuli_share:
                // 分享
                Observable.just(R.string.app_name)
                        .compose(this.<Integer>bindToLifecycle())
                        .compose(RxPermissions.getInstance(SingleMeiziDetailsActivity.this)
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

                                return GlideDownloadImageUtil.
                                        saveImageToLocal(SingleMeiziDetailsActivity.this, url);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry()
                        .subscribe(new Action1<Uri>()
                        {

                            @Override
                            public void call(Uri uri)
                            {

                                share(uri);
                            }
                        }, new Action1<Throwable>()
                        {

                            @Override
                            public void call(Throwable throwable)
                            {

                                Toast.makeText(SingleMeiziDetailsActivity.this, "分享失败,请重试",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                return true;

            case R.id.action_fuli_save:
                //保存
                saveImageToGallery();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public static Intent LuanchActivity(Activity activity, String url, String title)
    {

        Intent intent = new Intent(activity, SingleMeiziDetailsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_TITLE, title);
        return intent;
    }

    private void setUpPhotoAttacher()
    {

        mPhotoViewAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener()
        {

            @Override
            public void onViewTap(View view, float v, float v1)
            {
                //隐藏ToolBar
                hideOrShowToolbar();
            }
        });

        mPhotoViewAttacher.setOnLongClickListener(new View.OnLongClickListener()
        {

            @Override
            public boolean onLongClick(View v)
            {

                new AlertDialog.Builder(SingleMeiziDetailsActivity.this)
                        .setMessage("是否保存到本地?")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {

                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                                saveImageToGallery();
                                dialog.dismiss();
                            }
                        })
                        .show();


                return true;
            }
        });
    }


    private void saveImageToGallery()
    {

        Observable.just(R.string.app_name)
                .compose(this.<Integer>bindToLifecycle())
                .compose(RxPermissions.getInstance(SingleMeiziDetailsActivity.this)
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

                        return GlideDownloadImageUtil.saveImageToLocal(SingleMeiziDetailsActivity.this, url);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Uri>()
                {

                    @Override
                    public void call(Uri uri)
                    {

                        File appDir = new File(Environment.getExternalStorageDirectory(), ConstantUtil.FILE_DIR);
                        String msg = String.format("图片已保存至 %s 文件夹", appDir.getAbsolutePath());
                        Toast.makeText(SingleMeiziDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }, new Action1<Throwable>()
                {

                    @Override
                    public void call(Throwable throwable)
                    {

                        Toast.makeText(SingleMeiziDetailsActivity.this, "保存失败,请重试", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 分享图片
     *
     * @param uri
     */
    private void share(Uri uri)
    {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, title));
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
}
