package com.art.huakai.artshow.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.art.huakai.artshow.R;
import com.art.huakai.artshow.adapter.holder.EmptyHolder;
import com.art.huakai.artshow.adapter.holder.EnrollJoinHolder;
import com.art.huakai.artshow.entity.RepertorysBean;
import com.art.huakai.artshow.listener.OnItemClickListener;
import com.art.huakai.artshow.utils.DateUtil;
import com.art.huakai.artshow.utils.DeviceUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lidongliang on 2017/10/5.
 */

public class EnrollJoinAdapter extends RecyclerView.Adapter {
    public static final int TYPE_EMPTY = 10;
    public static final int TYPE_NORMAL = 12;
    private List<RepertorysBean> mlist;
    private OnItemClickListener onItemClickListener;
    private Date mEndDate;
    private SimpleDateFormat mSimpleDateFormat;

    public EnrollJoinAdapter(List<RepertorysBean> list) {
        this.mlist = list;
        mEndDate = new Date();
        mSimpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (TYPE_EMPTY == viewType) {
            //设置空布局高，使空布局图片居中
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty, parent, false);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = DeviceUtils.getScreenHeight(parent.getContext()) -
                    parent.getResources().getDimensionPixelSize(R.dimen.DIMEN_200PX);
            view.setLayoutParams(layoutParams);
            return new EmptyHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enroll_join, parent, false);
            return new EnrollJoinHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case TYPE_EMPTY:
                EmptyHolder emptyHolder = (EmptyHolder) holder;
                break;
            case TYPE_NORMAL:
                EnrollJoinHolder evrollJoinHolder = (EnrollJoinHolder) holder;
                RepertorysBean repertorysBean = mlist.get(position);
                evrollJoinHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClickListener(position);
                        }
                    }
                });
                if (position == 0) {
                    RecyclerView.LayoutParams layoutParams =
                            (RecyclerView.LayoutParams) evrollJoinHolder.rLyRootTheatre.getLayoutParams();
                    layoutParams.topMargin = evrollJoinHolder.rLyRootTheatre.
                            getResources().getDimensionPixelSize(R.dimen.DIMEN_11PX);
                    evrollJoinHolder.rLyRootTheatre.setLayoutParams(layoutParams);
                } else {
                    RecyclerView.LayoutParams layoutParams =
                            (RecyclerView.LayoutParams) evrollJoinHolder.rLyRootTheatre.getLayoutParams();
                    layoutParams.topMargin = evrollJoinHolder.rLyRootTheatre.
                            getResources().getDimensionPixelSize(R.dimen.DIMEN_7PX);
                    evrollJoinHolder.rLyRootTheatre.setLayoutParams(layoutParams);
                }
                evrollJoinHolder.sdvEnrollJoin.setImageURI(repertorysBean.getLogo());
                evrollJoinHolder.tvProjectTitle.setText(repertorysBean.getTitle());
                evrollJoinHolder.tvOrgName.setText(repertorysBean.getAgency());
                evrollJoinHolder.tvFirstShowTime.setText(DateUtil.transTime(String.valueOf(repertorysBean.getPremiereTime())));
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
        } else {
            return TYPE_NORMAL;
        }
    }
}
