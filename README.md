## FloatingBottomNavigation

这个一个浮动溢出效果的底部导航控件

### 效果图：

|debug-off|                           debug-on                           |
|:---:|:---:|
|![debug-off](https://github.com/Ysj001/FloatingBottomNavigator/blob/master/screenshots/1.gif)|![debug-on](https://github.com/Ysj001/FloatingBottomNavigator/blob/master/screenshots/2.gif)|
### 功能描述

- 能够自动识别控件包裹并绑定的 View 的大小
- 继承自 LinearLayout 便于布局和扩展
- 可开启 debug 视图，便于开发中的对效果的调整
- 支持 android:background 属性，会自动对背景进行裁剪
- 支持设置分割线的颜色和粗细
- 支持设置可浮动区域的大小

### 如何使用

|布局属性|功能|
|:---:|:---:|
|maxFloating|设置可浮动区域的大小|
|dividerColor|设置分割线的颜色|
|dividerStrokeWidth|设置分割线的粗细|

#### 例子

1. 布局中

   ```xml
   <com.ysj.ui.floatingbottomnavigation.FloatingBottomNavigation
           android:id="@+id/bottomNav"
           android:layout_width="match_parent"
           android:layout_height="68dp"
           android:background="@mipmap/bg_bottom"
           android:gravity="center_vertical"
           app:dividerColor="#4CAF50"
           app:dividerStrokeWidth="3dp"
           app:maxFloating="16dp">
   
           <FrameLayout
               android:layout_width="0dp"
               android:layout_height="match_parent"
               android:layout_weight="1">
   
               <View
                   android:id="@+id/view1"
                   android:layout_width="28dp"
                   android:layout_height="28dp"
                   android:layout_gravity="center_horizontal|bottom"
                   android:layout_marginBottom="18dp"
                   android:background="@drawable/cc"
                   android:onClick="onNavClick" />
   
               <TextView
                   android:layout_width="wrap_content"
                   android:layout_height="wrap_content"
                   android:layout_gravity="center_horizontal|bottom"
                   android:text="PAGE A"
                   android:textColor="#fff" />
           </FrameLayout>
   
   
           <FrameLayout
               android:layout_width="0dp"
               android:layout_height="match_parent"
               android:layout_weight="1">
   
               <View
                   android:id="@+id/view2"
                   android:layout_width="28dp"
                   android:layout_height="28dp"
                   android:layout_gravity="center_horizontal|bottom"
                   android:layout_marginBottom="18dp"
                   android:background="@drawable/cc"
                   android:onClick="onNavClick" />
   
               <TextView
                   android:layout_width="wrap_content"
                   android:layout_height="wrap_content"
                   android:layout_gravity="center_horizontal|bottom"
                   android:text="PAGE B"
                   android:textColor="#fff" />
           </FrameLayout>
   
       </com.ysj.ui.floatingbottomnavigation.FloatingBottomNavigation>
   ```

2. 代码中

   ```java
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
   ```

   



