package com.hotbitmapgg.moequest.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.Transition;
import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.base.RxBaseActivity;

import butterknife.Bind;

/**
 * Created by hcc on 16/6/30 21:57
 * 100332338@qq.com
 * <p/>
 * App欢迎页面
 */
public class AppSplashActivity extends RxBaseActivity
{

    @Bind(R.id.splash_image)
    KenBurnsView mKenBurnsView;

    @Override
    public int getLayoutId()
    {

        return R.layout.activity_splash;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {

        mKenBurnsView.setTransitionListener(new KenBurnsView.TransitionListener()
        {

            @Override
            public void onTransitionStart(Transition transition)
            {

            }

            @Override
            public void onTransitionEnd(Transition transition)
            {

                startActivity(new Intent(AppSplashActivity.this, MainActivity.class));
                AppSplashActivity.this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    public void initToolBar()
    {

    }
}
