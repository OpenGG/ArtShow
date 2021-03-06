package com.art.huakai.artshow.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.art.huakai.artshow.R;
import com.art.huakai.artshow.activity.WorksDetailMessageActivity;
import com.art.huakai.artshow.adapter.LookingWorksAdapter;
import com.art.huakai.artshow.adapter.ProjectFilterAdapter;
import com.art.huakai.artshow.adapter.SingleChooseAdapter;
import com.art.huakai.artshow.adapter.TheatreFilterAdapter;
import com.art.huakai.artshow.base.BaseFragment;
import com.art.huakai.artshow.constant.Constant;
import com.art.huakai.artshow.constant.JumpCode;
import com.art.huakai.artshow.decoration.GridLayoutItemDecoration;
import com.art.huakai.artshow.entity.RepertoryBean;
import com.art.huakai.artshow.entity.Work;
import com.art.huakai.artshow.utils.AnimUtils;
import com.art.huakai.artshow.utils.LogUtil;
import com.art.huakai.artshow.utils.RequestUtil;
import com.art.huakai.artshow.utils.ResponseCodeCheck;
import com.art.huakai.artshow.utils.SignUtil;
import com.art.huakai.artshow.widget.SmartRecyclerview;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class FoundProductionFragment extends BaseFragment implements View.OnClickListener, SmartRecyclerview.LoadingListener {
    @BindView(R.id.ll_complex_ranking)
    LinearLayout llComplexRanking;
    @BindView(R.id.ll_project_choose)
    LinearLayout llProjectChoose;
    @BindView(R.id.ll_filter)
    LinearLayout llFilter;
    @BindView(R.id.iv_loading)
    ImageView ivLoading;
    @BindView(R.id.iv_no_content)
    ImageView ivNoContent;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    private String TAG = "FoundProductionFragment";
    @BindView(R.id.iv_choose_price)
    ImageView ivComplexRanking;
    @BindView(R.id.iv_choose_number)
    ImageView ivChooseProject;
    @BindView(R.id.iv_real_choose)
    ImageView ivFilter;
    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    SmartRecyclerview recyclerView;
    @BindView(R.id.tv_whole_ranking)
    TextView tvComplexRanking;
    @BindView(R.id.tv_city_choose)
    TextView tvChooseProject;
    @BindView(R.id.tv_real_filter)
    TextView tvFilter;
    private ArrayList<Work> works = new ArrayList<Work>();
    private LookingWorksAdapter lookingWorksAdapter;
    private LinearLayoutManager linearlayoutManager;
    private PopupWindow popupWindow;
    private LayoutInflater mLayoutInflater;
    private int complexRankingRule = 0;


    private int theatreSize = -1;
    private int theatrefee = -1;
    private int showActorAccount = -1;
    private int repertorykind = -1;
    private int repertoryPosition = -1;
    private int page = 1;
    private String time;
    private ArrayList<String> months = new ArrayList<String>();
    private ArrayList<String> lists = new ArrayList<String>();
    private int monthPosition = -1;
    private ArrayList<RepertoryBean> repertorys = new ArrayList<RepertoryBean>();
    private boolean isLoading = false;

    public FoundProductionFragment() {
        Log.e(TAG, "FoundProductionFragment: ");
        // Required empty public constructor
    }

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                ivLoading.setVisibility(View.GONE);
                llContent.setVisibility(View.VISIBLE);
                ivNoContent.setVisibility(View.GONE);
                setData();

            }
        }
    };

    public static FoundProductionFragment newInstance() {
        FoundProductionFragment fragment = new FoundProductionFragment();
        return fragment;
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        Log.e(TAG, "initData: ");

    }

    private void setData() {

        linearlayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearlayoutManager);
        lookingWorksAdapter = new LookingWorksAdapter(getContext(), works);
        recyclerView.setAdapter(lookingWorksAdapter);
        lookingWorksAdapter.setOnItemClickListener(new LookingWorksAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(int position) {

                if ((works.get(position) != null) && !TextUtils.isEmpty(works.get(position).getId())) {
                    Bundle bundle = new Bundle();
                    bundle.putString(WorksDetailMessageActivity.PARAMS_ID, works.get(position).getId());
                    invokActivity(getContext(), WorksDetailMessageActivity.class, bundle, JumpCode.FLAG_REQ_DETAIL_PROJECT);
                }

//                Toast.makeText(getContext(), "itemclick", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public int getLayoutID() {
        Log.e(TAG, "getLayoutID: ");
        return R.layout.fragment_found_production;
    }

    @Override
    public void initView(View rootView) {
        Log.e(TAG, "initView: ");
        getList();
        getRepertoryClassify();
        AnimUtils.rotate(ivLoading);
        ivNoContent.setVisibility(View.GONE);
        llContent.setVisibility(View.GONE);
        _GetDate();
    }

    @Override
    public void setView() {
        llComplexRanking.setOnClickListener(this);
        llProjectChoose.setOnClickListener(this);
        llFilter.setOnClickListener(this);
        recyclerView.setLoadingListener(this);
//        recyclerView.refresh();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        Log.e(TAG, "onCreateView: ");
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        mLayoutInflater = inflater;
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "onClick: v.getId==" + v.getId());
        switch (v.getId()) {

            case R.id.ll_project_choose:

//                Toast.makeText(getContext(), "iv_choose_number", Toast.LENGTH_SHORT).show();
//                tvComplexRanking.setTextColor(0xff5a4b41);
//                ivComplexRanking.setImageResource(R.mipmap.arrow_down_icon);
                tvChooseProject.setTextColor(0xffe93c2c);
                ivChooseProject.setImageResource(R.mipmap.arrow_active);
//                tvFilter.setTextColor(0xff5a4b41);
//                ivFilter.setImageResource(R.mipmap.filter_default);
                showPopuwindow(2);

                break;

            case R.id.ll_complex_ranking:


                tvComplexRanking.setTextColor(0xffe93c2c);
                ivComplexRanking.setImageResource(R.mipmap.arrow_active);
//                tvChooseProject.setTextColor(0xff5a4b41);
//                ivChooseProject.setImageResource(R.mipmap.arrow_down_icon);
//                tvFilter.setTextColor(0xff5a4b41);
//                ivFilter.setImageResource(R.mipmap.filter_default);
                showPopuwindow(1);
//                Toast.makeText(getContext(), "iv_choose_price", Toast.LENGTH_SHORT).show();

                break;

            case R.id.ll_filter:

//                tvComplexRanking.setTextColor(0xff5a4b41);
//                ivComplexRanking.setImageResource(R.mipmap.arrow_down_icon);
//                tvChooseProject.setTextColor(0xff5a4b41);
//                ivChooseProject.setImageResource(R.mipmap.arrow_down_icon);
                tvFilter.setTextColor(0xffe93c2c);
                ivFilter.setImageResource(R.mipmap.filter_active);
                showPopuwindow(3);
//                Toast.makeText(getContext(), "iv_real_choose", Toast.LENGTH_SHORT).show();

                break;

        }
    }

    @Override
    public void onRefresh() {

        page = 1;
        getList();

    }

    @Override
    public void onLoadMore() {
        getList();
    }


    private void showPopuwindow(int type) {
        if (popupWindow == null) {
            popupWindow = new PopupWindow();
        }

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (complexRankingRule != 0) {
                    ivComplexRanking.setImageResource(R.mipmap.arrow_down_active);
                    tvComplexRanking.setTextColor(0xffe93c2c);
                } else {
                    ivComplexRanking.setImageResource(R.mipmap.arrow_down_icon);
                    tvComplexRanking.setTextColor(0xff5a4b41);
                }
                if (repertorykind != -1 && repertorykind != 0) {
                    ivChooseProject.setImageResource(R.mipmap.arrow_down_active);
                    tvChooseProject.setTextColor(0xffe93c2c);
                } else {
                    ivChooseProject.setImageResource(R.mipmap.arrow_down_icon);
                    tvChooseProject.setTextColor(0xff5a4b41);
                }

                if (theatreSize != -1 || showActorAccount != -1 || theatrefee != -1 || monthPosition != -1) {
                    tvFilter.setTextColor(0xffe93c2c);
                    ivFilter.setImageResource(R.mipmap.filter_active);
                } else {
                    tvFilter.setTextColor(0xff5a4b41);
                    ivFilter.setImageResource(R.mipmap.filter_default);
                }

            }
        });
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        if (type == 1) {
            View content = mLayoutInflater.inflate(R.layout.found_product_whole_ranking_popuwindow_item, null);
            if (complexRankingRule == 1) {
                content.findViewById(R.id.iv_one).setVisibility(View.VISIBLE);
                TextView view = (TextView) content.findViewById(R.id.tv_one);
                view.setTextColor(0xffe93c2c);

            } else if (complexRankingRule == 2) {
                content.findViewById(R.id.iv_two).setVisibility(View.VISIBLE);
                TextView view = (TextView) content.findViewById(R.id.tv_two);
                view.setTextColor(0xffe93c2c);

            } else if (complexRankingRule == 3) {
                content.findViewById(R.id.iv_three).setVisibility(View.VISIBLE);
                TextView view = (TextView) content.findViewById(R.id.tv_three);
                view.setTextColor(0xffe93c2c);

            } else if (complexRankingRule == 4) {
                content.findViewById(R.id.iv_four).setVisibility(View.VISIBLE);
                TextView view = (TextView) content.findViewById(R.id.tv_four);
                view.setTextColor(0xffe93c2c);

            }
            popupWindow.setContentView(content);
            content.findViewById(R.id.tv_one).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (complexRankingRule == 1) {
                        complexRankingRule = 0;
                        tvComplexRanking.setText("综合排序");
                    } else {
                        complexRankingRule = 1;
                        tvComplexRanking.setText("费用由高到低");
                    }


                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    page = 1;
                    getList();
                    works.clear();
                }
            });
            content.findViewById(R.id.tv_two).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (complexRankingRule == 2) {
                        complexRankingRule = 0;
                        tvComplexRanking.setText("综合排序");
                    } else {
                        complexRankingRule = 2;
                        tvComplexRanking.setText("费用由低到高");
                    }


                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    page = 1;
                    getList();
                    works.clear();
                }
            });
            content.findViewById(R.id.tv_three).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (complexRankingRule == 3) {
                        complexRankingRule = 0;
                        tvComplexRanking.setText("综合排序");
                    } else {
                        complexRankingRule = 3;
                        tvComplexRanking.setText("人数由高到低");
                    }


                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    page = 1;
                    getList();
                    works.clear();
                }
            });
            content.findViewById(R.id.tv_four).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (complexRankingRule == 4) {
                        complexRankingRule = 0;
                        tvComplexRanking.setText("综合排序");
                    } else {
                        complexRankingRule = 4;
                        tvComplexRanking.setText("人数由低到高");
                    }
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    page = 1;
                    getList();
                    works.clear();
                }
            });
        } else if (type == 3) {
            View content = mLayoutInflater.inflate(R.layout.found_works_real_filter_popuwindow_item, null);

            ArrayList<String> stringsone = new ArrayList<String>();
            stringsone.add("不限");
            stringsone.add("小剧场 （400人以内）");
            stringsone.add("中剧场 (400-800人)");
            stringsone.add("大剧场 （800-1500人）");
            stringsone.add("超大剧场 (1500人以上)");
            RecyclerView recyclerViewone = (RecyclerView) content.findViewById(R.id.rcv_one);
            final ProjectFilterAdapter singleChooseAdapterone = new ProjectFilterAdapter(getContext(), stringsone, theatreSize);
            singleChooseAdapterone.setOnItemClickListener(new ProjectFilterAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, String url) {
                    theatreSize = position;
                }
            });
            GridLayoutItemDecoration gridLayoutItemDecorationone = new GridLayoutItemDecoration(2, GridLayoutManager.VERTICAL, 40, 20);
            recyclerViewone.setItemAnimator(null);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            recyclerViewone.addItemDecoration(gridLayoutItemDecorationone);
            recyclerViewone.setLayoutManager(gridLayoutManager);
            recyclerViewone.setAdapter(singleChooseAdapterone);
            recyclerViewone.setNestedScrollingEnabled(false);

            RecyclerView recyclerViewtwo = (RecyclerView) content.findViewById(R.id.rcv_two);
            ArrayList<String> stringstwo = new ArrayList<String>();
            stringstwo.add("不限");
            stringstwo.add("3万以内");
            stringstwo.add("3万至5万");
            stringstwo.add("5万至8万");
            stringstwo.add("8万至10万");
            stringstwo.add("10万以上");
            final ProjectFilterAdapter singleChooseAdaptertwo = new ProjectFilterAdapter(getContext(), stringstwo, theatrefee);
            singleChooseAdaptertwo.setOnItemClickListener(new ProjectFilterAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, String url) {
                    theatrefee = position;
                }
            });
            GridLayoutItemDecoration gridLayoutItemDecorationtwo = new GridLayoutItemDecoration(3, GridLayoutManager.VERTICAL, 40, 20);
            recyclerViewtwo.setItemAnimator(null);
            GridLayoutManager gridLayoutManagertwo = new GridLayoutManager(getContext(), 3);
            gridLayoutManagertwo.setOrientation(GridLayoutManager.VERTICAL);
            recyclerViewtwo.addItemDecoration(gridLayoutItemDecorationtwo);
            recyclerViewtwo.setLayoutManager(gridLayoutManagertwo);
            recyclerViewtwo.setAdapter(singleChooseAdaptertwo);
            recyclerViewtwo.setNestedScrollingEnabled(false);


            RecyclerView recyclerViewthree = (RecyclerView) content.findViewById(R.id.rcv_three);
            ArrayList<String> stringsthree = new ArrayList<String>();
            stringsthree.add("不限");
            stringsthree.add("10人以内");
            stringsthree.add("10-20人");
            stringsthree.add("20-30人");
            stringsthree.add("30-50人");
            stringsthree.add("50人以上");
            final ProjectFilterAdapter singleChooseAdapterthree = new ProjectFilterAdapter(getContext(), stringsthree, showActorAccount);
            singleChooseAdapterthree.setOnItemClickListener(new ProjectFilterAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, String url) {
                    showActorAccount = position;
                }
            });
            GridLayoutItemDecoration gridLayoutItemDecorationthree = new GridLayoutItemDecoration(3, GridLayoutManager.VERTICAL, 40, 20);
            recyclerViewthree.setItemAnimator(null);
            GridLayoutManager gridLayoutManagerthree = new GridLayoutManager(getContext(), 3);
            gridLayoutManagerthree.setOrientation(GridLayoutManager.VERTICAL);
            recyclerViewthree.addItemDecoration(gridLayoutItemDecorationthree);
            recyclerViewthree.setLayoutManager(gridLayoutManagerthree);
            recyclerViewthree.setAdapter(singleChooseAdapterthree);
            recyclerViewthree.setNestedScrollingEnabled(false);


            RecyclerView recyclerViewfour = (RecyclerView) content.findViewById(R.id.rcv_four);
            final TheatreFilterAdapter singleChooseAdapterfour = new TheatreFilterAdapter(getContext(), months, monthPosition);
            singleChooseAdapterfour.setOnItemClickListener(new TheatreFilterAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, String url) {
                    monthPosition = position;


                }
            });
            GridLayoutItemDecoration gridLayoutItemDecorationfour = new GridLayoutItemDecoration(3, GridLayoutManager.VERTICAL, 40, 20);
            recyclerViewfour.setItemAnimator(null);
            GridLayoutManager gridLayoutManagerfour = new GridLayoutManager(getContext(), 3);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            recyclerViewfour.addItemDecoration(gridLayoutItemDecorationfour);
            recyclerViewfour.setLayoutManager(gridLayoutManagerfour);
            recyclerViewfour.setAdapter(singleChooseAdapterfour);
            recyclerViewfour.setNestedScrollingEnabled(false);


            content.findViewById(R.id.but_reset).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (singleChooseAdapterone != null) {
                        theatreSize = -1;
                        singleChooseAdapterone.resetData();
                    }
                    if (singleChooseAdaptertwo != null) {
                        theatrefee = -1;
                        singleChooseAdaptertwo.resetData();
                    }
                    if (singleChooseAdapterthree != null) {
                        showActorAccount = -1;
                        singleChooseAdapterthree.resetData();
                    }

                    if (singleChooseAdapterfour != null) {
                        monthPosition = -1;
                        singleChooseAdapterfour.resetData();
                    }

                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }


                    page = 1;
                    getList();
                    works.clear();

                }
            });

            content.findViewById(R.id.but_sure).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    page = 1;
                    getList();
                    works.clear();

                }
            });

            popupWindow.setContentView(content);

        } else if (type == 2) {

            View content = mLayoutInflater.inflate(R.layout.found_works_kind_filter_popuwindow_item, null);
            RecyclerView recyclerViewone = (RecyclerView) content.findViewById(R.id.rcv_one);
            final SingleChooseAdapter singleChooseAdapterone = new SingleChooseAdapter(getContext(), repertorys, repertoryPosition);
            singleChooseAdapterone.setOnItemClickListener(new SingleChooseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, String url) {
                    if (position == 0) {
                        repertorykind = 0;
                    } else {
                        repertorykind = repertorys.get(position).getId();
                    }
                    repertoryPosition = position;
                    tvChooseProject.setText(repertorys.get(position).getName());
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    page = 1;
                    getList();
                    works.clear();
                }
            });
            GridLayoutItemDecoration gridLayoutItemDecorationone = new GridLayoutItemDecoration(3, GridLayoutManager.VERTICAL, 40, 20);
            recyclerViewone.setItemAnimator(null);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            recyclerViewone.addItemDecoration(gridLayoutItemDecorationone);
            recyclerViewone.setLayoutManager(gridLayoutManager);
            recyclerViewone.setAdapter(singleChooseAdapterone);
            recyclerViewone.setNestedScrollingEnabled(false);

            popupWindow.setContentView(content);
        }
        if (popupWindow != null && !popupWindow.isShowing()) {
            if (Build.VERSION.SDK_INT < 24) {
                popupWindow.showAsDropDown(llComplexRanking, 0, getResources().getDimensionPixelSize(R.dimen.DIMEN_1PX));
            } else {
                int[] Location = new int[2];
                llComplexRanking.getLocationInWindow(Location);
                int x = Location[0];
                int y = Location[1];
                popupWindow.showAtLocation(llComplexRanking,
                        Gravity.NO_GRAVITY,
                        0,
                        y + llComplexRanking.getHeight() + getResources().getDimensionPixelSize(R.dimen.DIMEN_1PX));
            }
        }

    }

    private void getList() {

        if (isLoading) {
            return;
        }
        isLoading = true;
        Map<String, String> params = new TreeMap<>();
        Log.e(TAG, "getMessage: Constant.URL_GET_WORKS==" + Constant.URL_GET_WORKS);

        if (complexRankingRule == 1) {
            params.put("order", "expense");
            params.put("orderType", "desc");
        } else if (complexRankingRule == 2) {
            params.put("order", "expense");
            params.put("orderType", "asc");
        } else if (complexRankingRule == 3) {
            params.put("order", "seating");
            params.put("orderType", "desc");
        } else if (complexRankingRule == 4) {
            params.put("order", "seating");
            params.put("orderType", "asc");
        }

        if (theatreSize == 1) {
            params.put("seatingRequirMax", "400");
        } else if (theatreSize == 2) {
            params.put("seatingRequirMin", "400");
            params.put("seatingRequirMax", "800");
        } else if (theatreSize == 3) {
            params.put("seatingRequirMin", "800");
            params.put("seatingRequirMax", "1500");
        } else if (theatreSize == 4) {
            params.put("seatingRequirMin", "1500");
        }

        if (showActorAccount == 1) {
            params.put("peopleNumMax", "10");
        } else if (showActorAccount == 2) {
            params.put("peopleNumMin", "10");
            params.put("peopleNumMax", "20");
        } else if (showActorAccount == 3) {
            params.put("peopleNumMin", "20");
            params.put("peopleNumMax", "30");
        } else if (showActorAccount == 4) {
            params.put("peopleNumMin", "30");
            params.put("peopleNumMax", "50");
        } else if (showActorAccount == 5) {
            params.put("peopleNumMin", "50");
        }


        if (theatrefee == 1) {
            params.put("expenseMax", "30000");
        } else if (theatrefee == 2) {
            params.put("expenseMin", "30000");
            params.put("expenseMax", "50000");
        } else if (theatrefee == 3) {
            params.put("expenseMin", "50000");
            params.put("expenseMax", "80000");
        } else if (theatrefee == 4) {
            params.put("expenseMin", "80000");
            params.put("expenseMax", "100000");
        } else if (theatrefee == 5) {
            params.put("expenseMin", "100000");
        }

        params.put("page", page + "");
        if (repertorykind != 0 && repertorykind != -1) {
            params.put("classifyId", repertorykind + "");
        }

        if (!(monthPosition == 0 || monthPosition == -1)) {
            String month = months.get(monthPosition);
            params.put("enabledMonth", month.substring(0, 4) + "-" + lists.get(monthPosition));
        }

        String sign = SignUtil.getSign(params);
        params.put("sign", sign);
        Log.e(TAG, "getList: sign==" + sign);
        Log.e(TAG, "getList: params==" + params.toString());

        RequestUtil.request(true, Constant.URL_GET_WORKS, params, 107, new RequestUtil.RequestListener() {
            @Override
            public void onSuccess(boolean isSuccess, String obj, int code, int id) {
                LogUtil.i(TAG, obj);
                isLoading = false;
                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (page == 1 && works.size() == 0) {
                            ivLoading.setVisibility(View.GONE);
                            llContent.setVisibility(View.GONE);
                            ivNoContent.setVisibility(View.VISIBLE);
                        }
                    }
                }, 500);
                if (isSuccess) {
                    if (!TextUtils.isEmpty(obj)) {
                        Log.e(TAG, "onSuccess: obj==" + obj);
                        Gson gson = new Gson();
                        ArrayList<Work> tempWorks = new ArrayList<Work>();
                        tempWorks = gson.fromJson(obj, new TypeToken<List<Work>>() {
                        }.getType());
                        if (tempWorks != null && tempWorks.size() > 0) {
                            if (works.size() == 0) {
                                if (works.addAll(tempWorks)) {
                                    uiHandler.removeCallbacksAndMessages(null);
                                    uiHandler.sendEmptyMessage(0);
                                }
                                page++;
                            } else {
                                if (page == 1) {
                                    recyclerView.refreshComplete();
                                    works.clear();
                                    if (works.addAll(tempWorks)) {
                                        uiHandler.sendEmptyMessage(0);
                                    }
                                } else {
                                    recyclerView.loadMoreComplete();
                                    works.addAll(tempWorks);
                                    if (lookingWorksAdapter != null) {
//                                        lookingWorksAdapter.add(tempWorks);
                                        lookingWorksAdapter.notifyDataSetChanged();
                                    }
                                }
                                page++;
                            }
                        } else {
                            if (works.size() == 0) {
                                Toast.makeText(getContext(), "未查询到您筛选的数据", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onSuccess: 首次加载数据失败");
                            } else {

                                if (page == 1) {
                                    Toast.makeText(getContext(), "刷新数据失败", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "onSuccess: 刷新数据失败");
                                    recyclerView.refreshComplete();
                                } else {
                                    Toast.makeText(getContext(), "已无更多数据", Toast.LENGTH_SHORT).show();
                                    recyclerView.loadMoreComplete();
                                    Log.e(TAG, "onSuccess: 加载更多数据失败");
                                }
                            }
                        }
                        Log.e(TAG, "onSuccess: works.size==" + works.size());
                    } else {
                        if (works.size() == 0) {
                            Toast.makeText(getContext(), "未查询到您筛选的数据", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onSuccess: 首次加载数据失败");
                        } else {

                            if (page == 1) {
                                recyclerView.refreshComplete();
                                Toast.makeText(getContext(), "刷新数据失败", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onSuccess: 刷新数据失败");
                            } else {
                                recyclerView.loadMoreComplete();
                                Toast.makeText(getContext(), "已无更多数据", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onSuccess: 加载更多数据失败");
                            }
                        }
                    }
                } else {
                    if (works.size() == 0) {
                        Toast.makeText(getContext(), "未查询到您筛选的数据", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onSuccess: 首次加载数据失败");
                    } else {

                        if (page == 1) {
                            recyclerView.refreshComplete();
                            Toast.makeText(getContext(), "刷新数据失败", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onSuccess: 刷新数据失败");
                        } else {
                            Toast.makeText(getContext(), "已无更多数据", Toast.LENGTH_SHORT).show();
                            recyclerView.loadMoreComplete();
                            Log.e(TAG, "onSuccess: 加载更多数据失败");
                        }
                    }
                    ResponseCodeCheck.showErrorMsg(code);
                }
            }

            @Override
            public void onFailed(Call call, Exception e, int id) {
                LogUtil.e(TAG, e.getMessage() + "- id = " + id);
                isLoading = false;
                if (works.size() == 0) {
                    Toast.makeText(getContext(), "未查询到您筛选的数据", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onSuccess: 首次加载数据失败");
                    ivLoading.setVisibility(View.GONE);
                    llContent.setVisibility(View.GONE);
                    ivNoContent.setVisibility(View.VISIBLE);
                } else {

                    if (page == 1) {
                        Toast.makeText(getContext(), "刷新数据失败", Toast.LENGTH_SHORT).show();
                        recyclerView.refreshComplete();
                        Log.e(TAG, "onSuccess: 刷新数据失败");
                    } else {
                        recyclerView.loadMoreComplete();
                        Toast.makeText(getContext(), "已无更多数据", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onSuccess: 加载更多数据失败");
                    }
                }
            }
        });
    }

    private void getRepertoryClassify() {

        Map<String, String> params = new TreeMap<>();
        Log.e(TAG, "getMessage: Constant.URL_GET_CLASSFY_LIST==" + Constant.URL_GET_CLASSFY_LIST);
        params.put("type", "repertory");
        String sign = SignUtil.getSign(params);
        params.put("sign", sign);
        Log.e(TAG, "getList: sign==" + sign);
        Log.e(TAG, "getRepertoryClassify: " + params.toString());
        RequestUtil.request(true, Constant.URL_GET_CLASSFY_LIST, params, 106, new RequestUtil.RequestListener() {
            @Override
            public void onSuccess(boolean isSuccess, String obj, int code, int id) {
                if (isSuccess) {
                    if (!TextUtils.isEmpty(obj)) {
                        Log.e(TAG, "onSuccess: obj1111=" + obj);
                        Gson gson = new Gson();
                        if (repertorys.size() > 0) {
                            repertorys.clear();
                        }
                        repertorys = gson.fromJson(obj, new TypeToken<List<RepertoryBean>>() {
                        }.getType());
                        RepertoryBean repertoryBean = new RepertoryBean();
                        repertoryBean.setName("不限");
                        repertorys.add(0, repertoryBean);
                    }
                } else {
                    ResponseCodeCheck.showErrorMsg(code);
                }
            }

            @Override
            public void onFailed(Call call, Exception e, int id) {
                LogUtil.e(TAG, e.getMessage() + "- id = " + id);
            }
        });
    }

    private void _GetDate() {
//        TimeZone tz = TimeZone.getTimeZone("GMT");
//        Calendar c = Calendar.getInstance(tz);
//        Log.e(TAG, "_GetDate:year== " + c.get(Calendar.YEAR) + "month==" + c.get(Calendar.MONTH));
//        return "year = " + c.get(Calendar.YEAR) + "\n month = " + c.get(Calendar.MONTH) + "\n day = " + c.get(Calendar.DAY_OF_MONTH);

        months.add("不限");
        lists.add("0");
        Calendar c = Calendar.getInstance();//
        Log.e(TAG, "_GetDate: mYear==" + c.get(Calendar.YEAR) + "--mMonth==" + (c.get(Calendar.MONTH) + 1));
        int year = c.get(Calendar.YEAR);
        int month = (c.get(Calendar.MONTH) + 1);
        if (month < 10) {
            months.add(year + "年" + "0" + month + "月");
        } else {
            months.add(year + "年" + month + "月");
        }
        lists.add("" + month);
        for (int i = 1; i <= 11; i++) {
            if ((month + 1) > 12) {
                month = month + 1 - 12;
                year = year + 1;
            } else {
                month = month + 1;
                year = year;
            }
            if (month < 10) {
                months.add(year + "年" + "0" + month + "月");
            } else {
                months.add(year + "年" + month + "月");
            }
            lists.add("" + month);
        }
    }


}
