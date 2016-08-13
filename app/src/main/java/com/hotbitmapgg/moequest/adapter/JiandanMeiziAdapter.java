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
import com.hotbitmapgg.moequest.entity.jiandan.JianDanMeizi;
import com.hotbitmapgg.moequest.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

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
            JianDanMeizi.JianDanMeiziData jianDanMeiziData = datas.get(position);
            String picUrl;
            if (jianDanMeiziData.pics[0].endsWith(".gif"))
            {
                picUrl = jianDanMeiziData.pics[0]
                        .replace("mw600", "small")
                        .replace("mw690", "small")
                        .replace("mw1200", "small")
                        .replace("mw1024", "small")
                        .replace("large", "small");
                LogUtil.all(picUrl);
            } else
            {
                picUrl = jianDanMeiziData.pics[0];
            }

            Glide.clear(itemViewHolder.mImage);
            Glide.with(getContext())
                    .load(picUrl)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.override(200, 250)
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
