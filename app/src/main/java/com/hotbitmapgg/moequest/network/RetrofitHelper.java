package com.hotbitmapgg.moequest.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.hotbitmapgg.moequest.MoeQuestApp;
import com.hotbitmapgg.moequest.network.api.DoubanMeizhiApi;
import com.hotbitmapgg.moequest.network.api.GankMeiziApi;
import com.hotbitmapgg.moequest.network.api.HuaBanMeiziApi;
import com.hotbitmapgg.moequest.network.api.JianDanMeiziApi;
import com.hotbitmapgg.moequest.network.api.MeiziTuApi;
import com.hotbitmapgg.moequest.network.api.TaoFemaleaApi;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitHelper
{


    public static final String BASE_GANK_URL = "http://gank.io/api/";

    public static final String BASE_HUABAN_URL = "http://route.showapi.com/";

    public static final String BASE_DOUBAN_URL = "http://www.dbmeinv.com/dbgroup/";

    public static final String BASE_JIANDAN_URL = "http://jandan.net/";

    public static final String BASE_MEIZITU_URL = "http://www.mzitu.com/";

    private static OkHttpClient mOkHttpClient;

    static
    {
        initOkHttpClient();
    }

    /**
     * Gank妹子Api
     *
     * @return
     */
    public static GankMeiziApi getGankMeiziApi()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_GANK_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(GankMeiziApi.class);
    }


    /**
     * 花瓣Api
     *
     * @return
     */
    public static HuaBanMeiziApi getHuaBanMeiziApi()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_HUABAN_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(HuaBanMeiziApi.class);
    }

    /**
     * 豆瓣Api
     *
     * @return
     */
    public static DoubanMeizhiApi getDoubanMeiziApi()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_DOUBAN_URL)
                .client(new OkHttpClient())
                .build();

        return retrofit.create(DoubanMeizhiApi.class);
    }

    /**
     * 淘女郎Api
     *
     * @return
     */
    public static TaoFemaleaApi getTaoFemaleApi()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_HUABAN_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(TaoFemaleaApi.class);
    }

    /**
     * 煎蛋Api
     *
     * @return
     */
    public static JianDanMeiziApi getJianDanApi()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_JIANDAN_URL)
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(JianDanMeiziApi.class);
    }


    /**
     * 获取妹子图Api
     *
     * @return
     */
    public static MeiziTuApi getMeiziTuApi()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_MEIZITU_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(MeiziTuApi.class);
    }


    /**
     * 初始化OKHttpClient
     */
    private static void initOkHttpClient()
    {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (mOkHttpClient == null)
        {
            synchronized (RetrofitHelper.class)
            {
                if (mOkHttpClient == null)
                {
                    //设置Http缓存
                    Cache cache = new Cache(new File(MoeQuestApp.getContext().getCacheDir(), "HttpCache"), 1024 * 1024 * 100);

                    mOkHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(interceptor)
                            .addNetworkInterceptor(new StethoInterceptor())
                            .retryOnConnectionFailure(true)
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
    }
}
