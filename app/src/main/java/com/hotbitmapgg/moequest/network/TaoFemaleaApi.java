package com.hotbitmapgg.moequest.network;

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

    //https://route.showapi.com/126-2?order=&page=&showapi_appid=15314&showapi_timestamp=20160625193433&type=&showapi_sign=cf50d9a896b8de0d931bc10c318f2b09


    @GET("126-2")
    Observable<TaoFemale> getTaoFemale(@Query("page") String page,
                                       @Query("showapi_appid") String appId,
                                       @Query("showapi_sign") String sign);
}
