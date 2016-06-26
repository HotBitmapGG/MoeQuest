package com.hotbitmapgg.moequest.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.adapter.base.AbsRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcc on 16/6/25 21:20
 * 100332338@qq.com
 */
public class TaoFemaleImageRecycleAdapter extends AbsRecyclerViewAdapter
{

    public List<String> images = new ArrayList<>();

    public TaoFemaleImageRecycleAdapter(RecyclerView recyclerView, List<String> images)
    {

        super(recyclerView);
        this.images = images;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_image_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position)
    {

        if (holder instanceof ItemViewHolder)
        {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            String url = images.get(position);
            Glide.clear(itemViewHolder.imageView);
            Glide.with(getContext())
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemViewHolder.imageView);
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount()
    {

        return images.size();
    }

    public class ItemViewHolder extends AbsRecyclerViewAdapter.ClickableViewHolder
    {

        public ImageView imageView;

        public ItemViewHolder(View itemView)
        {

            super(itemView);
            imageView = $(R.id.tao_item_image);
        }
    }
}
