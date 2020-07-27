package com.ysj.ui.floatingbottomnavigation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlin.math.abs
import kotlin.math.sqrt


/**
 * 溢出浮动效果的底部导航
 * <p>
 * @author Ysj
 * Create time: 2020/7/24
 */
class FloatingBottomNavigation : LinearLayout {

    // =================== 绘制参数 =====================

    // 分割线颜色
    private var mDividerColor: Int

    // 分割线画笔宽度
    private var mDividerStrokeWidth: Float

    // 最大浮动
    private var mMaxFloating: Float

    // =================================================

    // =================== 绘制工具 =====================

    private val mDividerPaint = Paint()
    private val mDividerPath = Path()

    // =================================================

    // debug 的辅助绘制
    private var mDebugDraws: MutableList<Runnable>? = null

    // debug 的画笔
    private var mDebugPaint: Paint? = null

    // 绑定的用于响应浮动效果的 View 们
    private var mBindViews = ArrayList<View>()


    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {
        // 加载自定义属性
        context.obtainStyledAttributes(attrs, R.styleable.FloatingBottomNavigation).apply {
            mMaxFloating = getDimension(R.styleable.FloatingBottomNavigation_maxFloating, 0f)
            mDividerStrokeWidth =
                getDimension(R.styleable.FloatingBottomNavigation_dividerStrokeWidth, 0f)
            mDividerColor = getColor(R.styleable.FloatingBottomNavigation_dividerColor, Color.GRAY)
        }.recycle()
        init()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(Color.TRANSPARENT)
        mDebugPaint?.apply {
            canvas.drawLine(0f, 0f, width.toFloat(), 0f, this)
        }
        // 因为需要裁切 view 的背景，因此该调用需要在 super.draw(canvas) 前
        drawDivider(canvas)
        super.draw(canvas)
        mDebugDraws?.forEach { it.run() }
    }

    /**
     * 绑定一个 View 为来响应浮动效果
     */
    fun bindView(view: View?) {
        view?.apply { mBindViews.add(this) }
    }

    /**
     * 解绑一个 View
     */
    fun unBindView(view: View?) {
        view?.apply { mBindViews.remove(this) }
    }

    /**
     * 设置分割线颜色
     *
     * @attr ref R.styleable#FloatingBottomNavigation_dividerColor
     */
    fun setDividerColor(color: Int) {
        mDividerColor = color
        invalidate()
    }

    /**
     * 获取分割线的颜色
     */
    fun getDividerColor() = mDividerColor

    /**
     * 设置分割线的画笔粗细（px）
     *
     * @attr ref R.styleable#FloatingBottomNavigation_dividerStrokeWidth
     */
    fun setDividerStrokeWidth(dividerStrokeWidth: Float) {
        mDividerStrokeWidth = dividerStrokeWidth
        mDividerPaint.strokeWidth = mDividerStrokeWidth
        invalidate()
    }

    /**
     * 获取分割线画笔的粗细（px）
     */
    fun getDividerStrokeWidth() = mDividerStrokeWidth

    /**
     * 设置最大浮动范围（px）
     *
     * @attr ref R.styleable#FloatingBottomNavigation_maxFloating
     */
    fun setMaxFloating(maxFloat: Float) {
        mMaxFloating = maxFloat
        invalidate()
    }

    /**
     * 获取最大浮动范围（px）
     */
    fun getMaxFloating() = mMaxFloating

    /**
     * 是否开启 debug，开启后可以看到绘制辅助视图
     *
     * @param enable 为 true 则开启 debug
     */
    fun setDebugEnable(enable: Boolean) {
        mDebugDraws = if (enable) ArrayList() else null
        mDebugPaint = if (enable) Paint().apply {
            color = Color.CYAN
            style = Paint.Style.STROKE
            isAntiAlias = true
            strokeWidth = 4f
        } else null
        invalidate()
    }

    private fun drawDivider(canvas: Canvas) {
        mDebugDraws?.clear()

        mDividerPath.reset()
        mDividerPath.moveTo(-mDividerStrokeWidth, mMaxFloating)
        mBindViews.forEach {
            val childRealX = getChildRealX(it)
            val childRealY = getChildRealY(it)
            // 缩放后的宽高
            val childRealWidth = it.width * it.scaleX
            val childRealHeight = it.height * it.scaleY
            // view 中心坐标
            val x = childRealX + it.width / 2
            val y = childRealY + it.height / 2
            // 外圆半径
            val radius = sqrt(
                (childRealWidth / 2 * childRealWidth / 2
                        + childRealHeight / 2 * childRealHeight / 2).toDouble()
            ).toFloat()
            // 控制点 Y 坐标
            val controlPointY = if (y - radius >= mMaxFloating) mMaxFloating
            else y - radius - abs(y - radius - mMaxFloating)
            // 达到半径的比例
            val p = (mMaxFloating - controlPointY) / radius
            val r = if (p <= 0) 0f else (radius - childRealWidth / 2) * p / 2

            mDebugDraws?.add(Runnable {
                mDebugPaint?.apply {
                    val dr = 8f
                    // 第一段，原始点：canvas.drawCircle(x - radius, mMaxFloat, dr, this)
                    canvas.drawCircle(x - radius - r, mMaxFloating, dr, this)
                    canvas.drawCircle(x - radius, mMaxFloating, dr, this)
                    canvas.drawCircle(x - radius + r, mMaxFloating - r, dr, this)
                    // 第二段控制点
                    canvas.drawCircle(x, controlPointY, dr, this)
                    // 第三段，原始点：canvas.drawCircle(x + radius, mMaxFloat, dr, this)
                    canvas.drawCircle(x + radius - r, mMaxFloating - r, dr, this)
                    canvas.drawCircle(x + radius, mMaxFloating, dr, this)
                    canvas.drawCircle(x + radius + r, mMaxFloating, dr, this)

                    canvas.drawCircle(x, y, radius, this)
                }
            })
//            mDividerPath.lineTo(x - radius, mMaxFloat)
            mDividerPath.lineTo(x - radius - r, mMaxFloating)
//            mDividerPath.quadTo(x, controlPointY, x + radius, mMaxFloat)
            mDividerPath.quadTo(x - radius, mMaxFloating, x - radius + r, mMaxFloating - r)
            mDividerPath.quadTo(x, controlPointY, x + radius - r, mMaxFloating - r)
            mDividerPath.quadTo(x + radius, mMaxFloating, x + radius + r, mMaxFloating)
        }
        mDividerPath.lineTo(width + mDividerStrokeWidth, mMaxFloating)
        mDividerPath.lineTo(width + mDividerStrokeWidth, height + mDividerStrokeWidth)
        mDividerPath.lineTo(-mDividerStrokeWidth, height + mDividerStrokeWidth)
        mDividerPath.close()

        canvas.drawPath(mDividerPath, mDividerPaint)

        mDebugDraws?.forEach { it.run() }

        canvas.clipPath(mDividerPath)
    }

    /**
     * 获取在该 View 节点中的子 View 在该 View 中的 X 位置
     *
     * @param view 要获取的子 View
     */
    private fun getChildRealX(view: View): Float {
        if (view == this) return this.x
        var x = view.x
        val parent = view.parent
        if (parent is ViewGroup && parent != this) {
            x += getChildRealX(parent)
        }
        return x
    }

    /**
     * 获取在该 View 节点中的子 View 在该 View 中的 Y 位置
     *
     * @param view 要获取的子 View
     */
    private fun getChildRealY(view: View): Float {
        if (view == this) return this.y
        var y = view.y
        val parent = view.parent
        if (parent is ViewGroup && parent != this) {
            y += getChildRealY(parent)
        }
        return y
    }

    /**
     * 画笔等参数初始化
     */
    private fun init() {
        mDividerPaint.apply {
            flags = Paint.ANTI_ALIAS_FLAG
            isAntiAlias = true
            color = mDividerColor
            strokeWidth = mDividerStrokeWidth
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        compatibility()
        // 使其会绘制自己
        setWillNotDraw(false)
        // 锁定横向排列
        orientation = HORIZONTAL
    }

    /**
     * 进行兼容性设置
     */
    private fun compatibility() {
        // 设置为 LAYER_TYPE_SOFTWARE ，否则部分手机绘制时会闪烁
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        // 设置 LAYER_TYPE_SOFTWARE 后，需要设置一点透明度，否则部分手机会有显示问题
        alpha = 0.999f
    }
}