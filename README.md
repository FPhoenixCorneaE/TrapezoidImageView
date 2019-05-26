# TrapezoidImageView
梯形图片控件
================================

![图片预览](https://github.com/FPhoenixCorneaE/TrapezoidImageView/blob/master/preview/preview.gif)

-----------------------------

xml中使用
----------
```
<com.wkz.trapezoidimage.TrapezoidImageView
                android:id="@+id/tiv_image_3"
                android:layout_width="match_parent"
                android:layout_height="185dp"
                android:layout_marginTop="20dp"
                android:scaleType="centerCrop"
                android:src="@mipmap/img_2"
                app:tiv_in_cline="80dp"
                app:tiv_radius="15dp"
                app:tiv_shade_colors="@array/ShadeColors_1" />
```

--------------------

设置属性
--------------
```
mTivImage1
                //梯形上底与下底长度差，单位是dp
                .setIncline(65f)
                //圆角大小，单位是dp
                .setRadius(8f)
                //遮罩渐变颜色组
                .setShadeColor(0xff373737, 0xffffffff, 0xff373737)
        ;
```

----------------

加载图片，以Glide示例加载
----------------
```
Glide.with(mTivImage1)
                .load(IMAGE_URL)
                .into(mTivImage1);
```
