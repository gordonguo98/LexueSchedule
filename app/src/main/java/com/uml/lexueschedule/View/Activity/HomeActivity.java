package com.uml.lexueschedule.View.Activity;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uml.lexueschedule.MyApplication;
import com.uml.lexueschedule.R;
import com.uml.lexueschedule.Data.Adapter.ViewPagerAdapter;
import com.uml.lexueschedule.View.Fragment.HomeFragment;
import com.uml.lexueschedule.View.Fragment.NotificationFragment;
import com.uml.lexueschedule.View.Fragment.ScheduleFragment;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * by 郭晓凡
 */

public class HomeActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private BottomNavigationView navView;
    private ViewPagerAdapter viewPagerAdapter;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userId = getIntent().getStringExtra("userId");
        initToolbar();
        initView();

        MyApplication.destroyActivity("MainActivity");
        MyApplication.destroyActivity("AddProfileActivity");
    }

    private void initToolbar(){

        //设置状态栏，工具栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_home_toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        addMiddleTitle(this, "乐学课程表", toolbar);
    }

    public void addMiddleTitle(Context context, CharSequence title, Toolbar toolbar) {

        TextView textView = new TextView(context);
        textView.setText(title);
        textView.setTextColor(getResources().getColor(R.color.textColor));
        textView.setTextSize(20);

        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        toolbar.addView(textView, params);

    }

    /**
     * 初始化控件
     */
    private void initView(){

        viewPager = findViewById(R.id.viewpager);
        navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        //TODO
                        viewPager.setCurrentItem(0);//主页
                        return true;
                    case R.id.navigation_schedule:
                        //TODO
                        viewPager.setCurrentItem(1);//课表
                        return true;
                    case R.id.navigation_notifications:
                        //TODO
                        viewPager.setCurrentItem(2);//通知
                        return true;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                navView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new HomeFragment());
        viewPagerAdapter.addFragment(new ScheduleFragment());
        viewPagerAdapter.addFragment(new NotificationFragment());
        viewPager.setAdapter(viewPagerAdapter);

    }

}
