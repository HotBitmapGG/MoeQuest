package com.hotbitmapgg.moequest.network.api;

import com.hotbitmapgg.moequest.model.jiandan.JianDanMeizi;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by hcc on 16/6/26 16:49
 * 100332338@qq.com
 * <p/>
 * 煎蛋妹子请求Api
 * http://jandan.net/?oxwlxojflwblxbsapi=jandan.get_ooxx_comments&page=
 */
public interface JianDanMeiziApi
{

    @GET("?oxwlxojflwblxbsapi=jandan.get_ooxx_comments")
    Observable<JianDanMeizi> getJianDanMeizi(@Query("page") int page);
}
