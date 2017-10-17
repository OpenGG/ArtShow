package com.art.huakai.artshow.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.art.huakai.artshow.R;
import com.art.huakai.artshow.adapter.holder.CooperateDesHolder;
import com.art.huakai.artshow.adapter.holder.CooperateHolder;
import com.art.huakai.artshow.adapter.holder.EmptyHolder;
import com.art.huakai.artshow.utils.DeviceUtils;

import java.util.List;

/**
 * Created by lidongliang on 2017/10/5.
 */

public class CooperateAdapter extends RecyclerView.Adapter {
    public static final int TYPE_EMPTY = 10;
    public static final int TYPE_TEXT_DES = 11;
    public static final int TYPE_NORMAL = 12;
    private List<String> mlist;

    public CooperateAdapter(List<String> list) {
        this.mlist = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (TYPE_EMPTY == viewType) {
            //设置空布局高，使空布局图片居中
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty, parent, false);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = DeviceUtils.getScreenHeight(parent.getContext()) -
                    parent.getResources().getDimensionPixelSize(R.dimen.main_tab_bottom) -
                    parent.getResources().getDimensionPixelSize(R.dimen.collaborate_head);
            view.setLayoutParams(layoutParams);
            return new EmptyHolder(view);
        } else if (TYPE_TEXT_DES == viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cooperate_des, parent, false);
            return new CooperateDesHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cooperate_normal, parent, false);
            return new CooperateHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_EMPTY:
                EmptyHolder emptyHolder = (EmptyHolder) holder;
                break;
            case TYPE_TEXT_DES:
                CooperateDesHolder collDesHolder = (CooperateDesHolder) holder;
                break;
            case TYPE_NORMAL:
                CooperateHolder collHolder = (CooperateHolder) holder;
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mlist.size() == 0 ? 1 : mlist.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mlist == null || mlist.size() == 0) {
            return TYPE_EMPTY;
        } else if (mlist.get(position) == null) {
            return TYPE_TEXT_DES;
        } else {
            return TYPE_NORMAL;
        }
    }
}