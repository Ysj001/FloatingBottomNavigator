package com.ysj.ui.floatingbottomnavigator

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private var bottomNavHeight = 0
    }

    private var debugEnable = false

    // 动画范围
    private var mRang = Pair(1f, 1.4f)
    private var oldView: View? = null
    private var oldAnim: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNav.apply {
            if (bottomNavHeight != 0) layoutParams.height = bottomNavHeight
            tvMultiple.text = ("Max multiple: ${mRang.second}")
            tvMaxFloating.text = ("Max Floating: ${getMaxFloating()}(px)")
            tvDividerStrokeWidth.text = ("Divider Stroke Width: ${getDividerStrokeWidth()}(px)")
            tvBottomNavHeight.text = ("Bottom Navigation Height: ${layoutParams.height}(px)")
            bindView(view1)
            bindView(view2)
            bindView(view3)
            post { onNavClick(view1) }
        }
    }

    fun onDebugClick(view: View) {
        debugEnable = !debugEnable
        bottomNav.setDebugEnable(debugEnable)
        (view as TextView).text = if (debugEnable) "debug on" else "debug-off"
    }

    fun onNavClick(view: View) {
        if (view == oldView) return
        // 先还原老的
        oldAnim?.apply {
            if (isRunning) return
            duration = 200
            interpolator = LinearInterpolator()
            reverse()
        }
        // 新点击的
        val y = view.y
        val offset = view.height * (mRang.second - mRang.first)
        ValueAnimator.ofFloat(mRang.first, mRang.second).apply {
            oldAnim = this
            duration = 400
            interpolator = OvershootInterpolator(3f)
            addUpdateListener {
                val value = it.animatedValue as Float
                view.scaleX = value
                view.scaleY = value
                view.y = y - offset * (value - 1)
            }
        }.start()
        oldView = view
    }

    fun onMaxMultiplePlus(view: View) {
        mRang = Pair(mRang.first, mRang.second + 0.1f)
        tvMultiple.text = ("Max multiple: ${mRang.second}")
    }

    fun onMaxSizeMinus(view: View) {
        mRang = Pair(mRang.first, mRang.second - 0.1f)
        tvMultiple.text = ("Max multiple: ${mRang.second}")
    }

    fun onDividerStrokeWidthPlus(view: View) {
        val value = bottomNav.getDividerStrokeWidth()
        bottomNav.setDividerStrokeWidth(value + 1)
        tvDividerStrokeWidth.text = ("Divider Stroke Width: ${value + 1}(px)")
    }

    fun onDividerStrokeWidthMinus(view: View) {
        val value = bottomNav.getDividerStrokeWidth()
        bottomNav.setDividerStrokeWidth(value - 1)
        tvDividerStrokeWidth.text = ("Divider Stroke Width: ${value - 1}(px)")
    }

    fun onMaxFloatingPlus(view: View) {
        val value = bottomNav.getMaxFloating()
        bottomNav.setMaxFloating(value + 1)
        tvMaxFloating.text = ("Max Floating: ${value + 1}(px)")
    }

    fun onMaxFloatingMinus(view: View) {
        val value = bottomNav.getMaxFloating()
        bottomNav.setMaxFloating(value - 1)
        tvMaxFloating.text = ("Max Floating: ${value - 1}(px)")
    }

    fun onBottomNavHeightPlus(view: View) {
        val height = bottomNav.layoutParams.height
        bottomNav.layoutParams.height = height + 10
        bottomNavHeight = bottomNav.layoutParams.height
        recreate()
    }

    fun onBottomNavHeightMinus(view: View) {
        val height = bottomNav.layoutParams.height
        bottomNav.layoutParams.height = height - 10
        bottomNavHeight = bottomNav.layoutParams.height
        recreate()
    }

    fun onResetClick(view: View) {
        bottomNavHeight = 0
        recreate()
    }

}