package com.ysj.ui.floatingbottomnavigator;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ysj.ui.floatingbottomnavigation.FloatingBottomNavigation;

/**
 * Java 版的例子（不维护）
 * <p>
 *
 * @author Ysj
 * Create time: 2020/7/27
 * @see MainActivity
 */
@Deprecated
public class MainActivityJ extends AppCompatActivity {

    FloatingBottomNavigation bottomNav;
    View view1;
    View view2;

    Pair<Float, Float> mRang = new Pair<>(1f, 1.4f);
    View oldView;
    ValueAnimator oldAnim;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.bindView(view1 = findViewById(R.id.view1));
        bottomNav.bindView(view2 = findViewById(R.id.view2));
        bottomNav.post(() -> onNavClick(view1));
    }

    public void onNavClick(View view) {
        if (view.equals(oldView)) return;
        if (oldAnim != null) {
            if (oldAnim.isRunning()) return;
            oldAnim.setDuration(200);
            oldAnim.setInterpolator(new LinearInterpolator());
            oldAnim.reverse();
        }
        float y = view.getY();
        float offset = view.getHeight() * (mRang.second - mRang.first);
        oldAnim = ValueAnimator.ofFloat(mRang.first, mRang.second).setDuration(400);
        oldAnim.setInterpolator(new OvershootInterpolator(3f));
        oldAnim.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            view.setScaleX(value);
            view.setScaleY(value);
            view.setY(y - offset * (value - 1));
        });
        oldAnim.start();
        oldView = view;
    }
}
