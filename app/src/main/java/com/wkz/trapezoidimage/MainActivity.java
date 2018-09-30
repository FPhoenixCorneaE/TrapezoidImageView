package com.wkz.trapezoidimage;

import android.app.Activity;
import android.os.Bundle;

import com.bumptech.glide.Glide;

public class MainActivity extends Activity {

    private TrapezoidImageView mTivImage;
    private static final String IMAGE_URL = "http://data.whicdn.com/images/142801637/large.gif";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        mTivImage = findViewById(R.id.tiv_image);

        mTivImage.setIncline(65f)//梯形上底与下底长度差，单位是dp
                .setRadius(8f)//圆角大小，单位是dp
                .setShadeColor(0xff373737, 0xffffffff, 0xff373737)//遮罩渐变颜色组
        ;
    }

    private void initData() {
        Glide.with(mTivImage)
                .load(IMAGE_URL)
                .into(mTivImage);
    }
}
