package com.art.huakai.artshow.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.art.huakai.artshow.R;
import com.art.huakai.artshow.entity.NewsesBean;
import com.art.huakai.artshow.utils.DateUtil;
import com.art.huakai.artshow.widget.ChinaShowImageView;

import java.util.List;

/**
 * Created by lining on 17-9-7.
 */
public class IndustryNewsOneAdapter extends RecyclerView.Adapter {

    private static final String TAG = "IndustryNewsAdapter";

    private List<NewsesBean> list;
    private Context mContext;
    private OnItemClickListener onItemClickListener;
    private static final int TYPE_ONE = 1;
    private static final int TYPE_TWO = 2;


    public IndustryNewsOneAdapter(Context context, List<NewsesBean> list) {
        this.list = list;
        this.mContext = context;

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateViewHolder: 111111");
        if (viewType == 2) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.type_one_one_item, parent, false);
            TypeOneViewHolder typeOneViewHolder = new TypeOneViewHolder(view);
            return typeOneViewHolder;
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.type_one_two_item, parent, false);
            TypeTwoViewHolder typeTwoViewHolder = new TypeTwoViewHolder(view);
            return typeTwoViewHolder;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e(TAG, "onBindViewHolder: position==" + position);
//        if (holder instanceof TypeOneViewHolder) {
//            TypeOneViewHolder typeOneViewHolder = (TypeOneViewHolder) holder;
//            if (list.size() == 1) {
//                typeOneViewHolder.ll_whole_two.setVisibility(View.GONE);
//            } else {
//                if (list.get(1) != null) {
//                    if (!TextUtils.isEmpty(list.get(1).getLogo())) {
////                        typeOneViewHolder.chinaShowImageViewTwo.setImageURI(Uri.parse(list.get(1).getLogo()));
//                        Log.d(TAG, "onBindViewHolder: list.get(1)=="+list.get(1).getLogo());
//                        typeOneViewHolder.chinaShowImageViewTwo.setSpecificSizeImageUrl(Uri.parse(list.get(1).getLogo()),mContext.getResources().getDimensionPixelSize(R.dimen.DIMEN_360PX)/4,mContext.getResources().getDimensionPixelSize(R.dimen.DIMEN_91PX)/2);
//                    }
//                    try {
//                        if(0!=list.get(1).getCreateTime()){
//                            typeOneViewHolder.tv_update_time_one.setText(DateUtil.transTime(list.get(1).getCreateTime()+"","yyyy年MM月dd"));
//                        }
//                    }catch (Exception e){
//
//                    }
//                    typeOneViewHolder.tv_name_two.setText(list.get(1).getTitle());
//                }
//            }
//            if (list.get(0) != null) {
//                if (!TextUtils.isEmpty(list.get(0).getLogo())) {
////                    typeOneViewHolder.chinaShowImageViewOne.setImageURI(Uri.parse(list.get(0).getLogo()));
//                    Log.d(TAG, "onBindViewHolder: list.get(0)=="+list.get(0).getLogo());
//                    typeOneViewHolder.chinaShowImageViewTwo.setSpecificSizeImageUrl(Uri.parse(list.get(0).getLogo()),mContext.getResources().getDimensionPixelSize(R.dimen.DIMEN_360PX)/4,mContext.getResources().getDimensionPixelSize(R.dimen.DIMEN_91PX)/2);
//
//                }
//                try {
//                    if(0!=list.get(0).getCreateTime()){
//                        typeOneViewHolder.tv_update_time_one.setText(DateUtil.transTime(list.get(0).getCreateTime()+"","yyyy年MM月dd"));
//                    }
//                }catch (Exception e){
//
//                }
//                typeOneViewHolder.tv_name_one.setText(list.get(0).getTitle());
//            }
//
//            typeOneViewHolder.ll_whole_one.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e(TAG, "onClick: 111111");
//                    if (onItemClickListener != null) {
//                        onItemClickListener.onItemClickListener(0);
//                    }
//                }
//            });
//
//            typeOneViewHolder.ll_whole_two.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e(TAG, "onClick: 2222");
//                    if (onItemClickListener != null) {
//                        onItemClickListener.onItemClickListener(1);
//                    }
//                }
//            });
//        } else

        if (holder instanceof TypeOneViewHolder) {
            TypeOneViewHolder typeTwoViewHolder = (TypeOneViewHolder) holder;
            if (list.get(position ) != null) {
                if (!TextUtils.isEmpty(list.get(position ).getLogo())) {
//                    typeTwoViewHolder.chinaShowImageView.setImageURI(Uri.parse(list.get(position + 1).getLogo()));
                    typeTwoViewHolder.chinaShowImageViewOne.setSpecificSizeImageUrl(list.get(position).getLogo(),mContext.getResources().getDimensionPixelSize(R.dimen.DIMEN_360PX)/4,mContext.getResources().getDimensionPixelSize(R.dimen.DIMEN_91PX)/2);

                }
                typeTwoViewHolder.tv_name_one.setText(list.get(position ).getTitle());
//                typeTwoViewHolder.tv_subtitle.setText(list.get(position ).getDescription());
            }
            try {
                if(0!=list.get(position ).getCreateTime()){
//                    typeTwoViewHolder.tv_update_time_one.setText(DateUtil.transTime(list.get(position ).getCreateTime()+"","yyyy年MM月dd"));
                    typeTwoViewHolder.tv_update_time_one.setText(list.get(position).getTimeDescpt());
                }
            }catch (Exception e){

            }
            typeTwoViewHolder.ll_whole_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        Log.e(TAG, "onClick: 11111");
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
        private ChinaShowImageView chinaShowImageViewOne;
        private TextView tv_name_one;
        private TextView tv_update_time_one;
        private LinearLayout ll_whole_one;
        private ChinaShowImageView chinaShowImageViewTwo;
        private TextView tv_name_two;
        private TextView tv_update_time_two;
        private LinearLayout ll_whole_two;

        public TypeOneViewHolder(View itemView) {
            super(itemView);
            chinaShowImageViewOne = (ChinaShowImageView) itemView.findViewById(R.id.sdv_one);
            tv_name_one = (TextView) itemView.findViewById(R.id.tv_name_one);
            tv_update_time_one = (TextView) itemView.findViewById(R.id.tv_update_time_one);
            ll_whole_one = (LinearLayout) itemView.findViewById(R.id.ll_whole_one);



        }
    }

    public class TypeTwoViewHolder extends RecyclerView.ViewHolder {
        private ChinaShowImageView chinaShowImageView;
        private TextView tv_name;
        private TextView tv_update_time;
        private TextView tv_subtitle;
        private LinearLayout ll_whole;

        public TypeTwoViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            chinaShowImageView = (ChinaShowImageView) itemView.findViewById(R.id.sdv);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_update_time = (TextView) itemView.findViewById(R.id.tv_update_time);
            tv_subtitle = (TextView) itemView.findViewById(R.id.tv_subtitle);
            ll_whole = (LinearLayout) itemView.findViewById(R.id.ll_whole);
        }
    }


    @Override
    public int getItemViewType(int position) {
//        if (position <= 0) {
//            return TYPE_ONE;
//        } else {
//            return TYPE_TWO;
//        }
        return TYPE_TWO;
    }


//    @Override
//    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//        RecyclerView.LayoutManager layoutManager=recyclerView.getLayoutManager();
//        if(layoutManager instanceof GridLayoutManager){
//            final GridLayoutManager gridLayoutManager= (GridLayoutManager) layoutManager;
//            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                @Override
//                public int getSpanSize(int position) {
//                    int type=getItemViewType(position);
//                    if(type==TYPE_ONE){
//                        return 1;
//                    }else{
//                        return 2;
//                    }
//                }
//            });
//        }
//    }

    public interface OnItemClickListener {
        void onItemClickListener(int position);
    }


}
