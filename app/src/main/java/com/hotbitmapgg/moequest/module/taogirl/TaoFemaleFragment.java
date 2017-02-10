package com.hotbitmapgg.moequest.module.taogirl;

import butterknife.Bind;
import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.adapter.TaoFemaleAdapter;
import com.hotbitmapgg.moequest.adapter.helper.EndlessRecyclerOnScrollListener;
import com.hotbitmapgg.moequest.adapter.helper.HeaderViewRecyclerAdapter;
import com.hotbitmapgg.moequest.base.RxBaseFragment;
import com.hotbitmapgg.moequest.entity.taomodel.Contentlist;
import com.hotbitmapgg.moequest.module.commonality.SingleMeiziDetailsActivity;
import com.hotbitmapgg.moequest.network.RetrofitHelper;
import com.hotbitmapgg.moequest.utils.ConstantUtil;
import com.hotbitmapgg.moequest.utils.SnackbarUtil;
import java.util.ArrayList;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by hcc on 16/6/25 19:48
 * 100332338@qq.com
 * <p/>
 * 淘女郎
 */
public class TaoFemaleFragment extends RxBaseFragment {

  @Bind(R.id.swipe_refresh)
  SwipeRefreshLayout mSwipeRefreshLayout;

  @Bind(R.id.recycle)
  RecyclerView mRecyclerView;

  private int page = 1;

  private int pageNum = 20;

  private View footView;

  private List<Contentlist> datas = new ArrayList<>();

  private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;

  private TaoFemaleAdapter mAdapter;

  private boolean mIsRefreshing = false;


  public static TaoFemaleFragment newInstance() {

    return new TaoFemaleFragment();
  }


  @Override
  public int getLayoutId() {

    return R.layout.activity_tao_female;
  }


  @Override
  public void initViews() {

    initRefreshLayout();
    initRecycleView();
  }


  public void getTaoFemaleData() {

    RetrofitHelper.getTaoFemaleApi()
        .getTaoFemale(String.valueOf(page),
            ConstantUtil.APP_ID, ConstantUtil.APP_SIGN)
        .compose(this.bindToLifecycle())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(taoFemale -> {

          if (taoFemale.showapiResBody.pagebean.contentlist.size() < pageNum) {
            footView.setVisibility(View.GONE);
          }
          datas.addAll(taoFemale.showapiResBody.pagebean.contentlist);
          finishTask();
        }, throwable -> {

          footView.setVisibility(View.GONE);
          SnackbarUtil.showMessage(mRecyclerView, getString(R.string.error_message));
          mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));
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

      Intent intent = SingleMeiziDetailsActivity.
          LuanchActivity(getActivity(),
              datas.get(position).avatarUrl,
              datas.get(position).realName);
      if (android.os.Build.VERSION.SDK_INT >= 21) {
        startActivity(intent, ActivityOptions.
            makeSceneTransitionAnimation(getActivity(),
                holder.getParentView().findViewById(R.id.tao_avatar),
                "transitionImg").toBundle());
      } else {
        startActivity(intent);
      }
    });
  }


  public void initRecycleView() {

    mRecyclerView.setHasFixedSize(true);
    LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
    mRecyclerView.setLayoutManager(mLinearLayoutManager);
    mAdapter = new TaoFemaleAdapter(mRecyclerView, datas, getActivity());
    mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mAdapter);
    createFootView();
    mRecyclerView.setAdapter(mHeaderViewRecyclerAdapter);
    mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {

      @Override
      public void onLoadMore(int currentPage) {

        page++;
        footView.setVisibility(View.VISIBLE);
        getTaoFemaleData();
      }
    });
    setRecycleScrollBug();
  }


  private void createFootView() {

    footView = LayoutInflater.from(getActivity())
        .inflate(R.layout.load_more_foot_layout, mRecyclerView, false);
    mHeaderViewRecyclerAdapter.addFooterView(footView);
    footView.setVisibility(View.GONE);
  }


  private void initRefreshLayout() {

    mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    mSwipeRefreshLayout.setOnRefreshListener(() -> {

      page = 1;
      datas.clear();
      mIsRefreshing = true;
      getTaoFemaleData();
    });
    showRefreshProgress();
  }


  public void showRefreshProgress() {

    mSwipeRefreshLayout.postDelayed(() -> {

      mSwipeRefreshLayout.setRefreshing(true);
      mIsRefreshing = true;
      getTaoFemaleData();
    }, 500);
  }


  private void setRecycleScrollBug() {

    mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
  }
}
