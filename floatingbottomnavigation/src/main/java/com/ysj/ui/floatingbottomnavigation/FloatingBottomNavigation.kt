package com.ysj.ui.floatingbottomnavigation

import android.content.Context
import android.graphics.*
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

    private val TAG = "BottomNavigation"

    // =================== 绘制参数 =====================

    // 分割线颜色
    private val mDividerColor = Color.GREEN

    // 分割线到 view 的间距
    private var mDividerPadding: Float

    // 分割线高度
    private var mDividerHeight: Float

    // 最大浮动
    private val mMaxFloat: Float

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
        compatibility()
        // 使其会绘制自己
        setWillNotDraw(false)
        // 加载自定义属性
        context.obtainStyledAttributes(attrs, R.styleable.FloatingBottomNavigation).apply {
            mDividerHeight = getDimension(R.styleable.FloatingBottomNavigation_dividerHeight, 0f)
            mDividerPadding = getDimension(R.styleable.FloatingBottomNavigation_dividerPadding, 0f)
            mMaxFloat = getDimension(R.styleable.FloatingBottomNavigation_maxFloat, 0f)
        }.recycle()
        init()
    }

    override fun draw(canvas: Canvas) {
        // 因为需要裁切 view 的背景，因此该调用需要在 super.draw(canvas) 前
        drawDivider(canvas)
        super.draw(canvas)
        mDebugDraws?.forEach { it.run() }
//        drawDivider2(canvas)
    }

    /**
     * 绑定一个 View 为来响应浮动效果
     */
    fun bindView(view: View?) {
        view?.apply { mBindViews.add(this) }
    }

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
    }

    private fun drawDivider(canvas: Canvas) {
        mDebugDraws?.clear()

        mDividerPath.reset()
        mDividerPath.moveTo(-mDividerHeight, mMaxFloat)
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
            val radius = sqrt((childRealWidth / 2 * childRealWidth / 2
                    + childRealHeight / 2 * childRealHeight / 2).toDouble()).toFloat()
            // 控制点 Y 坐标
            val controlPointY = if (y - radius >= mMaxFloat) mMaxFloat
            else y - radius - abs(y - radius - mMaxFloat)

            val p = (mMaxFloat - controlPointY) / radius
            val r = if (p <= 0) 0f else (radius - childRealWidth / 2) * p / 2

            mDebugDraws?.add(Runnable {
                mDebugPaint?.apply {
                    val dr = 8f
                    // 第一段，原始点：canvas.drawCircle(x - radius, mMaxFloat, dr, this)
                    canvas.drawCircle(x - radius - r, mMaxFloat, dr, this)
                    canvas.drawCircle(x - radius + r * 2, mMaxFloat - r, dr, this)
                    canvas.drawCircle(x - radius + r, mMaxFloat, dr, this)
                    // 第二段控制点
                    canvas.drawCircle(x, controlPointY, dr, this)
                    // 第三段，原始点：canvas.drawCircle(x + radius, mMaxFloat, dr, this)
                    canvas.drawCircle(x + radius - r * 2, mMaxFloat - r, dr, this)
                    canvas.drawCircle(x + radius - r, mMaxFloat, dr, this)
                    canvas.drawCircle(x + radius + r, mMaxFloat, dr, this)

                    canvas.drawCircle(x, y, radius, this)
                }
            })
//            mDividerPath.lineTo(x - radius, mMaxFloat)
            mDividerPath.lineTo(x - radius - r, mMaxFloat)
//            mDividerPath.quadTo(x, controlPointY, x + radius, mMaxFloat)
            mDividerPath.quadTo(x - radius + r, mMaxFloat, x - radius + r * 2, mMaxFloat - r)
            mDividerPath.quadTo(x, controlPointY, x + radius - r * 2, mMaxFloat - r)
            mDividerPath.quadTo(x + radius - r, mMaxFloat, x + radius + r, mMaxFloat)

        }
        mDividerPath.lineTo(width + mDividerHeight, mMaxFloat)
        mDividerPath.lineTo(width + mDividerHeight, height + mDividerHeight)
        mDividerPath.lineTo(-mDividerHeight, height + mDividerHeight)
        mDividerPath.close()

        canvas.drawPath(mDividerPath, mDividerPaint)

        mDebugDraws?.forEach { it.run() }

        canvas.clipPath(mDividerPath)
    }

    @Deprecated("一种不完美的实现思路，保留下来可以看看", ReplaceWith("drawDivider()"))
    private fun drawDivider2(canvas: Canvas) {
        val mFloatingPath = Path()
        mFloatingPath.reset()
        mDividerPath.reset()

        mDividerPath.moveTo(-mDividerHeight, mMaxFloat)
        mDividerPath.lineTo(width + mDividerHeight, mMaxFloat)
        mDividerPath.lineTo(width + mDividerHeight, height + mDividerHeight)
        mDividerPath.lineTo(-mDividerHeight, height + mDividerHeight)
        mDividerPath.close()

        mBindViews.forEach {
            val childRealX = getChildRealX(it)
            val childRealY = getChildRealY(it)
            // 缩放后的宽高
            val childRealWidth = it.width * it.scaleX
            val childRealHeight = it.height * it.scaleY
            // 中心坐标
            val x = childRealX + it.width / 2
            val y = childRealY + it.height / 2
            // 外圆半径
            val radius = sqrt((childRealWidth / 2 * childRealWidth / 2
                    + childRealHeight / 2 * childRealHeight / 2).toDouble()).toFloat()

            mFloatingPath.moveTo(childRealX, childRealY + it.height / 2)
            mFloatingPath.addCircle(x, y, radius, Path.Direction.CW)
            mFloatingPath.moveTo(childRealX + it.width, mMaxFloat)
        }

        canvas.save()
        canvas.clipPath(mDividerPath, Region.Op.DIFFERENCE)
        canvas.clipPath(mFloatingPath, Region.Op.DIFFERENCE)
        canvas.drawPath(mDividerPath, mDividerPaint)
        canvas.drawPath(mFloatingPath, mDividerPaint)
        canvas.restore()
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
            strokeWidth = mDividerHeight
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
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