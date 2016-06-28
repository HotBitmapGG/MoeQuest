package com.hotbitmapgg.moequest.adapter;

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
import com.hotbitmapgg.moequest.model.jiandan.JianDanMeizi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcc on 16/6/28 21:35
 * 100332338@qq.com
 */
public class JiandanMeiziAdapter extends AbsRecyclerViewAdapter
{

    private List<JianDanMeizi.JianDanMeiziData> datas = new ArrayList<>();

    public JiandanMeiziAdapter(RecyclerView recyclerView, List<JianDanMeizi.JianDanMeiziData> datas)
    {

        super(recyclerView);
        this.datas = datas;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_jiandan_meizi, parent, false));
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position)
    {

        if (holder instanceof ItemViewHolder)
        {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Glide.with(getContext())
                    .load(datas.get(position).pics[0])
                    .centerCrop()
                    .crossFade(0)
                    .placeholder(R.drawable.placeholder_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemViewHolder.mImage);

            itemViewHolder.mDesc.setText(datas.get(position).commentAuthor);
            itemViewHolder.mDate.setText(datas.get(position).commentDate);
        }

        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount()
    {

        return datas.size();
    }

    public class ItemViewHolder extends AbsRecyclerViewAdapter.ClickableViewHolder
    {

        public ImageView mImage;

        public TextView mDesc;

        public TextView mDate;

        public ItemViewHolder(View itemView)
        {

            super(itemView);
            mImage = $(R.id.item_fill_image);
            mDesc = $(R.id.item_desc);
            mDate = $(R.id.item_date);
        }
    }
}
