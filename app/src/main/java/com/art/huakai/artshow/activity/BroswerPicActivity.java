package com.art.huakai.artshow.activity;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.art.huakai.artshow.R;
import com.art.huakai.artshow.base.BaseActivity;
import com.art.huakai.artshow.utils.statusBar.ImmerseStatusBar;
import com.art.huakai.artshow.widget.MultiTouchViewPager;
import com.art.huakai.artshow.widget.photoview.PhotoDraweeView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

import java.util.ArrayList;

import butterknife.BindView;

public class BroswerPicActivity extends BaseActivity {


    @BindView(R.id.view_pager)
    MultiTouchViewPager viewPager;
    @BindView(R.id.ll)
    LinearLayout ll;
    @BindView(R.id.iv_close)
    ImageView ivClose;
    private ArrayList<String> lists = new ArrayList<String>();
    private int lastPosition = 0;

    @Override
    public void immerseStatusBar() {
        ImmerseStatusBar.myStatusBar(this);
//        ImmerseStatusBar.setImmerseStatusBar(this, R.color.transparent);
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_pic_browse;
    }

    @Override
    public void initData() {
    }

    @Override
    public void initView() {

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    public void setView() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        lists = intent.getStringArrayListExtra("list");
        lastPosition = intent.getIntExtra("position", 0);
        Log.e(TAG, "onCreate: size==" + lists.size() + "--position==" + lastPosition);
        setData();
    }

    public class DraweePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(final ViewGroup viewGroup, int position) {
            final PhotoDraweeView photoDraweeView = new PhotoDraweeView(viewGroup.getContext());
            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
            controller.setUri(Uri.parse(lists.get(position)));
            controller.setOldController(photoDraweeView.getController());
            controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    if (imageInfo == null) {
                        return;
                    }
                    Log.e(TAG, "onFinalImageSet: getWidth==" + imageInfo.getWidth() + "--getHeight==" + imageInfo.getHeight());
                    photoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                }
            });
            photoDraweeView.setController(controller.build());
            try {
                Log.e(TAG, "instantiateItem: 111111111111111");
                viewGroup.addView(photoDraweeView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return photoDraweeView;
        }
    }


    private void setData() {


        for (int i = 0; i < lists.size(); i++) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(25, 25);
            if (i != 0) {
                layoutParams.leftMargin = 15;
            }
            View viewone = new View(BroswerPicActivity.this);
            viewone.setLayoutParams(layoutParams);
            if (lastPosition == i) {
                viewone.setBackgroundResource(R.drawable.shape_bg_yellow_circle);
            } else {
                viewone.setBackgroundResource(R.drawable.shape_bg_grey_circle);
            }
            ll.addView(viewone);
        }


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (lastPosition != -1) {
                    View view = ll.getChildAt(lastPosition);
                    view.setBackgroundResource(R.drawable.shape_bg_grey_circle);
                }

                View view = ll.getChildAt(position);
                view.setBackgroundResource(R.drawable.shape_bg_yellow_circle);
                lastPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(new DraweePagerAdapter());
        if (lastPosition > 0) {
            viewPager.setCurrentItem(lastPosition);
        } else {
            viewPager.setCurrentItem(0);
        }

    }

}
