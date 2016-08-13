package com.hotbitmapgg.moequest.network.api;

import com.hotbitmapgg.moequest.entity.jiandan.JianDanMeizi;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface JianDanMeiziApi
{

    /**
     * 煎蛋妹子请求Api
     * http://jandan.net/?oxwlxojflwblxbsapi=jandan.get_ooxx_comments&page=
     *
     * @param page
     * @return
     */
    @GET("?oxwlxojflwblxbsapi=jandan.get_ooxx_comments")
    Observable<JianDanMeizi> getJianDanMeizi(@Query("page") int page);
}
