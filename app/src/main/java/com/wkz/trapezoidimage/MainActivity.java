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

        mTivImage.setIncline(65f)
                .setRadius(5f);
    }

    private void initData() {
        Glide.with(mTivImage)
                .load(IMAGE_URL)
                .into(mTivImage);
    }
}
