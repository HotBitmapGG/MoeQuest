package com.hotbitmapgg.moequest.network.api;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface MeiziTuApi
{

    /**
     * 根据类型查询对应的妹子图
     *
     * @param type
     * @param pageNum
     * @return
     */
    @GET("{type}/page/{pageNum}")
    Observable<ResponseBody> getMeiziTuApi(@Path("type") String type, @Path("pageNum") int pageNum);


    /**
     * 分页查询对应的妹子图
     *
     * @param type
     * @param page
     * @return
     */
    @GET("{type}/comment-page-{page}#comments")
    Observable<ResponseBody> getHomeMeiziApi(@Path("type") String type, @Path("page") int page);
}
