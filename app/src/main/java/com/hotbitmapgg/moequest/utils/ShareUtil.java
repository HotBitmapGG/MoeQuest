package com.hotbitmapgg.moequest.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by hcc on 16/6/25 18:05
 * 100332338@qq.com
 */
public class ShareUtil
{

    /**
     * 分享链接
     *
     * @param url
     * @param title
     */
    public static void shareLink(String url, String title, Context context)
    {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, "来自「萌妹」的分享:" + url);
        context.startActivity(Intent.createChooser(intent, title));
    }

    /**
     * 分享图片
     *
     * @param uri
     */
    public static void sharePic(Uri uri, String desc, Context context)
    {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(shareIntent, desc));
    }
}
