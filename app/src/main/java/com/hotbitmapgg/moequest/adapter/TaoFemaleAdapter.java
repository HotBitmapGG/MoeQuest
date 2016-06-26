package com.hotbitmapgg.moequest.adapter;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.adapter.base.AbsRecyclerViewAdapter;
import com.hotbitmapgg.moequest.model.taomodel.Contentlist;
import com.hotbitmapgg.moequest.ui.activity.TaoFemalePagerActivity;
import com.hotbitmapgg.moequest.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcc on 16/6/25 20:55
 * 100332338@qq.com
 */
public class TaoFemaleAdapter extends AbsRecyclerViewAdapter
{

    private List<Contentlist> datas = new ArrayList<>();

    public TaoFemaleAdapter(RecyclerView recyclerView, List<Contentlist> datas)
    {

        super(recyclerView);
        this.datas = datas;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_tao_female, parent, false));
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position)
    {

        if (holder instanceof ItemViewHolder)
        {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Contentlist contentlist = datas.get(position);
            Glide.with(getContext())
                    .load(contentlist.avatarUrl)
                    .centerCrop()
                    .dontAnimate()
                    .placeholder(R.drawable.placeholder_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemViewHolder.mAvatar);

            itemViewHolder.mUserName.setText(contentlist.realName);
            itemViewHolder.mUserLocation.setText(contentlist.city);
            itemViewHolder.mUserWidth.setText("体重: " + contentlist.weight);
            itemViewHolder.mUserHeight.setText("身高: " + contentlist.height);
            itemViewHolder.mUserFansNum.setText(contentlist.totalFanNum);
            itemViewHolder.mType.setText(contentlist.type);

            setImageList(itemViewHolder, contentlist.imgList);
        }
        super.onBindViewHolder(holder, position);
    }

    private void setImageList(ItemViewHolder itemViewHolder, final ArrayList<String> imgList)
    {

        itemViewHolder.mImageList.setHasFixedSize(false);
        itemViewHolder.mImageList.setNestedScrollingEnabled(false);
        itemViewHolder.mImageList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        TaoFemaleImageRecycleAdapter mTaoFemaleImageRecycleAdapter = new TaoFemaleImageRecycleAdapter(itemViewHolder.mImageList, imgList);
        itemViewHolder.mImageList.setAdapter(mTaoFemaleImageRecycleAdapter);
        mTaoFemaleImageRecycleAdapter.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(int position, ClickableViewHolder holder)
            {

                TaoFemalePagerActivity.luancher((Activity) getContext(), imgList, position);
            }
        });
    }

    @Override
    public int getItemCount()
    {

        return datas.size();
    }

    public class ItemViewHolder extends AbsRecyclerViewAdapter.ClickableViewHolder
    {

        public CircleImageView mAvatar;

        public TextView mUserName;

        public TextView mUserLocation;

        public RecyclerView mImageList;

        public TextView mUserFansNum;

        public TextView mUserHeight;

        public TextView mUserWidth;

        public TextView mType;

        public ItemViewHolder(View itemView)
        {

            super(itemView);
            mAvatar = $(R.id.tao_avatar);
            mUserName = $(R.id.tao_name);
            mUserLocation = $(R.id.tao_location);
            mImageList = $(R.id.tao_recycle);
            mUserFansNum = $(R.id.tao_fans_num);
            mUserHeight = $(R.id.tao_height);
            mUserWidth = $(R.id.tao_width);
            mType = $(R.id.tao_type);
        }
    }
}
