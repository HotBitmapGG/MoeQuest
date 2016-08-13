package com.hotbitmapgg.moequest.network.api;

import com.hotbitmapgg.moequest.entity.taomodel.TaoFemale;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface TaoFemaleaApi
{

    /**
     * 来自易源接口的淘女郎
     *
     * @param page
     * @param appId
     * @param sign
     * @return
     */
    @GET("126-2")
    Observable<TaoFemale> getTaoFemale(@Query("page") String page,
                                       @Query("showapi_appid") String appId,
                                       @Query("showapi_sign") String sign);
}
