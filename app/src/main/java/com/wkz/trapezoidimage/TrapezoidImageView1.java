package com.wkz.trapezoidimage;

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
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 梯形图片控件
 *
 * @author wkz
 * @date 2019/5/26 12:49
 */
public class TrapezoidImageView1 extends ImageView {

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
    private PorterDuffXfermode mXfermodeSrc;
    private PorterDuffXfermode mXfermodeShape;
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
            mIncline = attributes.getDimension(R.styleable.TrapezoidImageView_tiv_in_cline, dpToPx(DEFAULT_INCLINE));
            mRadius = attributes.getDimension(R.styleable.TrapezoidImageView_tiv_radius, dpToPx(DEFAULT_RADIUS));
            int shadeColorRes = attributes.getResourceId(R.styleable.TrapezoidImageView_tiv_shade_colors, 0);
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

        setDrawingCacheEnabled(true);
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
        // 叠加处绘制源图,PorterDuff.Mode.SRC_IN只显示两层图像交集部分的上层图像
        mXfermodeSrc = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        // 绘制遮罩,使用PorterDuff.Mode.MULTIPLY
        mXfermodeShape = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
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

        //初始化BitmapShader
        initBitmapShader();

        //画梯形图片
        canvasTrapezoid(canvas);
    }

    /**
     * 初始化BitmapShader
     */
    private void initBitmapShader() {
        BitmapShader mShader = new BitmapShader(getSrcBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = Math.max(getWidth() * 1.0f / getSrcBitmap().getWidth(), getHeight() * 1.0f / getSrcBitmap().getHeight());
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

        // 绘制梯形路径
        canvas.clipPath(getTrapezoidPath());
        super.onDraw(canvas);

        // 画遮罩
        Bitmap shapeBitmap = getShapeBitmap();
        // 遮罩图形绘制混合模式
        mPaint.setXfermode(mXfermodeShape);
        canvas.drawBitmap(shapeBitmap, new Matrix(), mPaint);
        mPaint.setXfermode(null);
        shapeBitmap.recycle();
    }

    /**
     * 获取圆角梯形路径
     */
    private Path getTrapezoidPath() {
        Path trapezoidPath = new Path();
        trapezoidPath.moveTo(mRadius, 0);
        trapezoidPath.lineTo(mWidth, 0);
        trapezoidPath.lineTo(mWidth - mIncline, mHeight);
        trapezoidPath.lineTo(mRadius, mHeight);
        // 贝塞尔曲线绘制左下角的圆角
        trapezoidPath.quadTo(0, mHeight, 0, mHeight - mRadius);
        trapezoidPath.lineTo(0, mRadius);
        // 贝塞尔曲线绘制左上角的圆角
        trapezoidPath.quadTo(0, 0, mRadius, 0);
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
    private Bitmap getShapeBitmap() {
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
