package com.hotbitmapgg.moequest.ui.fragment;


import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;

import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.adapter.GankMeiziAdapter;
import com.hotbitmapgg.moequest.adapter.base.AbsRecyclerViewAdapter;
import com.hotbitmapgg.moequest.base.RxBaseFragment;
import com.hotbitmapgg.moequest.model.gank.GankMeizi;
import com.hotbitmapgg.moequest.model.gank.GankMeiziInfo;
import com.hotbitmapgg.moequest.model.gank.GankMeiziResult;
import com.hotbitmapgg.moequest.network.RetrofitHelper;
import com.hotbitmapgg.moequest.rx.RxBus;
import com.hotbitmapgg.moequest.ui.activity.GankMeiziPageActivity;
import com.hotbitmapgg.moequest.utils.LogUtil;
import com.hotbitmapgg.moequest.utils.SnackbarUtil;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by hcc on 16/6/25 19:48
 * 100332338@qq.com
 * <p/>
 * gank妹子
 */
public class GankMeiziFragment extends RxBaseFragment
{

    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.recycle)
    RecyclerView mRecyclerView;

    private StaggeredGridLayoutManager mLayoutManager;

    private RealmResults<GankMeizi> gankMeizis;

    private int pageNum = 20;

    private int page = 1;

    private static final int PRELOAD_SIZE = 6;

    private boolean mIsLoadMore = true;

    private GankMeiziAdapter mAdapter;

    private Realm realm;

    private int imageIndex;

    public static GankMeiziFragment newInstance()
    {

        return new GankMeiziFragment();
    }

    @Override
    public int getLayoutId()
    {

        return R.layout.fragment_gank_meizi;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews()
    {

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {

            @Override
            public void onRefresh()
            {

                page = 1;
                clearCache();
                getGankMeizi();
            }
        });
        showProgress();

        realm = Realm.getDefaultInstance();
        gankMeizis = realm.where(GankMeizi.class).findAll();


        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(OnLoadMoreListener(mLayoutManager));
        mAdapter = new GankMeiziAdapter(mRecyclerView, gankMeizis);
        mRecyclerView.setAdapter(mAdapter);


        RxBus.getInstance().toObserverable(Intent.class)
                .subscribe(new Action1<Intent>()
                {

                    @Override
                    public void call(Intent intent)
                    {

                        imageIndex = intent.getIntExtra("index", -1);
                        scrollIndex();
                        finishTask();
                    }
                }, new Action1<Throwable>()
                {

                    @Override
                    public void call(Throwable throwable)
                    {

                        LogUtil.all(throwable.getMessage());
                    }
                });

        setEnterSharedElementCallback(new SharedElementCallback()
        {

            @Override
            public void onMapSharedElements(List<String> names, Map<String,View> sharedElements)
            {
                super.onMapSharedElements(names, sharedElements);
                String newTransitionName = gankMeizis.get(imageIndex).getUrl();
                View newSharedView = mRecyclerView.findViewWithTag(newTransitionName);
                if (newSharedView != null)
                {
                    names.clear();
                    names.add(newTransitionName);
                    sharedElements.clear();
                    sharedElements.put(newTransitionName, newSharedView);
                }
            }
        });
    }


    public void showProgress()
    {

        mSwipeRefreshLayout.post(new Runnable()
        {

            @Override
            public void run()
            {

                mSwipeRefreshLayout.setRefreshing(true);
                clearCache();
                getGankMeizi();
            }
        });
    }

    private void clearCache()
    {

        try
        {
            realm.beginTransaction();
            realm.where(GankMeizi.class)
                    .findAll().clear();
            realm.commitTransaction();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void getGankMeizi()
    {

        RetrofitHelper.getGankMeiziApi()
                .getGankMeizi(pageNum, page)
                .filter(new Func1<GankMeiziResult,Boolean>()
                {

                    @Override
                    public Boolean call(GankMeiziResult gankMeiziResult)
                    {

                        return !gankMeiziResult.error;
                    }
                })
                .map(new Func1<GankMeiziResult,List<GankMeiziInfo>>()
                {

                    @Override
                    public List<GankMeiziInfo> call(GankMeiziResult gankMeiziResult)
                    {

                        return gankMeiziResult.gankMeizis;
                    }
                })
                .doOnNext(new Action1<List<GankMeiziInfo>>()
                {

                    @Override
                    public void call(List<GankMeiziInfo> gankMeiziInfos)
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
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<GankMeiziInfo>>()
                {

                    @Override
                    public void call(List<GankMeiziInfo> gankMeiziInfos)
                    {


                        finishTask();
                    }
                }, new Action1<Throwable>()
                {

                    @Override
                    public void call(Throwable throwable)
                    {

                        mSwipeRefreshLayout.post(new Runnable()
                        {

                            @Override
                            public void run()
                            {

                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        });

                        SnackbarUtil.showMessage(mRecyclerView, getString(R.string.error_message));
                    }
                });
    }


    private void finishTask()
    {

        if (page * pageNum - pageNum - 1 > 0)
            mAdapter.notifyItemRangeChanged(page * pageNum - pageNum - 1, pageNum);
        else
            mAdapter.notifyDataSetChanged();
        if (mSwipeRefreshLayout.isRefreshing())
        {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        mAdapter.setOnItemClickListener(new AbsRecyclerViewAdapter.OnItemClickListener()
        {

            @Override
            public void onItemClick(int position, AbsRecyclerViewAdapter.ClickableViewHolder holder)
            {

                Intent intent = GankMeiziPageActivity.luanch(getActivity(), position);
                if (Build.VERSION.SDK_INT >= 21)
                {
                    startActivity(intent,
                            ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                                    holder.getParentView().findViewById(R.id.item_img),
                                    gankMeizis.get(position).getUrl()).toBundle());
                } else
                {
                    startActivity(intent);
                }
            }
        });
    }

    RecyclerView.OnScrollListener OnLoadMoreListener(StaggeredGridLayoutManager layoutManager)
    {

        return new RecyclerView.OnScrollListener()
        {

            @Override
            public void onScrolled(RecyclerView rv, int dx, int dy)
            {

                boolean isBottom = mLayoutManager.findLastCompletelyVisibleItemPositions(
                        new int[2])[1] >= mAdapter.getItemCount() - PRELOAD_SIZE;
                if (!mSwipeRefreshLayout.isRefreshing() && isBottom)
                {
                    if (!mIsLoadMore)
                    {
                        mSwipeRefreshLayout.setRefreshing(true);
                        page++;
                        getGankMeizi();
                    } else
                    {
                        mIsLoadMore = false;
                    }
                }
            }
        };
    }


    public void scrollIndex()
    {
        if (imageIndex != -1)
        {
            mRecyclerView.scrollToPosition(imageIndex);
            mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
            {

                @Override
                public boolean onPreDraw()
                {

                    mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mRecyclerView.requestLayout();
                    return true;
                }
            });
        }

        LogUtil.all("onResume" + "  index " + imageIndex);
    }
}
