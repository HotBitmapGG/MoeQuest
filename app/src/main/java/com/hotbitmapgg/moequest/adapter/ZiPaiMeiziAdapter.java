package com.hotbitmapgg.moequest.adapter;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hotbitmapgg.moequest.R;
import com.hotbitmapgg.moequest.adapter.base.AbsRecyclerViewAdapter;
import com.hotbitmapgg.moequest.entity.meizitu.MeiziTu;

import java.util.ArrayList;
import java.util.List;


public class ZiPaiMeiziAdapter extends AbsRecyclerViewAdapter
{

    private List<MeiziTu> meiziList = new ArrayList<>();

    public ZiPaiMeiziAdapter(RecyclerView recyclerView, List<MeiziTu> meiziList)
    {

        super(recyclerView);
        this.meiziList = meiziList;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_zipai_meizi, parent, false));
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position)
    {

        if (holder instanceof ItemViewHolder)
        {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Glide.clear(itemViewHolder.mImageView);
            Glide.with(getContext())
                    .load(meiziList.get(position).getImageurl())
                    .crossFade(0)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String,GlideDrawable>()
                    {

                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource)
                        {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource)
                        {

                            itemViewHolder.mImageView.setImageDrawable(resource);
                            return false;
                        }
                    })
                    .into(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL);

            itemViewHolder.mImageView.setTag(R.string.app_name, meiziList.get(position).getImageurl());
            ViewCompat.setTransitionName(itemViewHolder.mImageView, meiziList.get(position).getImageurl());
        }

        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount()
    {

        return meiziList.size();
    }


    public class ItemViewHolder extends ClickableViewHolder
    {

        public ImageView mImageView;


        public ItemViewHolder(View itemView)
        {

            super(itemView);
            mImageView = $(R.id.item_home_img);
        }
    }
}
