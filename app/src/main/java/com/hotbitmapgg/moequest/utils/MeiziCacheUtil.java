package com.hotbitmapgg.moequest.utils;

import android.content.Context;

import com.hotbitmapgg.moequest.model.douban.DoubanMeizi;
import com.hotbitmapgg.moequest.model.meizitu.MeiziTu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class MeiziCacheUtil
{

    private static volatile MeiziCacheUtil mCache;

    private MeiziCacheUtil()
    {

        throw new RuntimeException("Unable to create the object");
    }

    public static MeiziCacheUtil getInstance()
    {

        if (mCache == null)
        {
            synchronized (MeiziCacheUtil.class)
            {
                if (mCache == null)
                {
                    mCache = new MeiziCacheUtil();
                }
            }
        }

        return mCache;
    }

    /**
     * 保存豆瓣妹子数据到数据库中
     *
     * @param context
     * @param type
     * @param response
     */
    public void putDoubanMeiziCache(Context context, int type, Response<ResponseBody> response)
    {

        try
        {

            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();

            String string = response.body().string();
            Document parse = Jsoup.parse(string);
            Elements elements = parse.select("div[class=thumbnail]>div[class=img_single]>a>img");
            DoubanMeizi meizi;
            for (Element e : elements)
            {
                String src = e.attr("src");
                String title = e.attr("title");

                meizi = new DoubanMeizi();
                meizi.setUrl(src);
                meizi.setTitle(title);
                meizi.setType(type);

                realm.copyToRealm(meizi);
            }

            realm.commitTransaction();
            realm.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 解析妹子图html
     *
     * @param html
     * @param type
     * @return
     */
    public static List<MeiziTu> ParserMeiziTuHtml(String html, String type)
    {

        List<MeiziTu> list = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("li");

        Element aelement;
        Element imgelement;
        for (int i = 7; i < links.size(); i++)
        {
            imgelement = links.get(i).select("img").first();
            aelement = links.get(i).select("a").first();
            MeiziTu bean = new MeiziTu();
            bean.setOrder(i);

            bean.setTitle(imgelement.attr("alt").toString());
            bean.setType(type);
            bean.setHeight(354);//element.attr("height")
            bean.setWidth(236);
            bean.setImageurl(imgelement.attr("data-original"));
            bean.setUrl(aelement.attr("href"));
            bean.setGroupid(url2groupid(bean.getUrl()));
            list.add(bean);
        }
        return list;
    }

    /**
     * 获取妹子图的GroupId
     *
     * @param url
     * @return
     */
    private static int url2groupid(String url)
    {

        return Integer.parseInt(url.split("/")[3]);
    }

    /**
     * 保存妹子图数据到数据库中
     *
     * @param list
     */
    public static void putMeiziTuCache(List<MeiziTu> list)
    {

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(list);
        realm.commitTransaction();
        realm.close();
    }
}
