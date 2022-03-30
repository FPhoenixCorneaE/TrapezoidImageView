package com.fphoenixcorneae.trapezoidimage

import android.app.Activity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.fphoenixcorneae.widget.trapezoidview.TrapezoidImageView

class MainActivity : Activity() {
    private var mIvImage1: TrapezoidImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()
    }

    private fun initView() {
        mIvImage1 = findViewById(R.id.iv_image_1)

        mIvImage1?.apply {
            // 梯形上底与下底长度差
            incline = 120f
            // 圆角大小
            radius = 20f
            // 遮罩渐变颜色组
            shadeColor = resources.getIntArray(R.array.ShadeColors_1)
        }
    }

    private fun initData() {
        Glide.with(mIvImage1!!)
            .load(IMAGE_URL)
            .into(mIvImage1!!)
    }

    companion object {
        private const val IMAGE_URL =
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fhbimg.b0.upaiyun.com%2F0219e59585ee83ea1e9d7598bf4e3df9fb9688971901e4-u8ky3Y_fw658&refer=http%3A%2F%2Fhbimg.b0.upaiyun.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1651137478&t=0717b50b5ee3d12d6851db0cc887c9da"
    }
}