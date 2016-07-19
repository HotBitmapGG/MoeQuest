package com.hotbitmapgg.moequest.network.api;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by hcc on 16/7/19 19:55
 * 100332338@qq.com
 */
public interface MeiziTuApi
{

    @GET("{type}/page/{pageNum}")
    Observable<ResponseBody> getMeiziTuApi(@Path("type") String type, @Path("pageNum") int pageNum);
}
