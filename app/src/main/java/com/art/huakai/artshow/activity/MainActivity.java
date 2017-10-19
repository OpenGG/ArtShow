package com.art.huakai.artshow.activity;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.art.huakai.artshow.R;
import com.art.huakai.artshow.base.BaseActivity;
import com.art.huakai.artshow.constant.Constant;
import com.art.huakai.artshow.entity.EnrollDetailInfo;
import com.art.huakai.artshow.entity.LocalUserInfo;
import com.art.huakai.artshow.entity.UserInfo;
import com.art.huakai.artshow.fragment.CooperateFragment;
import com.art.huakai.artshow.fragment.DiscoverFragment;
import com.art.huakai.artshow.fragment.MeFragment;
import com.art.huakai.artshow.fragment.ShowCircleFragment;
import com.art.huakai.artshow.utils.GsonTools;
import com.art.huakai.artshow.utils.LogUtil;
import com.art.huakai.artshow.utils.LoginUtil;
import com.art.huakai.artshow.utils.RequestUtil;
import com.art.huakai.artshow.utils.ResponseCodeCheck;
import com.art.huakai.artshow.utils.SharePreUtil;
import com.art.huakai.artshow.utils.SignUtil;
import com.art.huakai.artshow.utils.statusBar.ImmerseStatusBar;

import java.util.Map;
import java.util.TreeMap;

import okhttp3.Call;

/**
 * 主界面
 * Created by lidongliang on 2017/9/27.
 */
public class MainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    protected FragmentManager fragmentManager;
    protected FragmentTransaction ft;
    private String[] fragmentTags = new String[]{ShowCircleFragment.TAG_FRAGMENT,
            DiscoverFragment.TAG_FRAGMENT, CooperateFragment.TAG_FRAGMENT, MeFragment.TAG_FRAGMENT};
    //再次点击退出使用
    private long touchTime = 0;
    private int wholeItemPosition=1000;
    private boolean isValid=true;
    private RadioGroup radioGroup;

    public int getWholeItemPosition() {
        if(isValid){
            isValid=false;
            return wholeItemPosition;
        }else{
            return 1000;
        }
    }

    public void setWholeItemPosition(int wholeItemPosition) {
        isValid=true;
        this.wholeItemPosition = wholeItemPosition;
    }
    @Override
    public void immerseStatusBar() {
        ImmerseStatusBar.myStatusBar(this);
        ImmerseStatusBar.setImmerseStatusBar(this, R.color.transparent);
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        fragmentManager = getSupportFragmentManager();
        updateUserInfo();
    }

    /**
     * 更新用户信息
     */
    private void updateUserInfo() {
        if (LoginUtil.checkUserLogin(this, false)) {
            Map<String, String> params = new TreeMap<>();
            params.put("userId", LocalUserInfo.getInstance().getId());
            params.put("accessToken", LocalUserInfo.getInstance().getAccessToken());
            String sign = SignUtil.getSign(params);
            params.put("sign", sign);
            RequestUtil.request(true, Constant.URL_USER_INFO, params, 16, new RequestUtil.RequestListener() {
                @Override
                public void onSuccess(boolean isSuccess, String obj, int code, int id) {
                    LogUtil.i(TAG, obj);
                    if (isSuccess) {
                        try {
                            UserInfo.User user = GsonTools.parseData(obj, UserInfo.User.class);
                            LocalUserInfo localUserInfo = LocalUserInfo.getInstance();
                            localUserInfo.setId(user.id);
                            localUserInfo.setName(user.name);
                            localUserInfo.setMobile(user.mobile);
                            localUserInfo.setEmail(user.email);
                            localUserInfo.setWechatOpenid(user.wechatOpenid);
                            localUserInfo.setDp(user.dp);
                            localUserInfo.setPassword(user.password);
                            localUserInfo.setUserType(user.userType);
                            localUserInfo.setStatus(user.status);
                            localUserInfo.setCreateTime(user.createTime);
                            SharePreUtil.getInstance().storeUserInfo(localUserInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LoginUtil.checkUserLogin(this, false)) {
            if (LocalUserInfo.getInstance().getStatus() == LocalUserInfo.USER_STATUS_DEFAULT ||
                    LocalUserInfo.getInstance().getStatus() == LocalUserInfo.USER_STATUS_UNFILL_DATA) {
                startActivity(new Intent(this, LoginActivity.class));
            }
        }
    }

    @Override
    public void initView() {
        radioGroup= (RadioGroup) findViewById(R.id.rdogp_main_tab);
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void setView() {
        showFragment(ShowCircleFragment.TAG_FRAGMENT);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rdobtn_show_circle:
                showFragment(ShowCircleFragment.TAG_FRAGMENT);
                break;
            case R.id.rdobtn_discover:
                showFragment(DiscoverFragment.TAG_FRAGMENT);
                break;
            case R.id.rdobtn_collaborate:
                showFragment(CooperateFragment.TAG_FRAGMENT);
                break;
            case R.id.rdobtn_me:
                showFragment(MeFragment.TAG_FRAGMENT);
                break;
        }
    }

    /**
     * 显示Fragment
     *
     * @param tag
     */
    private void showFragment(String tag) {
        Log.e(TAG, "showFragment: tag=="+tag );
        ft = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragmentManager.findFragmentByTag(tag) == null) {
            fragment = addFragment(tag);
        }
        if (!fragment.isAdded()) {
            ft.add(R.id.fly_content, fragment, tag);
        } else {
            ft.show(fragment);
            // ft.addToBackStack(tag);
        }
        hideOtherFragment(tag, ft);
        ft.commitAllowingStateLoss();
        if(fragment instanceof DiscoverFragment){
            DiscoverFragment discoverFragment= (DiscoverFragment) fragment;
            ((DiscoverFragment) fragment).setCurrentItem();
        }
    }

    public void setCheckId(int id){
        radioGroup.check(id);
    }

    /**
     * 添加Fragment
     *
     * @param tag
     * @return
     */
    private Fragment addFragment(String tag) {
        Fragment fragment = null;
        if (tag.equals(ShowCircleFragment.TAG_FRAGMENT)) {
            fragment = ShowCircleFragment.newInstance();
        } else if (tag.equals(DiscoverFragment.TAG_FRAGMENT)) {
            fragment = DiscoverFragment.newInstance();
        } else if (tag.equals(CooperateFragment.TAG_FRAGMENT)) {
            fragment = CooperateFragment.newInstance();
        } else if (tag.equals(MeFragment.TAG_FRAGMENT)) {
        fragment = MeFragment.newInstance();
        }
        return fragment;
        }

    /**
     * 隐藏别的Fragment
     *
     * @param tag
     * @param ft
     */
    protected void hideOtherFragment(String tag, FragmentTransaction ft) {
        for (String t : fragmentTags) {
            if (!t.equals(tag)) {
                Fragment otherFragment = fragmentManager.findFragmentByTag(t);
                if (otherFragment != null) {
                    ft.hide(otherFragment);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - touchTime) > Constant.EXIT_APP_TIME_OFFSET) {
            //让Toast的显示时间和等待时间相同
            Toast.makeText(this, getString(R.string.exit_app_tips), Toast.LENGTH_SHORT).show();
            touchTime = currentTime;
        } else {
            this.finish();
        }
    }
}
