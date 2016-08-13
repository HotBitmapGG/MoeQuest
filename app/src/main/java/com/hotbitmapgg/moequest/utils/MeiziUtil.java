package com.hotbitmapgg.moequest.utils;

import com.hotbitmapgg.moequest.entity.douban.DoubanMeizi;
import com.hotbitmapgg.moequest.entity.gank.GankMeizi;
import com.hotbitmapgg.moequest.entity.gank.GankMeiziInfo;
import com.hotbitmapgg.moequest.entity.meizitu.MeiziTu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * 萌妹纸工具类
 */
public class MeiziUtil
{

    private static volatile MeiziUtil mCache;

    private MeiziUtil()
    {

    }

    public static MeiziUtil getInstance()
    {

        if (mCache == null)
        {
            synchronized (MeiziUtil.class)
            {
                if (mCache == null)
                {
                    mCache = new MeiziUtil();
                }
            }
        }

        return mCache;
    }


    /**
     * 保存gank妹子到数据库中
     *
     * @param gankMeiziInfos
     */

    public void putGankMeiziCache(List<GankMeiziInfo> gankMeiziInfos)
    {

        GankMeizi meizi;
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int i = 0; i < gankMeiziInfos.size(); i++)
        {
            meizi = new GankMeizi();
            String url = gankMeiziInfos.get(i).url;
            String desc = gankMeiziInfos.get(i).desc;
            meizi.setUrl(url);
            meizi.setDesc(desc);
            realm.copyToRealm(meizi);
        }
        realm.commitTransaction();
        realm.close();
    }


    /**
     * 保存豆瓣妹子数据到数据库中
     *
     * @param type
     * @param response
     */
    public void putDoubanMeiziCache(int type, Response<ResponseBody> response)
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
    public List<MeiziTu> parserMeiziTuHtml(String html, String type)
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
            bean.setHeight(354);
            bean.setWidth(236);
            bean.setImageurl(imgelement.attr("data-original"));
            bean.setUrl(aelement.attr("href"));
            bean.setGroupid(url2groupid(bean.getUrl()));
            list.add(bean);
        }
        return list;
    }


    /**
     * 解析自拍妹子Html
     *
     * @param html
     * @param type
     * @return
     */
    public List<MeiziTu> parserMeiziTuByAutodyne(String html, String type)
    {

        List<MeiziTu> list = new ArrayList<>();
        Document doc = Jsoup.parse(html);

        Elements p = doc.getElementsByTag("p");
        MeiziTu meiziTu;
        Element img;
        for (int i = 0; i < 15; i++)
        {
            meiziTu = new MeiziTu();
            img = p.get(i).select("img").first();
            String src = img.attr("src");
            String title = img.attr("alt");
            meiziTu.setOrder(i);
            meiziTu.setType(type);
            meiziTu.setWidth(0);
            meiziTu.setHeight(0);
            meiziTu.setImageurl(src);
            meiziTu.setTitle(title);
            list.add(meiziTu);
        }
        return list;
    }

    /**
     * 获取妹子图的GroupId
     *
     * @param url
     * @return
     */
    private int url2groupid(String url)
    {

        return Integer.parseInt(url.split("/")[3]);
    }

    /**
     * 保存妹子图数据到数据库中
     *
     * @param list
     */
    public void putMeiziTuCache(List<MeiziTu> list)
    {

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(list);
        realm.commitTransaction();
        realm.close();
    }
}
