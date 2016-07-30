package com.hotbitmapgg.moequest.network.api;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface HuaBanMeiziApi
{

    /**
     * 来自易源接口的花瓣妹子
     *
     * @param num
     * @param page
     * @param appId
     * @param type
     * @param sign
     * @return
     */
    @GET("819-1")
    Observable<ResponseBody> getHuaBanMeizi(@Query("num") String num,
                                            @Query("page") String page,
                                            @Query("showapi_appid") String appId,
                                            @Query("type") String type,
                                            @Query("showapi_sign") String sign);
}
