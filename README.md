# TrapezoidImageView

梯形图片控件
================================

[![](https://jitpack.io/v/FPhoenixCorneaE/TrapezoidImageView.svg)](https://jitpack.io/#FPhoenixCorneaE/TrapezoidImageView)

![图片预览](https://github.com/FPhoenixCorneaE/TrapezoidImageView/blob/master/images/trapezoid-view.gif)


How to include it in your project:
--------------
**Step 1.** Add the JitPack repository to your build file
```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

**Step 2.** Add the dependency
```groovy
dependencies {
	implementation("com.github.FPhoenixCorneaE:TrapezoidImageView:$latest")
}
```


xml中使用
----------

```
        <com.fphoenixcorneae.widget.trapezoidview.TrapezoidImageView
            android:id="@+id/iv_image_3"
            android:layout_width="match_parent"
            android:layout_height="185dp"
            android:layout_marginTop="8dp"
            android:scaleType="centerCrop"
            android:src="@mipmap/img_3"
            app:trapezoidIncline="100dp"
            app:trapezoidRadius="20dp"
            app:trapezoidShadeColors="@array/ShadeColors_3" />
```


设置属性
--------------

```
        mIvImage1?.apply {
            // 梯形上底与下底长度差
            incline = 120f
            // 圆角大小
            radius = 20f
            // 遮罩渐变颜色组
            shadeColor = resources.getIntArray(R.array.ShadeColors_1)
        }
```


加载图片，以Glide示例加载
----------------

```
        Glide.with(mIvImage1!!)
            .load(IMAGE_URL)
            .into(mIvImage1!!)
```
