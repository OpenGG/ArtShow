package com.art.huakai.artshow.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.art.huakai.artshow.R;
import com.art.huakai.artshow.adapter.TalentDetailFragmentAdapter;
import com.art.huakai.artshow.base.BaseActivity;
import com.art.huakai.artshow.base.HeaderViewPagerFragment;
import com.art.huakai.artshow.constant.Constant;
import com.art.huakai.artshow.constant.JumpCode;
import com.art.huakai.artshow.dialog.PageLoadingDialog;
import com.art.huakai.artshow.dialog.ShareDialog;
import com.art.huakai.artshow.dialog.TakePhoneDialog;
import com.art.huakai.artshow.entity.LocalUserInfo;
import com.art.huakai.artshow.entity.TalentDetailBean;
import com.art.huakai.artshow.entity.TalentDetailInfo;
import com.art.huakai.artshow.eventbus.TalentNotifyEvent;
import com.art.huakai.artshow.fragment.PersonalDetailAwarsFragment;
import com.art.huakai.artshow.fragment.PersonalDetailworksFragment;
import com.art.huakai.artshow.fragment.StaggerFragment;
import com.art.huakai.artshow.listener.PageLoadingListener;
import com.art.huakai.artshow.okhttp.request.RequestCall;
import com.art.huakai.artshow.utils.LogUtil;
import com.art.huakai.artshow.utils.RequestUtil;
import com.art.huakai.artshow.utils.ResponseCodeCheck;
import com.art.huakai.artshow.utils.SignUtil;
import com.art.huakai.artshow.utils.statusBar.ImmerseStatusBar;
import com.art.huakai.artshow.widget.ChinaShowImageView;
import com.art.huakai.artshow.widget.headerviewpager.HeaderViewPager;
import com.flyco.tablayout.SlidingTabLayout;
import com.google.gson.Gson;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class PersonalDetailMessageActivity extends BaseActivity implements View.OnClickListener, PageLoadingListener {
    public static final int ACTION_DIALOG_DISMISS = 30;
    public static final String PARAMS_ID = "PARAMS_ID";
    public static final String PARAMS_ORG = "PARAMS_ORG";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right_img)
    ImageView ivRightImg;
    @BindView(R.id.fly_right_img)
    FrameLayout fLyRightImg;
    @BindView(R.id.btn_edit)
    Button btnEdit;
    @BindView(R.id.talents_pic)
    ChinaShowImageView talentsPic;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_major)
    TextView tvMajor;
    @BindView(R.id.iv_authentication)
    ImageView ivAuthentication;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.tv_height)
    TextView tvHeight;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.tv_university)
    TextView tvUniversity;
    @BindView(R.id.tv_organize)
    TextView tvOrganize;
    @BindView(R.id.tv_introduce)
    TextView tvIntroduce;
    @BindView(R.id.stl_dis_tab)
    SlidingTabLayout stlDisTab;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.scrollableLayout)
    HeaderViewPager scrollableLayout;
    @BindView(R.id.ll_make_telephone)
    LinearLayout llMakeTelephone;
    @BindView(R.id.tv_authentication)
    TextView tvAuthentication;


    private String[] mTabArray;
    private ArrayList<HeaderViewPagerFragment> mFragments;
    private Handler handler = new Handler();
    private String talentId;
    private TalentDetailBean talentDetailBean;
    private boolean mIsFromOrgan;
    private ShareDialog shareDialog;
    private String URL_TALENT_DETAL;
    private WbShareHandler mShareHandler;
    private MyHandler myHandler;

    private static class MyHandler extends Handler {

        private WeakReference<PersonalDetailMessageActivity> reference;

        public MyHandler(PersonalDetailMessageActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PersonalDetailMessageActivity activity = reference.get();
            switch (msg.what) {
                case 0:
                    if (activity != null) {
                        activity.setData();
                    }
                    break;
                case ACTION_DIALOG_DISMISS:
                    if (activity != null && activity.getPageLoading() != null && activity.getPageLoading().isShowing()) {
                        activity.getPageLoading().dismiss();
                    }
                    break;
            }
        }
    }

    private TakePhoneDialog takePhoneDialog;
    private RequestCall requestCall;

    public PageLoadingDialog getPageLoading() {
        return pageLoading;
    }

    private PageLoadingDialog pageLoading;

    private void setData() {

        if (talentDetailBean.getAuthentication() == 0) {
            tvAuthentication.setVisibility(View.INVISIBLE);
            ivAuthentication.setVisibility(View.INVISIBLE);
        } else {
            tvAuthentication.setVisibility(View.VISIBLE);
            ivAuthentication.setVisibility(View.VISIBLE);
        }
        if (talentDetailBean.getStatus() == 1) {
            fLyRightImg.setVisibility(View.VISIBLE);
        } else {
            fLyRightImg.setVisibility(View.GONE);
        }

        mTabArray = getResources().getStringArray(R.array.talent_detail_tab);
        mFragments = new ArrayList<>();
        PersonalDetailworksFragment personalDetailworksFragment = PersonalDetailworksFragment.newInstance(talentDetailBean);
        mFragments.add(personalDetailworksFragment);
        StaggerFragment staggerFragment = StaggerFragment.newInstance(talentDetailBean.getPictures());
        mFragments.add(staggerFragment);
        PersonalDetailAwarsFragment personalDetailAwarsFragment = PersonalDetailAwarsFragment.newInstance(talentDetailBean);
        mFragments.add(personalDetailAwarsFragment);
        TalentDetailFragmentAdapter disPagerAdapter = new TalentDetailFragmentAdapter(getSupportFragmentManager(), mFragments, mTabArray);

        viewpager.setAdapter(disPagerAdapter);
        viewpager.setOffscreenPageLimit(2);
        stlDisTab.setViewPager(viewpager);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scrollableLayout.setCurrentScrollableContainer(mFragments.get(position));
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewpager.setCurrentItem(0);

        if (!TextUtils.isEmpty(talentDetailBean.getLogo())) {
            talentsPic.setImageURI(Uri.parse(talentDetailBean.getLogo()));
        }
        tvName.setText(talentDetailBean.getName());
        String major = "";
        if (talentDetailBean.getClassifyNames() != null && talentDetailBean.getClassifyNames().size() > 0) {
            for (int i = 0; i < talentDetailBean.getClassifyNames().size(); i++) {
                if (i == 0) {
                    major = talentDetailBean.getClassifyNames().get(i);
                } else {
                    major = major + "/" + talentDetailBean.getClassifyNames().get(i);
                }
            }
        }
        if (TextUtils.isEmpty(major)) {
            tvMajor.setVisibility(View.GONE);
        } else {
            tvMajor.setVisibility(View.VISIBLE);
            tvMajor.setText(major);
        }
        tvAge.setText(talentDetailBean.getAge() + "");
        tvWeight.setText(talentDetailBean.getWeight());
        tvHeight.setText(talentDetailBean.getHeight());
        tvCity.setText(talentDetailBean.getRegionName());
        tvUniversity.setText(talentDetailBean.getSchool());
        tvOrganize.setText(talentDetailBean.getAgency());
        tvIntroduce.setText(talentDetailBean.getDescription());


    }


    @Override
    public void immerseStatusBar() {
        ImmerseStatusBar.myStatusBar(this);
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_personal_detail_message;
    }

    @Override
    public void initData() {
        myHandler = new MyHandler(this);
        pageLoading = new PageLoadingDialog(this);
        pageLoading.setPageLoadingListener(this);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            talentId = extras.getString(PARAMS_ID);
            mIsFromOrgan = extras.getBoolean(PARAMS_ORG, false);
        }
        if (mIsFromOrgan) {
            URL_TALENT_DETAL = Constant.URL_USER_TALENT_DETAIL;
        } else {
            URL_TALENT_DETAL = Constant.URL_TALENT_DETAIL;
        }
        getTalentDetail();
        mShareHandler = ShareDialog.regToWeibo(this);
    }

    @Override
    public void initView() {
        ivRightImg.setImageResource(R.mipmap.icon_share_gray);
        if (mIsFromOrgan) {
            btnEdit.setVisibility(View.VISIBLE);
            llMakeTelephone.setVisibility(View.GONE);
        } else {
            btnEdit.setVisibility(View.GONE);
            llMakeTelephone.setVisibility(View.VISIBLE);
        }

//        AnimUtils.rotate(ivLoading);
//        ivNoContent.setVisibility(View.GONE);
//        rlContent.setVisibility(View.GONE);
    }

    @Override
    public void setView() {


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lly_back:
                finish();
                break;
        }
    }

    private void getTalentDetail() {
        Map<String, String> params = new TreeMap<>();
        params.put("id", talentId);
        if (mIsFromOrgan) {
            params.put("userId", LocalUserInfo.getInstance().getId());
            params.put("accessToken", LocalUserInfo.getInstance().getAccessToken());
        }
        String sign = SignUtil.getSign(params);
        params.put("sign", sign);
        Log.i(TAG, "getRepertoryClassify: " + params.toString());
        if (!pageLoading.isShowing() && !this.isFinishing()) {
            pageLoading.show();
        }
        requestCall = RequestUtil.request(true, URL_TALENT_DETAL, params, 120, new RequestUtil.RequestListener() {
            @Override
            public void onSuccess(boolean isSuccess, String obj, int code, int id) {
                if (isSuccess) {
                    myHandler.sendEmptyMessageDelayed(ACTION_DIALOG_DISMISS, 100);
                    if (!TextUtils.isEmpty(obj)) {
                        Log.i(TAG, "objj=" + obj);
                        Gson gson = new Gson();
                        try {
                            talentDetailBean = gson.fromJson(obj, TalentDetailBean.class);
                            if (talentDetailBean != null) {
                                myHandler.sendEmptyMessage(0);
                                return;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "onSuccess: 数据解析异常");
                        }
                    } else {
                        Log.e(TAG, "onSuccess: 数据为空");
                    }
                } else {
                    ResponseCodeCheck.showErrorMsg(code);
                    pageLoading.showErrorLoading();
                }

            }

            @Override
            public void onFailed(Call call, Exception e, int id) {
                LogUtil.e(TAG, e.getMessage() + "- id = " + id);
                pageLoading.showErrorLoading();
            }
        });
    }

    @OnClick(R.id.btn_edit)
    public void jump2ResumeEditActivity() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ProjectEditActivity.PARAMS_NEW, false);
        invokActivity(this, ResumeEditActivity.class, bundle, JumpCode.FLAG_REQ_TALENT_EDIT);
    }

    @OnClick(R.id.fly_right_img)
    public void shareProject() {
        if (shareDialog == null) {
            String title = talentDetailBean == null ||
                    TextUtils.isEmpty(talentDetailBean.getName()) ?
                    getString(R.string.app_name) :
                    talentDetailBean.getName();
            String shareLink = talentDetailBean == null ||
                    TextUtils.isEmpty(talentDetailBean.getShareLink()) ?
                    getString(R.string.share_main_url) :
                    talentDetailBean.getShareLink();
            shareDialog = ShareDialog.newInstence(title, shareLink);
            shareDialog.setShareHandler(mShareHandler);
        }
        shareDialog.show(getSupportFragmentManager(), "SHARE.DIALOG");
    }

    @OnClick(R.id.lly_back)
    public void back() {
        this.finish();
    }

    /**
     * 打电话
     */
    @OnClick(R.id.ll_make_telephone)
    public void callPhone() {
        if (TextUtils.isEmpty(talentDetailBean.getLinkTel())) {
            showToast(getString(R.string.tip_linkdata_error));
            return;
        }
        if (takePhoneDialog == null) {
            takePhoneDialog = TakePhoneDialog.newInstence(talentDetailBean.getLinkman(), talentDetailBean.getLinkTel());
        }
        takePhoneDialog.show(getSupportFragmentManager(), "TAKEPHONE.DIALOG");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (shareDialog != null) {
            mShareHandler.doResultIntent(intent, shareDialog);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (requestCall != null) {
            requestCall.cancel();
            requestCall = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventLogin(TalentNotifyEvent event) {
        if (event == null) {
            return;
        }
        if (this.isFinishing()) {
            return;
        }
        try {
            TalentDetailInfo t = TalentDetailInfo.getInstance();
            switch (event.getActionCode()) {
                case TalentNotifyEvent.NOTIFY_AVATAR:
                    talentsPic.setImageURI(t.getLogo());
                    break;
                case TalentNotifyEvent.NOTIFY_BASE_INFO:
                    tvName.setText(t.getName());
                    List<String> classifyNames = t.getClassifyNames();
                    String s = "";
                    for (int i = 0; i < classifyNames.size(); i++) {
                        if (i != classifyNames.size() - 1) {
                            s += classifyNames.get(i) + "/";
                        } else {
                            s += classifyNames.get(i);
                        }
                    }
                    if (TextUtils.isEmpty(s)) {
                        tvMajor.setVisibility(View.GONE);
                    } else {
                        tvMajor.setVisibility(View.VISIBLE);
                        tvMajor.setText(s);
                    }
                    tvAge.setText(String.valueOf(t.getAge()));
                    tvWeight.setText(String.valueOf(t.getWeight()));
                    tvHeight.setText(String.valueOf(t.getHeight()));
                    tvCity.setText(t.getRegionName());
                    tvUniversity.setText(t.getSchool());
                    tvOrganize.setText(t.getAgency());
                    break;
                case TalentNotifyEvent.NOTIFY_INTRODUCE:
                    tvIntroduce.setText(t.getDescription());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_QZONE_SHARE) {
            if (resultCode == Constants.ACTIVITY_OK && shareDialog != null) {
                Tencent.handleResultData(data, shareDialog);
            }
        }
    }

    @Override
    public void onClose() {
        finish();
    }

    @Override
    public void onRetry() {
        getTalentDetail();
    }
}
