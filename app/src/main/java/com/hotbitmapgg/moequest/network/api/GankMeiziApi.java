package com.hotbitmapgg.moequest.network.api;


import com.hotbitmapgg.moequest.entity.gank.GankMeiziResult;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface GankMeiziApi
{

    /**
     * gank妹子,福利
     *
     * @param number
     * @param page
     * @return
     */
    @GET("data/福利/{number}/{page}")
    Observable<GankMeiziResult> getGankMeizi(@Path("number") int number, @Path("page") int page);
}
