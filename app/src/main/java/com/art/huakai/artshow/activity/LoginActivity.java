package com.art.huakai.artshow.activity;

import android.support.v4.app.Fragment;

import com.art.huakai.artshow.R;
import com.art.huakai.artshow.base.BaseActivity;
import com.art.huakai.artshow.eventbus.LoginEvent;
import com.art.huakai.artshow.fragment.LoginFragment;
import com.art.huakai.artshow.fragment.LoginRegFragment;
import com.art.huakai.artshow.fragment.RegisterFragment;
import com.art.huakai.artshow.utils.statusBar.ImmerseStatusBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LoginActivity extends BaseActivity {

    private LoginRegFragment mLoginRegFragment;

    @Override
    public void immerseStatusBar() {
        ImmerseStatusBar.myStatusBar(this);
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_login;
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);
        mLoginRegFragment = LoginRegFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fly_content, mLoginRegFragment).commit();
    }

    @Override
    public void initView() {

    }

    @Override
    public void setView() {

    }

    /**
     * 从登录fragment中来的事件，替换LogingActivity中的fragment
     *
     * @param enterEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventTalk(LoginEvent enterEvent) {
        if (enterEvent == null)
            return;
        switch (enterEvent.getActionCode()) {
            case LoginEvent.CODE_ACTION_LOGIN:
                LoginFragment loginFragment = LoginFragment.newInstance();
                initFragmentAddback(loginFragment);
                break;
            case LoginEvent.CODE_ACTION_REGISTER:
                RegisterFragment registerFragment = RegisterFragment.newInstance();
                initFragmentAddback(registerFragment);
                break;
        }
    }

    public void initFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fly_content, fragment).commit();
    }

    //添加一个fragment到返回栈中
    public void initFragmentAddback(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fly_content, fragment).addToBackStack(null).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}