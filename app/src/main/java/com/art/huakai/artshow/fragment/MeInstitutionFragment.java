package com.art.huakai.artshow.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.art.huakai.artshow.R;
import com.art.huakai.artshow.activity.DataUploadActivity;
import com.art.huakai.artshow.activity.ResumeActivity;
import com.art.huakai.artshow.activity.TheatreActivity;
import com.art.huakai.artshow.base.BaseFragment;
import com.art.huakai.artshow.constant.JumpCode;
import com.art.huakai.artshow.widget.SettingItem;


/**
 * 我Fragment,没有登录状态下，底部推荐内容
 * Created by lidongliang on 2017/9/27.
 */
public class MeInstitutionFragment extends BaseFragment implements View.OnClickListener {

    private SettingItem itemAuth;
    private SettingItem itemMyProject;
    private SettingItem itemMyTheatre;
    private SettingItem itemMyTalent;

    public MeInstitutionFragment() {
        // Required empty public constructor
    }

    public static MeInstitutionFragment newInstance() {
        MeInstitutionFragment fragment = new MeInstitutionFragment();
        return fragment;
    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_me_institution;
    }

    @Override
    public void initView(View rootView) {
        itemAuth = (SettingItem) rootView.findViewById(R.id.item_institution_auth);
        itemMyProject = (SettingItem) rootView.findViewById(R.id.item_my_project);
        itemMyTheatre = (SettingItem) rootView.findViewById(R.id.item_my_theatre);
        itemMyTalent = (SettingItem) rootView.findViewById(R.id.item_my_talent);

        itemAuth.setOnClickListener(this);
        itemMyProject.setOnClickListener(this);
        itemMyTheatre.setOnClickListener(this);
        itemMyTalent.setOnClickListener(this);
    }

    @Override
    public void setView() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_institution_auth:
                Bundle bundle = new Bundle();
                bundle.putString(DataUploadActivity.PARAMS_FROM, DataUploadFragment.FROM_ME);
                invokActivity(getContext(), DataUploadActivity.class, bundle, JumpCode.FLAG_REQ_DATA_UPLOAD);
                break;
            case R.id.item_my_project:
                break;
            case R.id.item_my_theatre:
                invokActivity(getContext(), TheatreActivity.class, null, JumpCode.FLAG_REQ_THEATRE_MY);
                break;
            case R.id.item_my_talent:
                break;
        }
    }
}
