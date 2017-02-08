package com.hotbitmapgg.moequest.module.commonality;

import butterknife.Bind;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.base.RxBaseFragment;
import com.hotbitmapgg.moequest.rx.RxBus;
import com.hotbitmapgg.moequest.utils.ConstantUtil;
import com.hotbitmapgg.moequest.utils.GlideDownloadImageUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import java.io.File;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoViewAttacher;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by hcc on 16/7/5 21:14
 * 100332338@qq.com
 * <p/>
 * 去掉了重复的界面,整合为一个界面
 * 妹子详情Fragment显示界面
 */
public class MeiziDetailsFragment extends RxBaseFragment
    implements RequestListener<String, GlideDrawable> {

  @Bind(R.id.meizi)
  ImageView mImageView;

  @Bind(R.id.tv_image_error)
  TextView mImageError;

  private static final String EXTRA_URL = "extra_url";

  private String url;

  private PhotoViewAttacher mPhotoViewAttacher;


  public static MeiziDetailsFragment newInstance(String url) {

    MeiziDetailsFragment mMeiziFragment = new MeiziDetailsFragment();
    Bundle mBundle = new Bundle();
    mBundle.putString(EXTRA_URL, url);
    mMeiziFragment.setArguments(mBundle);

    return mMeiziFragment;
  }


  @Override
  public int getLayoutId() {

    return R.layout.fragment_meizi_details;
  }


  @Override
  public void initViews() {

    url = getArguments().getString(EXTRA_URL);
    Glide.with(this).load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .crossFade(0)
        .listener(this)
        .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
  }


  private void saveImageToGallery() {

    Observable.just(R.string.app_name)
        .compose(this.bindToLifecycle())
        .compose(RxPermissions.getInstance(getActivity())
            .ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        .observeOn(Schedulers.io())
        .filter(aBoolean -> aBoolean)
        .flatMap(new Func1<Boolean, Observable<Uri>>() {

          @Override
          public Observable<Uri> call(Boolean aBoolean) {

            return GlideDownloadImageUtil.saveImageToLocal(getActivity(), url);
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(uri -> {

          File appDir = new File(Environment.getExternalStorageDirectory(),
              ConstantUtil.FILE_DIR);
          String msg = String.format("图片已保存至 %s 文件夹", appDir.getAbsolutePath());
          Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }, throwable -> {

          Toast.makeText(getActivity(), "保存失败,请重试", Toast.LENGTH_SHORT).show();
        });
  }


  @Override
  public boolean onException(Exception e, String model,
                             Target<GlideDrawable> target, boolean isFirstResource) {

    mImageError.setVisibility(View.VISIBLE);
    return false;
  }


  @Override
  public boolean onResourceReady(GlideDrawable resource, String model,
                                 Target<GlideDrawable> target, boolean
                                     isFromMemoryCache, boolean isFirstResource) {

    mImageView.setImageDrawable(resource);
    mPhotoViewAttacher = new PhotoViewAttacher(mImageView);
    mImageError.setVisibility(View.GONE);
    setPhotoViewAttacher();
    return false;
  }


  private void setPhotoViewAttacher() {
    mPhotoViewAttacher.setOnLongClickListener(v -> {

      new AlertDialog.Builder(getActivity())
          .setMessage("是否保存到本地?")
          .setNegativeButton("取消", (dialog, which) -> dialog.cancel())
          .setPositiveButton("确定", (dialog, which) -> {
            saveImageToGallery();
            dialog.dismiss();
          })
          .show();

      return true;
    });

    mPhotoViewAttacher.setOnViewTapListener(
        (view, v, v1) -> RxBus.getInstance().post("hideAppBar"));
    mImageError.setOnClickListener(v -> RxBus.getInstance().post("hideAppBar"));
  }


  public View getSharedElement() {

    return mImageView;
  }
}
