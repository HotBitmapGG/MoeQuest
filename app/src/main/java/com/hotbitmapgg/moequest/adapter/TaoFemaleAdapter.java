package com.hotbitmapgg.moequest.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.adapter.base.AbsRecyclerViewAdapter;
import com.hotbitmapgg.moequest.entity.taomodel.Contentlist;
import com.hotbitmapgg.moequest.module.commonality.SingleMeiziDetailsActivity;
import com.hotbitmapgg.moequest.module.taogirl.TaoFemalePagerActivity;
import com.hotbitmapgg.moequest.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

public class TaoFemaleAdapter extends AbsRecyclerViewAdapter
{

    private List<Contentlist> datas = new ArrayList<>();

    Context mContext;

    public TaoFemaleAdapter(RecyclerView recyclerView, List<Contentlist> datas, Context context)
    {

        super(recyclerView);
        this.datas = datas;
        this.mContext = context;
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
                    .placeholder(R.drawable.ic_slide_menu_avatar_no_login)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemViewHolder.mAvatar);

            itemViewHolder.mUserName.setText(contentlist.realName);
            itemViewHolder.mUserLocation.setText(contentlist.city);
            itemViewHolder.mUserWidth.setText("体重: " + contentlist.weight);
            itemViewHolder.mUserHeight.setText("身高: " + contentlist.height);
            itemViewHolder.mUserFansNum.setText(contentlist.totalFanNum);
            itemViewHolder.mType.setText(contentlist.type);

            setImageList(itemViewHolder, contentlist.imgList, contentlist.avatarUrl, contentlist.realName);
        }
        super.onBindViewHolder(holder, position);
    }

    private void setImageList(final ItemViewHolder itemViewHolder, final ArrayList<String> imgList, final String url, final String name)
    {

        Glide.clear(itemViewHolder.mImage);
        Glide.with(getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(itemViewHolder.mImage);

        itemViewHolder.mImageNum.setText(imgList.size() + "张");

        itemViewHolder.mImage.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                if (imgList.size() > 0)
                {
                    TaoFemalePagerActivity.luancher((Activity) getContext(), imgList, 0);
                } else
                {
                    Intent intent = SingleMeiziDetailsActivity.LuanchActivity((Activity) mContext, url, name);
                    if (android.os.Build.VERSION.SDK_INT >= 21)
                    {
                        mContext.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, itemViewHolder.mImage, "transitionImg").toBundle());
                    } else
                    {
                        mContext.startActivity(intent);
                    }
                }
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

        public ImageView mImage;

        public TextView mUserFansNum;

        public TextView mUserHeight;

        public TextView mUserWidth;

        public TextView mType;

        public TextView mImageNum;

        public ItemViewHolder(View itemView)
        {

            super(itemView);
            mAvatar = $(R.id.tao_avatar);
            mUserName = $(R.id.tao_name);
            mUserLocation = $(R.id.tao_location);
            mImage = $(R.id.tao_image);
            mUserFansNum = $(R.id.tao_fans_num);
            mUserHeight = $(R.id.tao_height);
            mUserWidth = $(R.id.tao_width);
            mType = $(R.id.tao_type);
            mImageNum = $(R.id.tao_image_num);
        }
    }
}
