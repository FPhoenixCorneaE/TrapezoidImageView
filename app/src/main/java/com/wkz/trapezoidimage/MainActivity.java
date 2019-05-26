package com.wkz.trapezoidimage;

import android.app.Activity;
import android.os.Bundle;

import com.bumptech.glide.Glide;

public class MainActivity extends Activity {

    private static final String IMAGE_URL = "http://data.whicdn.com/images/142801637/large.gif";
    private TrapezoidImageView mTivImage1;
    private TrapezoidImageView1 mTivImage2;
    private TrapezoidImageView mTivImage3;
    private TrapezoidImageView1 mTivImage4;
    private TrapezoidImageView mTivImage5;
    private TrapezoidImageView1 mTivImage6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        mTivImage1 = findViewById(R.id.tiv_image_1);
        mTivImage2 = findViewById(R.id.tiv_image_2);
        mTivImage3 = findViewById(R.id.tiv_image_3);
        mTivImage4 = findViewById(R.id.tiv_image_4);
        mTivImage5 = findViewById(R.id.tiv_image_5);
        mTivImage6 = findViewById(R.id.tiv_image_6);

        mTivImage1
                //梯形上底与下底长度差，单位是dp
                .setIncline(65f)
                //圆角大小，单位是dp
                .setRadius(8f)
                //遮罩渐变颜色组
                .setShadeColor(0xff373737, 0xffffffff, 0xff373737)
        ;
    }

    private void initData() {
        Glide.with(mTivImage1)
                .load(IMAGE_URL)
                .into(mTivImage1);
        Glide.with(mTivImage2)
                .load(IMAGE_URL)
                .into(mTivImage2);
    }
}
