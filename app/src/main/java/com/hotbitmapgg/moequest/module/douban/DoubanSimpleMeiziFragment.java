package com.hotbitmapgg.moequest.module.douban;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.adapter.DoubanMeiziAdapter;
import com.hotbitmapgg.moequest.adapter.base.AbsRecyclerViewAdapter;
import com.hotbitmapgg.moequest.base.RxBaseFragment;
import com.hotbitmapgg.moequest.entity.douban.DoubanMeizi;
import com.hotbitmapgg.moequest.network.RetrofitHelper;
import com.hotbitmapgg.moequest.rx.RxBus;
import com.hotbitmapgg.moequest.utils.LogUtil;
import com.hotbitmapgg.moequest.utils.MeiziUtil;
import com.hotbitmapgg.moequest.utils.SnackbarUtil;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;

/**
 * Created by hcc on 16/8/13 12:48
 * 100332338@qq.com
 * <p/>
 * 豆瓣妹子详情界面
 */
public class DoubanSimpleMeiziFragment extends RxBaseFragment
{


    @Bind(R.id.recycle)
    RecyclerView mRecyclerView;

    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int PRELOAD_SIZE = 6;

    public static final String EXTRA_CID = "extra_cid";

    public static final String EXTRA_TYPE = "extra_type";

    private boolean mIsLoadMore = true;

    private int cid;

    private int pageNum = 20;

    private int page = 1;

    private DoubanMeiziAdapter mAdapter;

    private StaggeredGridLayoutManager mLayoutManager;

    private int type;

    private Realm realm;

    private RealmResults<DoubanMeizi> doubanMeizis;

    private int imageIndex;

    private boolean mIsRefreshing = false;


    public static DoubanSimpleMeiziFragment newInstance(int cid, int type)
    {

        DoubanSimpleMeiziFragment mDoubanSimpleMeiziFragment = new DoubanSimpleMeiziFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_CID, cid);
        bundle.putInt(EXTRA_TYPE, type);
        mDoubanSimpleMeiziFragment.setArguments(bundle);

        return mDoubanSimpleMeiziFragment;
    }

    @Override
    public int getLayoutId()
    {

        return R.layout.fragment_simple_meizi;
    }

    @Override
    public void initViews()
    {

        cid = getArguments().getInt(EXTRA_CID);
        type = getArguments().getInt(EXTRA_TYPE);

        showProgress();
        realm = Realm.getDefaultInstance();
        doubanMeizis = realm.where(DoubanMeizi.class)
                .equalTo("type", type)
                .findAll();

        initRecycleView();

        RxBus.getInstance().toObserverable(Intent.class)
                .compose(this.<Intent>bindToLifecycle())
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
                String newTransitionName = doubanMeizis.get(imageIndex).getUrl();
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

    private void showProgress()
    {

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {

            @Override
            public void onRefresh()
            {

                page = 1;
                mIsRefreshing = true;
                clearCache();
                getDoubanMeizi();
            }
        });

        mSwipeRefreshLayout.postDelayed(new Runnable()
        {

            @Override
            public void run()
            {

                mSwipeRefreshLayout.setRefreshing(true);
                mIsRefreshing = true;
                clearCache();
                getDoubanMeizi();
            }
        }, 500);
    }

    private void initRecycleView()
    {

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(OnLoadMoreListener(mLayoutManager));
        mAdapter = new DoubanMeiziAdapter(mRecyclerView, doubanMeizis);
        mRecyclerView.setAdapter(mAdapter);
        setRecycleScrollBug();
    }

    private void clearCache()
    {

        try
        {
            realm.beginTransaction();
            realm.where(DoubanMeizi.class)
                    .equalTo("type", type)
                    .findAll().clear();
            realm.commitTransaction();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void getDoubanMeizi()
    {

        RetrofitHelper.getDoubanMeiziApi()
                .getDoubanMeizi(cid, page)
                .enqueue(new Callback<ResponseBody>()
                {

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {


                        MeiziUtil.getInstance().putDoubanMeiziCache(type, response);
                        finishTask();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t)
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
        mIsRefreshing = false;

        mAdapter.setOnItemClickListener(new AbsRecyclerViewAdapter.OnItemClickListener()
        {

            @Override
            public void onItemClick(int position, AbsRecyclerViewAdapter.ClickableViewHolder holder)
            {

                Intent intent = DoubanMeiziPageActivity.luanch(getActivity(), position, type);
                if (Build.VERSION.SDK_INT >= 22)
                {
                    startActivity(intent,
                            ActivityOptions.makeSceneTransitionAnimation(getActivity(),
                                    holder.getParentView().findViewById(R.id.item_img),
                                    doubanMeizis.get(position).getUrl()).toBundle());
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
                        getDoubanMeizi();
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
    }

    private void setRecycleScrollBug()
    {

        mRecyclerView.setOnTouchListener(new View.OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {


                if (mIsRefreshing)
                {
                    return true;
                } else
                {
                    return false;
                }
            }
        });
    }
}
