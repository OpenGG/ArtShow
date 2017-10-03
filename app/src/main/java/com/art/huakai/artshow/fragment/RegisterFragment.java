package com.art.huakai.artshow.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.art.huakai.artshow.R;
import com.art.huakai.artshow.activity.WebActivity;
import com.art.huakai.artshow.base.BaseFragment;
import com.art.huakai.artshow.constant.Constant;
import com.art.huakai.artshow.eventbus.LoginEvent;
import com.art.huakai.artshow.okhttp.OkHttpUtils;
import com.art.huakai.artshow.okhttp.callback.Callback;
import com.art.huakai.artshow.okhttp.callback.StringCallback;
import com.art.huakai.artshow.utils.LogUtil;
import com.art.huakai.artshow.utils.MyCountTimer;
import com.art.huakai.artshow.utils.PhoneUtils;
import com.art.huakai.artshow.utils.RequestUtil;
import com.art.huakai.artshow.utils.ResponseCodeCheck;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 注册新账户Fragment
 * Created by lidongliang on 2017/9/27.
 */
public class RegisterFragment extends BaseFragment implements View.OnClickListener {

    private EditText edtPhone, edtVerifyCode, edtPassword, edtPwdAffirm;
    private TextView tvSendVerify;
    private Gson mGson;

    public RegisterFragment() {
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        mGson = new Gson();
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_register;
    }

    @Override
    public void initView(View rootView) {
        TextView tvProtocol = (TextView) rootView.findViewById(R.id.tv_protocol);
        SpannableString spannableString = new SpannableString(getString(R.string.register_protocol_tip));
        spannableString.setSpan(
                new TextAppearanceSpan(getContext(), R.style.protocol_style_gray),
                0,
                spannableString.length() - 7,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(
                new TextAppearanceSpan(getContext(), R.style.protocol_style_blue),
                spannableString.length() - 7,
                spannableString.length() - 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvProtocol.setText(spannableString);

        edtPhone = (EditText) rootView.findViewById(R.id.edt_phone);
        edtVerifyCode = (EditText) rootView.findViewById(R.id.edt_verify_code);
        edtPassword = (EditText) rootView.findViewById(R.id.edt_password);
        edtPwdAffirm = (EditText) rootView.findViewById(R.id.edt_pwd_affirm);

        tvSendVerify = (TextView) rootView.findViewById(R.id.tv_send_verify);

        rootView.findViewById(R.id.lly_back).setOnClickListener(this);
        rootView.findViewById(R.id.btn_register).setOnClickListener(this);

        tvProtocol.setOnClickListener(this);
        tvSendVerify.setOnClickListener(this);
    }

    @Override
    public void setView() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lly_back:
                getActivity().onBackPressed();
                break;
            case R.id.tv_send_verify:
                requestVerifyCode();
                break;
            case R.id.tv_protocol:
                startActivity(new Intent(getContext(), WebActivity.class));
                break;
            case R.id.btn_register:
                //应该注册成功后，走接下来的完善信息流程
                //EventBus.getDefault().post(new LoginEvent(LoginEvent.CODE_ACTION_REGISTER_SUC));
                doRegister();
                break;
        }
    }

    /**
     * 注册
     */
    private void doRegister() {
        String phoneNum = edtPhone.getText().toString();
        String verifyCode = edtVerifyCode.getText().toString();
        String pwd = edtPassword.getText().toString();
        String pwdAffirm = edtPwdAffirm.getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            showToast(getString(R.string.tip_input_phone));
            return;
        }
        if (!PhoneUtils.isMobileNumber(phoneNum)) {
            showToast(getString(R.string.please_input_correct_phone));
            return;
        }
        if (TextUtils.isEmpty(verifyCode)) {
            showToast(getString(R.string.tip_input_verify_code));
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            showToast(getString(R.string.tip_input_pwd));
            return;
        }
        if (TextUtils.isEmpty(pwdAffirm)) {
            showToast(getString(R.string.tip_input_entrue_pwd));
            return;
        }
        if (!pwd.equals(pwdAffirm)) {
            showToast(getString(R.string.tip_input_dif_psw));
            return;
        }
        if (pwd.length() < 8) {
            showToast(getString(R.string.tip_input_length));
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("mobile", phoneNum);
        params.put("password", pwd);
        params.put("verifyCode", verifyCode);
        RequestUtil.request(true, Constant.URL_USER_REGISTER, params, 10, new RequestUtil.RequestListener() {
            @Override
            public void onSuccess(boolean isSuccess, String obj, int code, int id) {
                LogUtil.i(TAG, obj);
                if (isSuccess) {
                    //mGson.fromJson(obj,)
                }
            }

            @Override
            public void onFailed(Call call, Exception e, int id) {
                LogUtil.e(TAG, e.getMessage() + "- id = " + id);
            }
        });
    }

    /**
     * 获取验证码
     */
    public void requestVerifyCode() {
        String phoneNum = edtPhone.getText().toString();
        if (TextUtils.isEmpty(phoneNum)) {
            showToast(getString(R.string.tip_input_phone));
            return;
        }
        if (!PhoneUtils.isMobileNumber(phoneNum)) {
            showToast(getString(R.string.please_input_correct_phone));
            return;
        } else {
            final MyCountTimer timeCount = new MyCountTimer(tvSendVerify,
                    getResources().getColor(R.color.register_new),
                    getResources().getColor(R.color.login_light));// 传入了文字颜色值
            timeCount.start();
            HashMap<String, String> params = new HashMap<>();
            params.put("receiver", phoneNum);
            params.put("method", "sms");
            RequestUtil.request(false, Constant.URL_GET_VERIFY_CODE, params, 11, new RequestUtil.RequestListener() {
                @Override
                public void onSuccess(boolean isSuccess, String obj, int code, int id) {
                    LogUtil.i(TAG, obj);
                    if (isSuccess) {

                    }
                }

                @Override
                public void onFailed(Call call, Exception e, int id) {
                    LogUtil.e(TAG, e.getMessage() + "-id = " + id);
                }
            });
        }
    }
}
