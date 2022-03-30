package com.fphoenixcorneae.widget.trapezoidview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.max

/**
 * 梯形图片控件
 *
 * @author wkz
 * @date 2019/5/26 12:49
 */
class TrapezoidImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    /** 画梯形的画笔 */
    private var mPaint: Paint = Paint()

    /** 通过src设置的图片 */
    private var mDrawable: Drawable? = null

    /** 控件的宽度和高度 */
    private var mWidth = 0
    private var mHeight = 0

    /** 绘制遮罩,使用PorterDuff.Mode.MULTIPLY */
    private var mXfermodeShade = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)

    /** 圆角半径数组 */
    private lateinit var mRadii: FloatArray

    /** 梯度差,即上底与下底长度差 */
    var incline = dpToPx(DEFAULT_INCLINE)

    /** 圆角半径 */
    var radius = dpToPx(DEFAULT_RADIUS)
        set(value) {
            field = value
            initRadii()
        }

    /** 遮罩颜色渐变数组 */
    var shadeColor = DEFAULT_SHADE_COLOR

    /**
     * 初始化自定义属性
     */
    private fun initAttributes(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.TrapezoidImageView, defStyleAttr, 0)
        try {
            incline = attributes.getDimension(R.styleable.TrapezoidImageView_trapezoidIncline, dpToPx(DEFAULT_INCLINE))
            radius = attributes.getDimension(R.styleable.TrapezoidImageView_trapezoidRadius, dpToPx(DEFAULT_RADIUS))
            val shadeColorRes = attributes.getResourceId(R.styleable.TrapezoidImageView_trapezoidShadeColors, 0)
            if (shadeColorRes != 0) {
                shadeColor = resources.getIntArray(shadeColorRes)
            }
        } finally {
            attributes.recycle()
        }
    }

    private fun initPaint() {
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mPaint.flags = Paint.ANTI_ALIAS_FLAG
    }

    private fun initRadii() {
        /* 向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。*/
        /* 圆角的半径，依次为左上角xy半径，右上角，右下角，左下角。*/
        mRadii = floatArrayOf(radius, radius, 0f, 0f, 0f, 0f, radius, radius)
    }

    private fun initXfermode() {
        // 绘制遮罩,使用PorterDuff.Mode.MULTIPLY
        mXfermodeShade = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
    }

    override fun onDraw(canvas: Canvas) {
        mDrawable = drawable
        if (mDrawable == null || mWidth == 0 || mHeight == 0) {
            return
        }

        // 初始化BitmapShader
        initBitmapShader()

        // 画梯形图片
        canvasTrapezoid(canvas)
    }

    /**
     * 初始化BitmapShader
     */
    private fun initBitmapShader() {
        val srcBitmap = getSrcBitmap()
        val mShader = BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val scale = max(width * 1.0f / srcBitmap.width, height * 1.0f / srcBitmap.height)
        val mMatrix = Matrix()
        mMatrix.setScale(scale, scale)
        mShader.setLocalMatrix(mMatrix)
        mPaint.shader = mShader
    }

    /**
     * 画梯形图片
     */
    @SuppressLint("WrongCall")
    private fun canvasTrapezoid(canvas: Canvas) {
        // 背景透明
        canvas.drawColor(Color.TRANSPARENT)
        // 获取圆角梯形路径
        val roundTrapezoidPath = getRoundTrapezoidPath()
        // 裁剪圆角梯形路径
        canvas.clipPath(roundTrapezoidPath)
        super.onDraw(canvas)

        // 画遮罩
        val shadeBitmap = getShadeBitmap()
        // 遮罩图形绘制混合模式
        mPaint.xfermode = mXfermodeShade
        canvas.drawBitmap(shadeBitmap, Matrix(), mPaint)
        mPaint.xfermode = null
        // 回收遮罩位图
        shadeBitmap.recycle()
    }

    /**
     * 利用布尔操作运算获取圆角梯形路径
     */
    private fun getRoundTrapezoidPath(): Path {
        // 获取圆角矩形路径
        val roundRectPath = Path()
        // 向路径中添加圆角矩形
        roundRectPath.addRoundRect(0f, 0f, mWidth.toFloat(), mHeight.toFloat(), mRadii, Path.Direction.CW)
        roundRectPath.close()
        // 获取梯形路径
        val trapezoidPath = Path()
        trapezoidPath.moveTo(0f, 0f)
        trapezoidPath.lineTo(mWidth.toFloat(), 0f)
        trapezoidPath.lineTo(mWidth - incline, mHeight.toFloat())
        trapezoidPath.lineTo(0f, mHeight.toFloat())
        trapezoidPath.close()
        // 布尔操作运算获取圆角矩形路径与梯形路径相交区域，结果存入圆角矩形路径中
        roundRectPath.op(trapezoidPath, Path.Op.INTERSECT)
        return roundRectPath
    }

    /**
     * 利用贝塞尔曲线获取圆角梯形路径
     */
    private fun getRoundTrapezoidPath2(): Path {
        val trapezoidPath = Path()
        // 移动至左上角
        trapezoidPath.moveTo(radius, 0f)
        // 画直线到右上角
        trapezoidPath.lineTo(mWidth.toFloat(), 0f)
        // 画直线到右下角
        trapezoidPath.lineTo(mWidth - incline, mHeight.toFloat())
        // 贝塞尔曲线绘制左下角的圆角
        trapezoidPath.lineTo(radius, mHeight.toFloat())
        trapezoidPath.quadTo(0f, mHeight.toFloat(), 0f, mHeight - radius)
        // 贝塞尔曲线绘制左上角的圆角
        trapezoidPath.lineTo(0f, radius)
        trapezoidPath.quadTo(0f, 0f, radius, 0f)
        // 闭合路径
        trapezoidPath.close()
        return trapezoidPath
    }

    /**
     * 将mDrawable转换成Bitmap对象
     */
    private fun getSrcBitmap(): Bitmap {
        if (mDrawable is BitmapDrawable) {
            return (mDrawable as BitmapDrawable).bitmap
        }
        var bitmapWidth = mDrawable!!.intrinsicWidth
        var bitmapHeight = mDrawable!!.intrinsicHeight
        if (bitmapWidth <= 0) {
            bitmapWidth = mWidth
        }
        if (bitmapHeight <= 0) {
            bitmapHeight = mHeight
        }
        val mBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvasBitmap = Canvas(mBitmap)
        mDrawable!!.setBounds(0, 0, bitmapWidth, bitmapHeight)
        mDrawable!!.draw(canvasBitmap)
        return mBitmap
    }

    /**
     * 获取遮罩Bitmap
     *
     * @return 遮罩Bitmap
     */
    private fun getShadeBitmap(): Bitmap {
        if (shadeColor.size < 2) {
            shadeColor = DEFAULT_SHADE_COLOR
        }
        val shapeDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, shadeColor)
        shapeDrawable.shape = GradientDrawable.RECTANGLE
        val shapeBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
        val shapeCanvas = Canvas(shapeBitmap)
        shapeDrawable.setBounds(0, 0, mWidth, mHeight)
        shapeDrawable.draw(shapeCanvas)
        return shapeBitmap
    }

    /**
     * Convert dp to px
     */
    private fun dpToPx(dp: Float): Float {
        val scale = this.context.resources.displayMetrics.density
        return dp * scale
    }

    companion object {
        private const val DEFAULT_INCLINE = 60f
        private const val DEFAULT_RADIUS = 8f
        private val DEFAULT_SHADE_COLOR = intArrayOf(-0xc8c8c9, -0x1, -0xc8c8c9)
    }

    init {
        initAttributes(context, attrs, defStyleAttr)
        // 初始化画笔
        initPaint()
        // 圆角矩形
        initRadii()
        // 图形绘制混合模式
        initXfermode()
        setWillNotDraw(false)
    }
}