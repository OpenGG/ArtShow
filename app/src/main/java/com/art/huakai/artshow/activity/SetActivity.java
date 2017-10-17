package com.art.huakai.artshow.activity;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.art.huakai.artshow.R;
import com.art.huakai.artshow.base.BaseActivity;
import com.art.huakai.artshow.constant.JumpCode;
import com.art.huakai.artshow.dialog.ConfirmDialog;
import com.art.huakai.artshow.eventbus.NameChangeEvent;
import com.art.huakai.artshow.utils.statusBar.ImmerseStatusBar;
import com.art.huakai.artshow.widget.SmartToast;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        Toast.makeText(this, "关于我们", Toast.LENGTH_SHORT).show();
    }

    /**
     * 联系我们
     */
    @OnClick(R.id.item_connect_us)
    public void jumpConnectUs() {
        Toast.makeText(this, "联系我们", Toast.LENGTH_SHORT).show();
    }

    /**
     * 退出登录
     */
    @OnClick(R.id.btn_exit_login)
    public void exitLogin() {
        Toast.makeText(this, "退出登录", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}