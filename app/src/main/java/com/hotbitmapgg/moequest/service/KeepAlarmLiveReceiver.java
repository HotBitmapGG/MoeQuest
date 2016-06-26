package com.hotbitmapgg.moequest.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hotbitmapgg.moequest.utils.AlarmManagerUtils;


/**
 * Created by hcc on 16/6/25 18:05
 * 100332338@qq.com
 */
public class KeepAlarmLiveReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {

        if (intent != null && Intent.ACTION_USER_PRESENT.equals(intent.getAction()))
        {
            AlarmManagerUtils.register(context);
        }
    }
}
