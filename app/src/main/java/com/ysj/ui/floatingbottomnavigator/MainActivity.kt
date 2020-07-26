package com.ysj.ui.floatingbottomnavigator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ysj.ui.floatingbottomnavigation.FloatingBottomNavigation

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: FloatingBottomNavigation
    private lateinit var v1: View
    private lateinit var v2: View
    private lateinit var v3: View

    private var oldView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNav = findViewById(R.id.bottomNav)
        v1 = findViewById(R.id.view1)
        v2 = findViewById(R.id.view2)
        v3 = findViewById(R.id.view3)
        bottomNav.apply {
            post {
                bindView(v1)
                bindView(v2)
                bindView(v3)
                setDebugEnable(true)
                test(v1)
            }
        }
    }

    fun test(view: View) {
        val id = view.id
        oldView?.apply {
            // 先还原老的
            animate().setDuration(300)
                .scaleX(scaleX / 1.2f)
                .scaleY(scaleY / 1.2f)
                .y(y + 24)
                .start()
        }

        // 新的
        view.animate().setDuration(300)
            .scaleX(view.scaleX * 1.2f)
            .scaleY(view.scaleY * 1.2f)
            .y(view.y - 24)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    oldView = view
                }
            })
            .start()
    }
}