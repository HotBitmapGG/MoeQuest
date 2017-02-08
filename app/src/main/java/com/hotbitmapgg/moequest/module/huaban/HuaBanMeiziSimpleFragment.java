package com.hotbitmapgg.moequest.module.huaban;

import butterknife.Bind;
import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.adapter.HuaBanMeiziAdapter;
import com.hotbitmapgg.moequest.base.RxBaseFragment;
import com.hotbitmapgg.moequest.entity.huaban.HuaBanMeizi;
import com.hotbitmapgg.moequest.entity.huaban.HuaBanMeiziInfo;
import com.hotbitmapgg.moequest.module.commonality.SingleMeiziDetailsActivity;
import com.hotbitmapgg.moequest.network.RetrofitHelper;
import com.hotbitmapgg.moequest.utils.ConstantUtil;
import com.hotbitmapgg.moequest.utils.SnackbarUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by hcc on 16/8/13 12:50
 * 100332338@qq.com
 * <p/>
 * 花瓣妹子详情界面
 */
public class HuaBanMeiziSimpleFragment extends RxBaseFragment {

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

  private HuaBanMeiziAdapter mAdapter;

  private StaggeredGridLayoutManager mLayoutManager;

  private List<HuaBanMeiziInfo> meiziInfos = new ArrayList<>();

  private boolean mIsRefreshing = false;

  private int type;


  public static HuaBanMeiziSimpleFragment newInstance(int cid, int type) {

    HuaBanMeiziSimpleFragment mHuaBanMeiziSimpleFragment = new HuaBanMeiziSimpleFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(EXTRA_CID, cid);
    bundle.putInt(EXTRA_TYPE, type);
    mHuaBanMeiziSimpleFragment.setArguments(bundle);

    return mHuaBanMeiziSimpleFragment;
  }


  @Override
  public int getLayoutId() {

    return R.layout.fragment_simple_meizi;
  }


  @Override
  public void initViews() {

    cid = getArguments().getInt(EXTRA_CID);
    type = getArguments().getInt(EXTRA_TYPE);

    showProgress();
    initRecycleView();
  }


  private void initRecycleView() {

    mRecyclerView.setHasFixedSize(true);
    mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    mRecyclerView.setLayoutManager(mLayoutManager);
    mRecyclerView.addOnScrollListener(OnLoadMoreListener(mLayoutManager));
    mAdapter = new HuaBanMeiziAdapter(mRecyclerView, meiziInfos);
    mRecyclerView.setAdapter(mAdapter);
    setRecycleScrollBug();
  }


  private void showProgress() {

    mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    mSwipeRefreshLayout.postDelayed(() -> {

      mSwipeRefreshLayout.setRefreshing(true);
      mIsRefreshing = true;
      getHuaBanMeizi();
    }, 500);

    mSwipeRefreshLayout.setOnRefreshListener(() -> {

      page = 1;
      meiziInfos.clear();
      mIsRefreshing = true;
      getHuaBanMeizi();
    });
  }


  private void getHuaBanMeizi() {

    RetrofitHelper.getHuaBanMeiziApi()
        .getHuaBanMeizi(pageNum + "", page + "",
            ConstantUtil.APP_ID, cid + "",
            ConstantUtil.APP_SIGN)
        .compose(this.bindToLifecycle())
        .map(responseBody -> {

          try {
            String string = responseBody.string();

            return HuaBanMeizi.createFromJson(string);
          } catch (IOException e) {
            e.printStackTrace();
            return null;
          }
        })

        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(huaBanMeizi -> {

          meiziInfos.addAll(huaBanMeizi.infos);
          finishTask();
        }, throwable -> {

          mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));

          SnackbarUtil.showMessage(mRecyclerView, getString(R.string.error_message));
        });
  }


  private void finishTask() {

    if (page * pageNum - pageNum - 1 > 0) {
      mAdapter.notifyItemRangeChanged(page * pageNum - pageNum - 1, pageNum);
    } else {
      mAdapter.notifyDataSetChanged();
    }
    if (mSwipeRefreshLayout.isRefreshing()) {
      mSwipeRefreshLayout.setRefreshing(false);
    }

    mIsRefreshing = false;

    mAdapter.setOnItemClickListener((position, holder) -> {

      Intent intent = SingleMeiziDetailsActivity
          .LuanchActivity(getActivity(), meiziInfos.get(position).getThumb(),
              meiziInfos.get(position).getTitle());
      if (android.os.Build.VERSION.SDK_INT >= 21) {
        startActivity(intent, ActivityOptions
            .makeSceneTransitionAnimation(getActivity(),
                holder.getParentView().findViewById(R.id.item_img),
                "transitionImg").toBundle());
      } else {
        startActivity(intent);
      }
    });
  }


  RecyclerView.OnScrollListener OnLoadMoreListener(StaggeredGridLayoutManager layoutManager) {

    return new RecyclerView.OnScrollListener() {

      @Override
      public void onScrolled(RecyclerView rv, int dx, int dy) {

        boolean isBottom = mLayoutManager.findLastCompletelyVisibleItemPositions(
            new int[2])[1] >= mAdapter.getItemCount() - PRELOAD_SIZE;
        if (!mSwipeRefreshLayout.isRefreshing() && isBottom) {
          if (!mIsLoadMore) {
            mSwipeRefreshLayout.setRefreshing(true);
            page++;
            getHuaBanMeizi();
          } else {
            mIsLoadMore = false;
          }
        }
      }
    };
  }


  private void setRecycleScrollBug() {

    mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
  }
}
