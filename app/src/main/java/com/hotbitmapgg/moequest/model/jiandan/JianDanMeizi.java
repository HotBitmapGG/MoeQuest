package com.hotbitmapgg.moequest.model.jiandan;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hcc on 16/6/28 20:26
 * 100332338@qq.com
 * <p/>
 * "comment_ID":"3184860", "comment_post_ID":"21183", "comment_author":
 * "FD", "comment_author_email":"user@example.com", "comment_author_url":
 * "", "comment_author_IP":"211.162.219.188", "comment_date":
 * "2016-06-28 20:51:34", "comment_date_gmt":"2016-06-28 12:51:34", "comment_content":
 * "<img src=\"http:\/\/ww4.sinaimg.cn\/mw600\/a15b4afegw1f5b87y6cw5j20xc18g1kx.jpg\" \/>", "comment_karma":
 * "0", "comment_approved":"1", "comment_agent":
 * "Mozilla\/5.0 (Windows NT 10.0; Win64; x64; rv:47.0) Gecko\/20100101 Firefox\/47.0", "comment_type":
 * "", "comment_parent":"0", "user_id":"0", "comment_subscribe":"N", "comment_reply_ID":
 * "0", "vote_positive":"1", "vote_negative":"4", "vote_ip_pool":"", "text_content":
 * "", "pics":["http:\/\/ww4.sinaimg.cn\/mw600\/a15b4afegw1f5b87y6cw5j20xc18g1kx.jpg"],
 * "videos":[]
 */
public class JianDanMeizi
{


    public String status;

    @SerializedName("current_page")
    public int currentPage;

    @SerializedName("total_comments")
    public int totalComments;

    @SerializedName("page_count")
    public int pageCount;

    public int count;

    public List<JianDanMeiziData> comments;


    public class JianDanMeiziData
    {

        @SerializedName("comment_ID")
        public String commentID;

        @SerializedName("comment_author")
        public String commentAuthor;

        @SerializedName("comment_date")
        public String commentDate;

        @SerializedName("text_content")
        public String textContent;

        @SerializedName("vote_positive")
        public String votePositive;

        @SerializedName("vote_negative")
        public String voteNegative;

        @SerializedName("comment_counts")
        public String commentCounts;

        public String[] pics;
    }
}
