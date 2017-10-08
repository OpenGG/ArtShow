package com.art.huakai.artshow.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Toast;

import com.art.huakai.artshow.R;
import com.art.huakai.artshow.base.BaseFragment;
import com.art.huakai.artshow.dialog.TypeConfirmDialog;
import com.art.huakai.artshow.eventbus.LoginEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * 账户类型选择Fragment
 * Created by lidongliang on 2017/9/27.
 */
public class PerfectInfoFragment extends BaseFragment implements View.OnClickListener {

    public PerfectInfoFragment() {
        // Required empty public constructor
    }

    public static PerfectInfoFragment newInstance() {
        PerfectInfoFragment fragment = new PerfectInfoFragment();
        return fragment;
    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_perfect_info;
    }

    @Override
    public void initView(View rootView) {
        rootView.findViewById(R.id.btn_next_step).setOnClickListener(this);
    }

    @Override
    public void setView() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_step:
                EventBus.getDefault().post(new LoginEvent(LoginEvent.CODE_ACTION_PERFECT_INFO_SUC));
                break;
        }
    }
}