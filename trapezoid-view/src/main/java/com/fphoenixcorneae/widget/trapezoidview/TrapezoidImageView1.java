package com.fphoenixcorneae.widget.trapezoidview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * 梯形图片控件
 *
 * @author wkz
 * @date 2019/5/26 12:49
 */
public class TrapezoidImageView1 extends AppCompatImageView {

    private static final float DEFAULT_INCLINE = 60F;
    private static final float DEFAULT_RADIUS = 8F;
    private static final int[] DEFAULT_SHADE_COLOR = new int[]{0xff373737, 0xffffffff, 0xff373737};

    /**
     * 画梯形的画笔
     */
    private Paint mPaint;
    /**
     * 通过src设置的图片
     */
    private Drawable mDrawable = null;
    /**
     * 控件的宽度和高度
     */
    private int mWidth, mHeight;
    /**
     * 梯度差,即上底与下底长度差
     */
    private float mIncline;
    /**
     * 圆角半径
     */
    private float mRadius;
    /**
     * 遮罩颜色
     */
    private int[] mShadeColor;
    private PorterDuffXfermode mXfermodeShade;
    private float[] mRadii;

    public TrapezoidImageView1(Context context) {
        this(context, null);
    }

    public TrapezoidImageView1(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrapezoidImageView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            initAttributes(context, attrs, defStyleAttr);
        } else {
            this.mIncline = dpToPx(DEFAULT_INCLINE);
            this.mRadius = dpToPx(DEFAULT_RADIUS);
            this.mShadeColor = DEFAULT_SHADE_COLOR;
        }
        init();
    }

    /**
     * 初始化自定义属性
     */
    private void initAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TrapezoidImageView, defStyleAttr, 0);
        try {
            mIncline = attributes.getDimension(R.styleable.TrapezoidImageView_trapezoidIncline, dpToPx(DEFAULT_INCLINE));
            mRadius = attributes.getDimension(R.styleable.TrapezoidImageView_trapezoidRadius, dpToPx(DEFAULT_RADIUS));
            int shadeColorRes = attributes.getResourceId(R.styleable.TrapezoidImageView_trapezoidShadeColors, 0);
            if (shadeColorRes != 0) {
                mShadeColor = getResources().getIntArray(shadeColorRes);
            } else {
                mShadeColor = DEFAULT_SHADE_COLOR;
            }
        } finally {
            attributes.recycle();
        }
    }

    /**
     * 初始化操作
     */
    private void init() {

        // 初始化画笔
        initPaint();
        // 圆角矩形
        initRadii();
        // 图形绘制混合模式
        initXfermode();

        setWillNotDraw(false);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    private void initRadii() {
        /* 向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。*/
        /* 圆角的半径，依次为左上角xy半径，右上角，右下角，左下角。*/
        mRadii = new float[]{mRadius, mRadius, 0.0f, 0.0f, 0.0f, 0.0f, mRadius, mRadius};
    }

    private void initXfermode() {
        // 绘制遮罩,使用PorterDuff.Mode.MULTIPLY
        mXfermodeShade = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mDrawable = getDrawable();

        if (mDrawable == null || mWidth == 0 || mHeight == 0) {
            return;
        }

        // 初始化BitmapShader
        initBitmapShader();

        // 画梯形图片
        canvasTrapezoid(canvas);
    }

    /**
     * 初始化BitmapShader
     */
    private void initBitmapShader() {
        Bitmap srcBitmap = getSrcBitmap();
        BitmapShader mShader = new BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = Math.max(getWidth() * 1.0f / srcBitmap.getWidth(), getHeight() * 1.0f / srcBitmap.getHeight());
        Matrix mMatrix = new Matrix();
        mMatrix.setScale(scale, scale);
        mShader.setLocalMatrix(mMatrix);
        mPaint.setShader(mShader);
    }

    /**
     * 画梯形图片
     */
    @SuppressLint("WrongCall")
    private void canvasTrapezoid(Canvas canvas) {
        // 背景透明
        canvas.drawColor(Color.TRANSPARENT);
        // 裁剪梯形路径
        canvas.clipPath(getRoundTrapezoidPath());
        super.onDraw(canvas);

        // 画遮罩
        Bitmap shadeBitmap = getShadeBitmap();
        // 遮罩图形绘制混合模式
        mPaint.setXfermode(mXfermodeShade);
        canvas.drawBitmap(shadeBitmap, new Matrix(), mPaint);
        mPaint.setXfermode(null);
        // 回收遮罩位图
        shadeBitmap.recycle();
    }

    /**
     * 获取圆角梯形路径
     */
    private Path getRoundTrapezoidPath() {
        Path trapezoidPath = new Path();
        // 移动至左上角
        trapezoidPath.moveTo(mRadius, 0);
        // 画直线到右上角
        trapezoidPath.lineTo(mWidth, 0);
        // 画直线到右下角
        trapezoidPath.lineTo(mWidth - mIncline, mHeight);
        // 贝塞尔曲线绘制左下角的圆角
        trapezoidPath.lineTo(mRadius, mHeight);
        trapezoidPath.quadTo(0, mHeight, 0, mHeight - mRadius);
        // 贝塞尔曲线绘制左上角的圆角
        trapezoidPath.lineTo(0, mRadius);
        trapezoidPath.quadTo(0, 0, mRadius, 0);
        // 闭合路径
        trapezoidPath.close();
        return trapezoidPath;
    }

    /**
     * 将mDrawable转换成Bitmap对象
     */
    private Bitmap getSrcBitmap() {
        if (mDrawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) mDrawable).getBitmap();
        }
        int bitmapWidth = mDrawable.getIntrinsicWidth();
        int bitmapHeight = mDrawable.getIntrinsicHeight();

        if (bitmapWidth <= 0) {
            bitmapWidth = mWidth;
        }

        if (bitmapHeight <= 0) {
            bitmapHeight = mHeight;
        }

        Bitmap mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvasBitmap = new Canvas(mBitmap);
        mDrawable.setBounds(0, 0, bitmapWidth, bitmapHeight);
        mDrawable.draw(canvasBitmap);
        return mBitmap;
    }

    /**
     * 获取遮罩Bitmap
     *
     * @return 遮罩Bitmap
     */
    private Bitmap getShadeBitmap() {
        if (mShadeColor.length < 2) {
            mShadeColor = DEFAULT_SHADE_COLOR;
        }
        GradientDrawable shapeDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, mShadeColor);
        shapeDrawable.setShape(GradientDrawable.RECTANGLE);
        Bitmap shapeBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas shapeCanvas = new Canvas(shapeBitmap);
        shapeDrawable.setBounds(0, 0, mWidth, mHeight);
        shapeDrawable.draw(shapeCanvas);
        return shapeBitmap;
    }

    /**
     * Convert dp to px
     */
    private float dpToPx(float dp) {
        float scale = this.getContext().getResources().getDisplayMetrics().density;
        return dp * scale;
    }

    /**
     * @param mIncline dpValue
     */
    public TrapezoidImageView1 setIncline(float mIncline) {
        this.mIncline = dpToPx(mIncline);
        return this;
    }

    /**
     * @param mRadius dpValue
     */
    public TrapezoidImageView1 setRadius(float mRadius) {
        this.mRadius = dpToPx(mRadius);
        initRadii();
        return this;
    }

    public TrapezoidImageView1 setShadeColor(int... mShadeColor) {
        this.mShadeColor = mShadeColor;
        return this;
    }

    public TrapezoidImageView1 setRadii(float[] mRadii) {
        this.mRadii = mRadii;
        return this;
    }
}
