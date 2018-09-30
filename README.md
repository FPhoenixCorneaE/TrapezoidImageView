# TrapezoidImageView
梯形图片控件
================================

！[图片预览](https://github.com/FPhoenixCorneaE/TrapezoidImageView/blob/master/preview/preview.gif)

-----------------------------

xml中使用
----------
```
<com.wkz.trapezoidimage.TrapezoidImageView
        android:id="@+id/tiv_image"
        android:layout_width="215dp"
        android:layout_height="185dp"
        android:scaleType="centerCrop" />
```
--------------------

设置属性
--------------
```
mTivImage.setIncline(65f)//梯形上底与下底长度差，单位是dp
        .setRadius(8f)//圆角大小，单位是dp
        .setShadeColor(0xff373737, 0xffffffff, 0xff373737)//遮罩渐变颜色组
;
```
----------------

加载图片，以Glide示例加载
----------------
```
Glide.with(mTivImage)
       .load(IMAGE_URL)
       .into(mTivImage);
```
