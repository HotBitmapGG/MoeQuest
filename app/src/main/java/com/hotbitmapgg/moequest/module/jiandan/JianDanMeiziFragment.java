package com.hotbitmapgg.moequest.module.jiandan;

import butterknife.Bind;
import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.adapter.JiandanMeiziAdapter;
import com.hotbitmapgg.moequest.adapter.helper.EndlessRecyclerOnScrollListener;
import com.hotbitmapgg.moequest.adapter.helper.HeaderViewRecyclerAdapter;
import com.hotbitmapgg.moequest.base.RxBaseFragment;
import com.hotbitmapgg.moequest.entity.jiandan.JianDanMeizi;
import com.hotbitmapgg.moequest.module.commonality.SingleMeiziDetailsActivity;
import com.hotbitmapgg.moequest.network.RetrofitHelper;
import com.hotbitmapgg.moequest.utils.LogUtil;
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
 * Created by hcc on 16/6/28 20:30
 * 100332338@qq.com
 * <p/>
 * 煎蛋妹子
 */
public class JianDanMeiziFragment extends RxBaseFragment {

  @Bind(R.id.recycle)
  RecyclerView mRecyclerView;

  @Bind(R.id.swipe_refresh)
  SwipeRefreshLayout mSwipeRefreshLayout;

  private int page = 1;

  private int count = 25;

  private List<JianDanMeizi.JianDanMeiziData> jianDanMeiziDataList = new ArrayList<>();

  private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;

  private JiandanMeiziAdapter mAdapter;

  private View footView;

  private boolean mIsRefreshing = false;


  public static JianDanMeiziFragment newInstance() {

    return new JianDanMeiziFragment();
  }


  @Override
  public int getLayoutId() {

    return R.layout.fragment_jiandan_meizi;
  }


  @Override
  public void initViews() {

    showProgress();
    initRecycle();
  }


  private void getJianDanMeizi() {

    RetrofitHelper.getJianDanApi()
        .getJianDanMeizi(page)
        .compose(this.bindToLifecycle())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(jianDanMeizi -> {

          List<JianDanMeizi.JianDanMeiziData> comments = jianDanMeizi.comments;
          if (comments.size() < count) {
            footView.setVisibility(View.GONE);
          }

          jianDanMeiziDataList.addAll(comments);
          finishTask();
        }, throwable -> {

          LogUtil.all(throwable.getMessage());
          mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));
          footView.setVisibility(View.GONE);
          SnackbarUtil.showMessage(mRecyclerView, getString(R.string.error_message));
        });
  }


  private void finishTask() {

    if (page * count - count - 1 > 0) {
      mAdapter.notifyItemRangeChanged(page * count - count - 1, count);
    } else {
      mAdapter.notifyDataSetChanged();
    }

    if (mSwipeRefreshLayout.isRefreshing()) {
      mSwipeRefreshLayout.setRefreshing(false);
    }

    mIsRefreshing = false;

    mAdapter.setOnItemClickListener((position, holder) -> {

      Intent intent = SingleMeiziDetailsActivity.LuanchActivity(getActivity(),
          jianDanMeiziDataList.get(position).pics[0],
          jianDanMeiziDataList.get(position).commentAuthor);
      if (android.os.Build.VERSION.SDK_INT >= 21) {
        startActivity(intent, ActivityOptions.
            makeSceneTransitionAnimation(getActivity(),
                holder.getParentView().findViewById(R.id.item_fill_image),
                "transitionImg").toBundle());
      } else {
        startActivity(intent);
      }
    });
  }


  private void initRecycle() {

    mRecyclerView.setHasFixedSize(true);
    LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
    mRecyclerView.setLayoutManager(mLinearLayoutManager);
    mAdapter = new JiandanMeiziAdapter(mRecyclerView, jianDanMeiziDataList);
    mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mAdapter);
    createFootView();
    mRecyclerView.setAdapter(mHeaderViewRecyclerAdapter);
    mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {

      @Override
      public void onLoadMore(int currentPage) {

        page++;
        getJianDanMeizi();
        footView.setVisibility(View.VISIBLE);
      }
    });
    setRecycleScrollBug();
  }


  private void showProgress() {

    mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
    mSwipeRefreshLayout.setOnRefreshListener(() -> {

      page = 1;
      jianDanMeiziDataList.clear();
      mIsRefreshing = true;
      getJianDanMeizi();
    });
    mSwipeRefreshLayout.postDelayed(() -> {

      mSwipeRefreshLayout.setRefreshing(true);
      mIsRefreshing = true;
      getJianDanMeizi();
    }, 500);
  }


  private void createFootView() {

    footView = LayoutInflater.from(getActivity())
        .inflate(R.layout.load_more_foot_layout, mRecyclerView, false);
    mHeaderViewRecyclerAdapter.addFooterView(footView);
    footView.setVisibility(View.GONE);
  }


  private void setRecycleScrollBug() {

    mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
  }
}
