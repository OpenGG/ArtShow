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
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.art.huakai.artshow.R;
import com.art.huakai.artshow.adapter.TheatreDetailFragmentAdapter;
import com.art.huakai.artshow.base.BaseActivity;
import com.art.huakai.artshow.base.HeaderViewPagerFragment;
import com.art.huakai.artshow.constant.Constant;
import com.art.huakai.artshow.constant.JumpCode;
import com.art.huakai.artshow.dialog.PageLoadingDialog;
import com.art.huakai.artshow.dialog.ShareDialog;
import com.art.huakai.artshow.dialog.TakePhoneDialog;
import com.art.huakai.artshow.entity.DisabledDatesBean;
import com.art.huakai.artshow.entity.LocalUserInfo;
import com.art.huakai.artshow.entity.TheatreDetailBean;
import com.art.huakai.artshow.entity.TheatreDetailInfo;
import com.art.huakai.artshow.eventbus.TheatreNotifyEvent;
import com.art.huakai.artshow.fragment.StaggerFragment;
import com.art.huakai.artshow.fragment.TheatreDetailDesFragment;
import com.art.huakai.artshow.fragment.TheatreDetailParamsFragment;
import com.art.huakai.artshow.listener.PageLoadingListener;
import com.art.huakai.artshow.okhttp.request.RequestCall;
import com.art.huakai.artshow.utils.DateUtil;
import com.art.huakai.artshow.utils.LogUtil;
import com.art.huakai.artshow.utils.RequestUtil;
import com.art.huakai.artshow.utils.ResponseCodeCheck;
import com.art.huakai.artshow.utils.SignUtil;
import com.art.huakai.artshow.utils.statusBar.ImmerseStatusBar;
import com.art.huakai.artshow.widget.ChinaShowImageView;
import com.art.huakai.artshow.widget.calendar.CalendarSelectorActivity;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class TheatreDetailMessageActivity extends BaseActivity implements View.OnClickListener, AMapLocationListener, PageLoadingListener {
    public static final int ACTION_DIALOG_DISMISS = 20;
    public static final String PARAMS_ID = "PARAMS_ID";
    public static final String PARAMS_ORG = "PARAMS_ORG";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.sdv)
    ChinaShowImageView sdv;
    @BindView(R.id.tv_theatre_name)
    TextView tvTheatreName;
    @BindView(R.id.tv_theatre_kind)
    TextView tvTheatreKind;

    @BindView(R.id.tv_fee)
    TextView tvFee;
    @BindView(R.id.tv_seat_count)
    TextView tvSeatCount;
    @BindView(R.id.ll_check_ticket_area)
    LinearLayout llCheckTicketArea;
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
    @BindView(R.id.iv_right_img)
    ImageView ivRightImg;
    @BindView(R.id.btn_edit)
    Button btnEdit;
    @BindView(R.id.lly_back)
    LinearLayout llyBack;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.ll_check_map_area)
    LinearLayout llCheckMapArea;
    @BindView(R.id.ll_check_schedule_area)
    LinearLayout llCheckScheduleArea;
    @BindView(R.id.tv_unit)
    TextView tvUnit;
    @BindView(R.id.fly_right_img)
    FrameLayout fLyRightImg;

    private String[] mTabArray;
    private ArrayList<HeaderViewPagerFragment> mFragments;
    private Handler handler = new Handler();
    private String theatreId;
    private TheatreDetailBean theatreDetailBean;
    private ShareDialog shareDialog;
    private boolean mIsFromOrgan;
    private String URL_THEATRE_DETAL;
    private TakePhoneDialog takePhoneDialog;
    private WbShareHandler mShareHandler;

    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private MyHandler myHandler;

    private static class MyHandler extends Handler {

        private WeakReference<TheatreDetailMessageActivity> reference;

        public MyHandler(TheatreDetailMessageActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TheatreDetailMessageActivity activity = reference.get();
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

    private RequestCall requestCall;

    public PageLoadingDialog getPageLoading() {
        return pageLoading;
    }

    private PageLoadingDialog pageLoading;

    private void initPosition() {
        mlocationClient = new AMapLocationClient(this);
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位监听
        mlocationClient.setLocationListener(this);
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        mLocationOption.setOnceLocation(true);
//设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();
    }

    private void setData() {
        if (TextUtils.isEmpty(theatreDetailBean.getPriceDiagram())) {
            llCheckTicketArea.setVisibility(View.INVISIBLE);
        } else {
            llCheckTicketArea.setVisibility(View.VISIBLE);
        }
        if (theatreDetailBean.getStatus() == 1) {
            fLyRightImg.setVisibility(View.VISIBLE);
        } else {
            fLyRightImg.setVisibility(View.GONE);
        }
        mTabArray = getResources().getStringArray(R.array.theatre_detail_tab);
        mFragments = new ArrayList<HeaderViewPagerFragment>();
        TheatreDetailDesFragment theatreDetailTheatreFragment = TheatreDetailDesFragment.newInstance(theatreDetailBean);
        mFragments.add(theatreDetailTheatreFragment);
        StaggerFragment staggerFragment = StaggerFragment.newInstance(theatreDetailBean.getPictures());
        mFragments.add(staggerFragment);

        TheatreDetailParamsFragment theatreDetailParamsFragment = TheatreDetailParamsFragment.newInstance(theatreDetailBean);
        mFragments.add(theatreDetailParamsFragment);

        TheatreDetailFragmentAdapter disPagerAdapter = new TheatreDetailFragmentAdapter(getSupportFragmentManager(), mFragments, mTabArray);

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
        if (!TextUtils.isEmpty(theatreDetailBean.getLogo())) {
            sdv.setImageURI(Uri.parse(theatreDetailBean.getLogo()));
        }
        tvTheatreName.setText(theatreDetailBean.getName());

        tvFee.setText(theatreDetailBean.getExpenseDescpt());
        tvUnit.setText(theatreDetailBean.getExpenseUnit());
        tvSeatCount.setText(theatreDetailBean.getSeatingDescpt());
//        tvWeight.setText(talentDetailBean.getWeight());
//        tvHeight.setText(talentDetailBean.getHeight());
//        tvUniversity.setText(talentDetailBean.getSchool());
//        tvOrganize.setText(talentDetailBean.getAgency());
        tvIntroduce.setText(theatreDetailBean.getDescription());
        tvLocation.setText(theatreDetailBean.getAddress());
        if (TextUtils.isEmpty(theatreDetailBean.getRoomName())) {
            tvTheatreKind.setVisibility(View.GONE);
        } else {
            tvTheatreKind.setVisibility(View.VISIBLE);
            tvTheatreKind.setText(theatreDetailBean.getRoomName());
        }


    }

    @Override
    public void immerseStatusBar() {
        ImmerseStatusBar.myStatusBar(this);
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_theatre_detail_message;
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
            theatreId = extras.getString(PARAMS_ID);
            mIsFromOrgan = extras.getBoolean(PARAMS_ORG, false);
        }
        if (mIsFromOrgan) {
            URL_THEATRE_DETAL = Constant.URL_USER_THEATER_DETAIL;
        } else {
            URL_THEATRE_DETAL = Constant.URL_THEATER_DETAIL;
        }
        getTheatreDetail();
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
        llCheckMapArea.setOnClickListener(this);
        llCheckScheduleArea.setOnClickListener(this);
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
            case R.id.ll_check_map_area:
                Intent intent = new Intent();
                String coordinate = theatreDetailBean.getCoordinate();
                String[] lists = {};
                if (!TextUtils.isEmpty(coordinate)) {
                    lists = coordinate.split(",");
                }
                intent.putExtra("toLatitude", "");
                intent.putExtra("toLongitude", "");
                if (lists != null && lists.length == 2) {
                    intent.putExtra("toLatitude", lists[1]);
                    intent.putExtra("toLongitude", lists[0]);
                }
                intent.putExtra("theatreName", theatreDetailBean.getName());
                intent.putExtra("theatreLocation", theatreDetailBean.getAddress());
                intent.setClass(TheatreDetailMessageActivity.this, NavigationActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_check_schedule_area:
                ArrayList<String> listAdd = new ArrayList<>();
                if (theatreDetailBean != null) {
                    List<DisabledDatesBean> disabledDates = theatreDetailBean.getDisabledDates();
                    for (DisabledDatesBean d : disabledDates) {
                        listAdd.add(DateUtil.transTime(String.valueOf(d.getDate()), "yyyy-MM-dd"));
                    }
                } else {
                    return;
                }
                Intent i = new Intent(TheatreDetailMessageActivity.this, CalendarSelectorActivity.class);
                i.putExtra(CalendarSelectorActivity.DAYS_OF_SELECT, 1000);
                i.putExtra(CalendarSelectorActivity.ORDER_DAY, "");
                i.putExtra(CalendarSelectorActivity.SELECT_ENALBE, false);
                i.putStringArrayListExtra(CalendarSelectorActivity.SELECT_LIST, listAdd);
                startActivity(i);
                break;

        }


    }

    private void getTheatreDetail() {

        Map<String, String> params = new TreeMap<>();
        params.put("id", theatreId);
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
        requestCall = RequestUtil.request(true, URL_THEATRE_DETAL, params, 130, new RequestUtil.RequestListener() {
            @Override
            public void onSuccess(boolean isSuccess, String obj, int code, int id) {
                if (isSuccess) {
                    myHandler.sendEmptyMessageDelayed(ACTION_DIALOG_DISMISS, 100);
                    if (!TextUtils.isEmpty(obj)) {
                        Log.i(TAG, "onSuccess: obj=" + obj);
                        Gson gson = new Gson();
                        try {
                            theatreDetailBean = gson.fromJson(obj, TheatreDetailBean.class);
                            if (theatreDetailBean != null) {
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

    @OnClick(R.id.fly_right_img)
    public void shareProject() {
        if (shareDialog == null) {
            String title = theatreDetailBean == null || TextUtils.isEmpty(theatreDetailBean.getName()) ? getString(R.string.app_name) : theatreDetailBean.getName();
            String shareLink = theatreDetailBean == null || TextUtils.isEmpty(theatreDetailBean.getShareLink()) ? getString(R.string.share_main_url) : theatreDetailBean.getShareLink();
            shareDialog = ShareDialog.newInstence(title, shareLink);
            shareDialog.setShareHandler(mShareHandler);
        }
        shareDialog.show(getSupportFragmentManager(), "SHARE.DIALOG");
    }


    @OnClick(R.id.btn_edit)
    public void jump2TheatreActivity() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(TheatreEditActivity.PARAMS_NEW_CREATE, false);
        invokActivity(TheatreDetailMessageActivity.this, TheatreEditActivity.class, bundle, JumpCode.FLAG_REQ_THEATRE_EDIT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    /**
     * 返回
     */
    @OnClick(R.id.lly_back)
    public void back() {
        finish();
    }

    /**
     * 打电话
     */
    @OnClick(R.id.ll_make_telephone)
    public void callPhone() {
        if (TextUtils.isEmpty(theatreDetailBean.getLinkTel())) {
            showToast(getString(R.string.tip_linkdata_error));
            return;
        }
        if (takePhoneDialog == null) {
            takePhoneDialog = TakePhoneDialog.newInstence(theatreDetailBean.getLinkman(), theatreDetailBean.getLinkTel());
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
    public void onLocationChanged(AMapLocation amapLocation) {

        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息

                amapLocation.getDistrict();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                Toast.makeText(TheatreDetailMessageActivity.this, amapLocation.toString(), Toast.LENGTH_SHORT).show();
                mlocationClient.stopLocation();
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());

                Toast.makeText(TheatreDetailMessageActivity.this, amapLocation.getErrorInfo(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @OnClick(R.id.rly_ticket_area)
    public void showTicketArea() {
        if (TextUtils.isEmpty(theatreDetailBean.getPriceDiagram())) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(TicketAreaActivity.PARAMS_URL, theatreDetailBean.getPriceDiagram());
        invokActivity(this, TicketAreaActivity.class, bundle, JumpCode.FLAG_REQ_CHECK_TICKET_AREA);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
        if (requestCall != null) {
            requestCall.cancel();
            requestCall = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventLogin(TheatreNotifyEvent event) {
        if (event == null) {
            return;
        }
        if (this.isFinishing()) {
            return;
        }
        try {
            TheatreDetailInfo t = TheatreDetailInfo.getInstance();
            switch (event.getActionCode()) {
                case TheatreNotifyEvent.NOTIFY_THEATRE_AVATAR:
                    sdv.setImageURI(t.getLogo());
                    break;
                case TheatreNotifyEvent.NOTIFY_THEATRE_BASE_INFO:
                    tvTheatreName.setText(t.getName());
                    tvFee.setText(t.getExpense() + "");
                    tvSeatCount.setText(t.getSeating() + "");
                    tvLocation.setText(t.getAddress());
                    tvIntroduce.setText(t.getDescription());
                    if (TextUtils.isEmpty(t.getRoomName())) {
                        tvTheatreKind.setVisibility(View.GONE);
                    } else {
                        tvTheatreKind.setVisibility(View.VISIBLE);
                        tvTheatreKind.setText(t.getRoomName());
                    }
                    break;
                case TheatreNotifyEvent.NOTIFY_THEATRE_INTRODUCE:
                    tvIntroduce.setText(t.getDescription());
                    break;
                case TheatreNotifyEvent.NOTIFY_THEATRE_DISABLE_DATE:
                    if (theatreDetailBean != null) {
                        theatreDetailBean.setDisabledDates(t.getDisabledDates());
                    }
                    break;
                case TheatreNotifyEvent.NOTIFY_THEATRE_TICKET:
                    if (theatreDetailBean != null) {
                        theatreDetailBean.setPriceDiagram(t.getPriceDiagram());
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        getTheatreDetail();
    }
}
