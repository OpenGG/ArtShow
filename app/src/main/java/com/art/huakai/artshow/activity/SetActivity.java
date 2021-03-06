package com.art.huakai.artshow.activity;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.art.huakai.artshow.R;
import com.art.huakai.artshow.base.BaseActivity;
import com.art.huakai.artshow.constant.JumpCode;
import com.art.huakai.artshow.dialog.ConfirmDialog;
import com.art.huakai.artshow.utils.FrescoHelper;
import com.art.huakai.artshow.utils.LoginUtil;
import com.art.huakai.artshow.utils.SharePreUtil;
import com.art.huakai.artshow.utils.statusBar.ImmerseStatusBar;
import com.art.huakai.artshow.widget.SmartToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 设置页面
 * Created by lidongliang on 2017/10/13.
 */
public class SetActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    private Unbinder mUnbinder;
    private ConfirmDialog confirmDialog;

    @Override
    public void immerseStatusBar() {
        ImmerseStatusBar.myStatusBar(this);
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_set;
    }

    @Override
    public void initData() {
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    public void initView() {
    }

    @Override
    public void setView() {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.setting_title);
    }

    /**
     * 返回
     */
    @OnClick(R.id.lly_back)
    public void back() {
        finish();
    }

    /**
     * 账户信息
     */
    @OnClick(R.id.item_account_info)
    public void jumpAccountInfo() {
        invokActivity(this, AccountInfoActivity.class, null, JumpCode.FLAG_REQ_ACCOUNT_INFO);
    }

    /**
     * 微信绑定
     */
    @OnClick(R.id.item_bind_wechat)
    public void jumpBindWechat() {
        invokActivity(this, BindWechatActivity.class, null, JumpCode.FLAG_REQ_BIND_WECHAT);
    }


    /**
     * 清除缓存
     */
    @OnClick(R.id.item_clear_cache)
    public void clearCache() {
        if (confirmDialog == null) {
            confirmDialog = ConfirmDialog.newInstence(getString(R.string.affirm_clear), getString(R.string.cancel));
            confirmDialog.setOnCallBack(new ConfirmDialog.CallBack() {
                @Override
                public void onChoose(DialogFragment dialogFragment) {
                    dialogFragment.dismiss();
                    FrescoHelper.clear();
                    SmartToast.makeToast(SetActivity.this,
                            getString(R.string.cache_clear_suc),
                            getResources().getDrawable(R.mipmap.image_select_p),
                            Toast.LENGTH_SHORT,
                            Gravity.CENTER,
                            0,
                            0).show();
                }

                @Override
                public void onCancel(DialogFragment dialogFragment) {
                    dialogFragment.dismiss();
                }
            });
        }
        confirmDialog.show(getSupportFragmentManager(), "CONFIRM.DIALOG");
    }

    /**
     * 关于我们
     */
    @OnClick(R.id.item_about_us)
    public void jumpAboutUs() {
        Bundle bundle = new Bundle();
        bundle.putString(WebActivity.PARAMS_TYPE, WebActivity.KEY_ABLOUT);
        invokActivity(this, WebActivity.class, bundle, JumpCode.FLAG_REQ_LINK);
    }

    /**
     * 联系我们
     */
    @OnClick(R.id.item_connect_us)
    public void jumpConnectUs() {
        Bundle bundle = new Bundle();
        bundle.putString(WebActivity.PARAMS_TYPE, WebActivity.KEY_LINKUS);
        invokActivity(this, WebActivity.class, bundle, JumpCode.FLAG_REQ_LINK);
    }

    /**
     * 退出登录
     */
    @OnClick(R.id.btn_exit_login)
    public void exitLogin() {
        //TODO 用一个集合保存左右activity，退出关闭所有activity
        SharePreUtil.getInstance().clearUserInfo();
        SharePreUtil.getInstance().initUserInfo();
        LoginUtil.clearLocalUserInfo();
        setResult(JumpCode.FLAG_RES_LOGIN_OUT);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
