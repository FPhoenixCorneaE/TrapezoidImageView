package com.wkz.trapezoidimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 梯形图片控件
 */
public class TrapezoidImageView extends ImageView {

    private static final float DEFAULT_INCLINE = 65F;
    private static final float DEFAULT_RADIUS = 8F;
    private static final int[] DEFAULT_SHADE_COLOR = new int[]{0xff373737, 0xffffffff, 0xff373737};

    private Paint mPaint;//画梯形的画笔
    private Drawable mDrawable = null;//通过src设置的图片
    private int mWidth, mHeight;//控件的宽度和高度
    private float mIncline;//梯度,即上底与下底长度差
    private float mRadius;//圆角半径
    private int[] mShadeColor;//遮罩颜色
    private PorterDuffXfermode mXfermodeSrc;
    private PorterDuffXfermode mXfermodeShape;
    private float[] mRadii;

    public TrapezoidImageView(Context context) {
        this(context, null);
    }

    public TrapezoidImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrapezoidImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化操作
     */
    private void init() {

        this.mIncline = dpToPx(DEFAULT_INCLINE);
        this.mRadius = dpToPx(DEFAULT_RADIUS);
        this.mShadeColor = DEFAULT_SHADE_COLOR;

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
    private void canvasTrapezoid(Canvas canvas) {
        // 背景透明
        canvas.drawColor(Color.TRANSPARENT);
        // 保存为单独的层
        int saveCount = canvas.saveLayer(0, 0, mWidth, mHeight, mPaint, Canvas.ALL_SAVE_FLAG);

        // 绘制圆角矩形
        canvas.drawPath(getRoundRectPath(), mPaint);

        // 图形绘制混合模式
        mPaint.setXfermode(mXfermodeSrc);

        // 绘制梯形路径
        canvas.drawPath(getTrapezoidPath(), mPaint);

        // 画遮罩
        mPaint.setXfermode(mXfermodeShape);
        canvas.drawBitmap(getShapeBitmap(), new Matrix(), mPaint);

        mPaint.setXfermode(null);
        canvas.restoreToCount(saveCount);
    }

    /**
     * 获取圆角矩形路径
     */
    @NonNull
    private Path getRoundRectPath() {
        Path path = new Path();
        // 向路径中添加圆角矩形
        path.addRoundRect(new RectF(0, 0, mWidth, mHeight), mRadii, Path.Direction.CW);
        return path;
    }

    /**
     * 获取梯形路径
     */
    private Path getTrapezoidPath() {
        Path topPath = new Path();
        topPath.moveTo(0, 0);
        topPath.lineTo(mWidth, 0);
        topPath.lineTo(mWidth - mIncline, mHeight);
        topPath.lineTo(0, mHeight);
        topPath.close();
        return topPath;
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
     * @return
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
     * @return
     */
    public TrapezoidImageView setIncline(float mIncline) {
        this.mIncline = dpToPx(mIncline);
        return this;
    }

    /**
     * @param mRadius dpValue
     * @return
     */
    public TrapezoidImageView setRadius(float mRadius) {
        this.mRadius = dpToPx(mRadius);
        initRadii();
        return this;
    }

    public TrapezoidImageView setShadeColor(int... mShadeColor) {
        this.mShadeColor = mShadeColor;
        return this;
    }

    public TrapezoidImageView setRadii(float[] mRadii) {
        this.mRadii = mRadii;
        return this;
    }
}
