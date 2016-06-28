package com.hotbitmapgg.moequest.network.api;

import com.hotbitmapgg.moequest.model.taomodel.TaoFemale;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by hcc on 16/6/25 19:44
 * 100332338@qq.com
 */
public interface TaoFemaleaApi
{

    @GET("126-2")
    Observable<TaoFemale> getTaoFemale(@Query("page") String page,
                                       @Query("showapi_appid") String appId,
                                       @Query("showapi_sign") String sign);
}
