package com.art.huakai.artshow.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.art.huakai.artshow.R;
import com.art.huakai.artshow.entity.TalentBean;
import com.art.huakai.artshow.widget.ChinaShowImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lining on 17-10-7.
 */
public class LookingProfessionalAdapter extends RecyclerView.Adapter {

    private static final String TAG = "LookingProfessionalAdap";

    private List<TalentBean> list;
    private Context mContext;
    private OnItemClickListener onItemClickListener;


    public LookingProfessionalAdapter(Context context, List<TalentBean> list) {
        this.list = list;
        this.mContext = context;

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.looking_professional_item, parent, false);
        TypeOneViewHolder typeOneViewHolder = new TypeOneViewHolder(view);
        return typeOneViewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof TypeOneViewHolder) {
            TypeOneViewHolder typeOneViewHolder = (TypeOneViewHolder) holder;
            if (position == 0) {
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) typeOneViewHolder.ll_whole.getLayoutParams();
                layoutParams.topMargin = typeOneViewHolder.itemView.getResources().getDimensionPixelSize(R.dimen.DIMEN_15PX);
                typeOneViewHolder.ll_whole.setLayoutParams(layoutParams);
            } else {
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) typeOneViewHolder.ll_whole.getLayoutParams();
                layoutParams.topMargin = 0;
                typeOneViewHolder.ll_whole.setLayoutParams(layoutParams);
            }

            if (list.get(position) != null) {
                TalentBean talent = list.get(position);
                if (!TextUtils.isEmpty(talent.getLogo())) {
                    typeOneViewHolder.chinaShowImageView.setSpecificSizeImageUrl(list.get(position).getLogo(), mContext.getResources().getDimensionPixelSize(R.dimen.DIMEN_125PX) / 2, mContext.getResources().getDimensionPixelSize(R.dimen.DIMEN_125PX) / 2);
                }
                String str = "";
                if (talent.getClassifyNames() != null && talent.getClassifyNames().size() > 0) {
                    for (int i = 0; i < talent.getClassifyNames().size(); i++) {
                        if (i > 0) {
                            str = str + "/" + talent.getClassifyNames().get(i);
                        } else {
                            str = str + talent.getClassifyNames().get(i);
                        }
                    }
                }
                typeOneViewHolder.tv_professional_name.setText(talent.getName());
                typeOneViewHolder.tv_majors.setText(str);
                typeOneViewHolder.tv_subside_organ.setText(talent.getAgency());
                if (talent.getAuthentication() == 0) {
                    typeOneViewHolder.tv_auth.setVisibility(View.INVISIBLE);
                    typeOneViewHolder.iv_auth.setVisibility(View.INVISIBLE);
                } else {
                    typeOneViewHolder.tv_auth.setVisibility(View.VISIBLE);
                    typeOneViewHolder.iv_auth.setVisibility(View.VISIBLE);
                }
            }

            typeOneViewHolder.ll_whole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        Log.e(TAG, "onClick: 2222");
                        onItemClickListener.onItemClickListener(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (list != null && list.size() > 0) {
            return list.size();
        }
        return 0;
    }


    public class TypeOneViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_professional_name;
        private ChinaShowImageView chinaShowImageView;
        private LinearLayout ll_whole;
        private TextView tv_majors;
        private TextView tv_subside_organ;
        private TextView tv_auth;
        private ImageView iv_auth;

        public TypeOneViewHolder(View itemView) {
            super(itemView);
            tv_professional_name = (TextView) itemView.findViewById(R.id.tv_professional_name);
            chinaShowImageView = (ChinaShowImageView) itemView.findViewById(R.id.sdv);
            ll_whole = (LinearLayout) itemView.findViewById(R.id.ll_whole);
            tv_majors = (TextView) itemView.findViewById(R.id.tv_majors);
            tv_subside_organ = (TextView) itemView.findViewById(R.id.tv_subside_organ);
            iv_auth = (ImageView) itemView.findViewById(R.id.iv_auth);
            tv_auth = (TextView) itemView.findViewById(R.id.tv_auth);

        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position);
    }

    public void add(ArrayList<TalentBean> theatres) {
        int lastIndex = this.list.size();
        if (this.list.addAll(theatres)) {
            notifyItemRangeInserted(lastIndex, list.size());
        }
    }

    public void notifyDataSetChange(ArrayList<TalentBean> theatres) {
        list.clear();
        if (this.list.addAll(theatres)) {
            notifyDataSetChanged();
        }
    }


}
