package com.art.huakai.artshow.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.art.huakai.artshow.R;
import com.art.huakai.artshow.adapter.KeywordSearchWorksAdapter;
import com.art.huakai.artshow.base.BaseActivity;
import com.art.huakai.artshow.constant.Constant;
import com.art.huakai.artshow.constant.JumpCode;
import com.art.huakai.artshow.entity.EnrollInfo;
import com.art.huakai.artshow.entity.NewsesBean;
import com.art.huakai.artshow.entity.TalentBean;
import com.art.huakai.artshow.entity.Theatre;
import com.art.huakai.artshow.entity.Work;
import com.art.huakai.artshow.utils.AnimUtils;
import com.art.huakai.artshow.utils.LogUtil;
import com.art.huakai.artshow.utils.RequestUtil;
import com.art.huakai.artshow.utils.ResponseCodeCheck;
import com.art.huakai.artshow.utils.SignUtil;
import com.art.huakai.artshow.utils.statusBar.ImmerseStatusBar;
import com.art.huakai.artshow.widget.SmartRecyclerview;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Created by lining on 2017/10/22.
 */
public class KeywordSearchRepertorysResultShowActivity extends BaseActivity implements View.OnClickListener , SmartRecyclerview.LoadingListener {


    private static final String REPERTORYS = "repertorys";

    @BindView(R.id.rcv)
    SmartRecyclerview recyclerView;
    @BindView(R.id.lly_back)
    LinearLayout llyBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_search_count)
    TextView tvSearchCount;
    @BindView(R.id.tv_search_type)
    TextView tvSearchType;
    @BindView(R.id.iv_loading)
    ImageView ivLoading;
    @BindView(R.id.iv_no_content)
    ImageView ivNoContent;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    private String keyword = "新闻";
    private String searchType = "";
    private int totalCount;
    private List<Work> workLists = new ArrayList<Work>();


    private KeywordSearchWorksAdapter keywordSearchWorksAdapter;
    private LinearLayoutManager linearlayoutManager;

    private int page = 1;

    private void setData() {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("搜索-" + keyword);
        if (searchType.equals(REPERTORYS)) {
            tvSearchCount.setVisibility(View.VISIBLE);
            tvSearchType.setText("项目");
            tvSearchCount.setText("共发现" + totalCount + "条相关数据");
            keywordSearchWorksAdapter = new KeywordSearchWorksAdapter(this, workLists);
            linearlayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(linearlayoutManager);
            recyclerView.setAdapter(keywordSearchWorksAdapter);
            keywordSearchWorksAdapter.setOnItemClickListener(new KeywordSearchWorksAdapter.OnItemClickListener() {
                @Override
                public void onItemClickListener(int position) {
                    Bundle bundle = new Bundle();
                    bundle.putString(PersonalDetailMessageActivity.PARAMS_ID, workLists.get(position).getId());
                    invokActivity(KeywordSearchRepertorysResultShowActivity.this, WorksDetailMessageActivity.class, bundle, JumpCode.FLAG_REQ_DETAIL_PERSONAL);
                }
            });
        }
    }


    @Override
    public void immerseStatusBar() {
        ImmerseStatusBar.myStatusBar(this);
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_search_result_show;
    }

    @Override
    public void initData() {


    }


    @Override
    public void initView() {

        llyBack.setOnClickListener(this);
        recyclerView.setLoadingListener(this);
    }


    @Override
    public void setView() {

    }

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if(ivLoading==null)return;
                setData();
                ivLoading.setVisibility(View.GONE);
                llContent.setVisibility(View.VISIBLE);
                ivNoContent.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lly_back:
                finish();
                break;
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        Intent intent = getIntent();
        searchType = intent.getStringExtra("searchType");
        keyword = intent.getStringExtra("keyword");
        Log.e(TAG, "onCreate: searchType==" + searchType + "----keyword==" + keyword);
        getKeywordSearchAllMessage();
        AnimUtils.rotate(ivLoading);
        ivNoContent.setVisibility(View.GONE);
        llContent.setVisibility(View.GONE);

    }


    private void getKeywordSearchAllMessage() {

        Map<String, String> params = new TreeMap<>();
        params.put("keyword", keyword);
        params.put("page", page + "");
        String url = "";
        if (searchType.equals(REPERTORYS)) {
//            项目
            url = Constant.URL_GET_WORKS;
        }
        String sign = SignUtil.getSign(params);
        params.put("sign", sign);
        Log.e(TAG, "getKeywordSearchAllMessage: url==" + url);
        Log.e(TAG, "getKeywordSearchAllMessage: params==" + params.toString());
        RequestUtil.request(url, params, 113, new RequestUtil.RequestListener() {
            @Override
            public void onSuccess(boolean isSuccess, String obj, int code, int id) {
                Log.e(TAG, "onSuccess: " );
                if(ivLoading==null)return;
                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(ivLoading==null)return;
                        if(page==1&&workLists.size() == 0){
                            ivLoading.setVisibility(View.GONE);
                            llContent.setVisibility(View.GONE);
                            ivNoContent.setVisibility(View.VISIBLE);
                        }
                    }
                },500);

                if (isSuccess) {
                    if (!TextUtils.isEmpty(obj)) {
                        Log.e(TAG, "onSuccess: obj==" + obj);
                        Gson gson = new Gson();
                        ArrayList<Work> tempTheatres = new ArrayList<Work>();
//                        theatres.clear();
                        tempTheatres = gson.fromJson(obj, new TypeToken<List<Work>>() {
                        }.getType());
                        if (tempTheatres != null && tempTheatres.size() > 0) {
                            if (workLists.size() == 0) {
                                if (workLists.addAll(tempTheatres)) {
                                    totalCount=id;
                                    uiHandler.removeCallbacksAndMessages(null);
                                    uiHandler.sendEmptyMessage(0);
                                }
                                page++;
                            } else {
                                if (page == 1) {
                                    recyclerView.refreshComplete();
                                    workLists.clear();
                                    totalCount=id;
                                    if (workLists.addAll(tempTheatres)) {
                                        uiHandler.sendEmptyMessage(0);
                                    }
                                } else {
                                    recyclerView.loadMoreComplete();
                                    workLists.addAll(tempTheatres);
                                    if (keywordSearchWorksAdapter != null) {
                                        keywordSearchWorksAdapter.add(tempTheatres);
                                    }
                                }
                                page++;
                            }
                        } else {
                            if (workLists.size() == 0) {
                                Log.e(TAG, "onSuccess: 首次加载数据失败");
                            } else {
                                if (page == 1) {
                                    Toast.makeText(KeywordSearchRepertorysResultShowActivity.this,"刷新数据失败",Toast.LENGTH_SHORT).show();
                                    recyclerView.refreshComplete();
                                } else {
                                    recyclerView.loadMoreComplete();
                                    Toast.makeText(KeywordSearchRepertorysResultShowActivity.this,"已无更多数据",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        Log.e(TAG, "onSuccess: theatres.size==" + workLists.size());
                    } else {
                        if (workLists.size() == 0) {
                            Log.e(TAG, "onSuccess: 首次加载数据失败");
                        } else {
                            if (page == 1) {
                                recyclerView.refreshComplete();
                                Toast.makeText(KeywordSearchRepertorysResultShowActivity.this,"刷新数据失败",Toast.LENGTH_SHORT).show();
                            } else {
                                recyclerView.loadMoreComplete();
                                Toast.makeText(KeywordSearchRepertorysResultShowActivity.this,"已无更多数据",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    if (workLists.size() == 0) {
                        Log.e(TAG, "onSuccess: 首次加载数据失败");
                        ivLoading.setVisibility(View.GONE);
                        llContent.setVisibility(View.GONE);
                        ivNoContent.setVisibility(View.VISIBLE);
                    } else {
                        if (page == 1) {
                            recyclerView.refreshComplete();
                            Toast.makeText(KeywordSearchRepertorysResultShowActivity.this,"刷新数据失败",Toast.LENGTH_SHORT).show();
                        } else {
                            recyclerView.loadMoreComplete();
                            Toast.makeText(KeywordSearchRepertorysResultShowActivity.this,"已无更多数据",Toast.LENGTH_SHORT).show();
                        }
                    }
                    ResponseCodeCheck.showErrorMsg(code);
                }
            }

            @Override
            public void onFailed(Call call, Exception e, int id) {
                LogUtil.e(TAG, e.getMessage() + "- id = " + id);
                if(ivLoading==null)return;
                if (workLists.size() == 0) {
                    Log.e(TAG, "onSuccess: 首次加载数据失败");
                    ivLoading.setVisibility(View.GONE);
                    llContent.setVisibility(View.GONE);
                    ivNoContent.setVisibility(View.VISIBLE);
                } else {
                    if (page == 1) {
                        recyclerView.refreshComplete();
                        Toast.makeText(KeywordSearchRepertorysResultShowActivity.this,"刷新数据失败",Toast.LENGTH_SHORT).show();
                    } else {
                        recyclerView.loadMoreComplete();
                        Toast.makeText(KeywordSearchRepertorysResultShowActivity.this,"已无更多数据",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    @Override
    public void onRefresh() {
        page = 1;
        getKeywordSearchAllMessage();
    }

    @Override
    public void onLoadMore() {
        getKeywordSearchAllMessage();
    }
}
