package com.fphoenixcorneae.trapezoidimage;

import android.app.Activity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.fphoenixcorneae.widget.trapezoidview.TrapezoidImageView;
import com.fphoenixcorneae.widget.trapezoidview.TrapezoidImageView1;

public class MainActivity extends Activity {

    private static final String IMAGE_URL = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F0219e59585ee83ea1e9d7598bf4e3df9fb9688971901e4-u8ky3Y_fw658&refer=http%3A%2F%2Fhbimg.b0.upaiyun.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1651137478&t=0717b50b5ee3d12d6851db0cc887c9da";
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

//        mTivImage1
//                //梯形上底与下底长度差，单位是dp
//                .setIncline(65f)
//                //圆角大小，单位是dp
//                .setRadius(8f)
//                //遮罩渐变颜色组
//                .setShadeColor(0xff373737, 0xffffffff, 0xff373737)
//        ;
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
