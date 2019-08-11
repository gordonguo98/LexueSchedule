package com.uml.lexueschedule.ScheduleModule.View;

import android.app.Application;


/**
 * Created by Liu ZhuangFei on 2018/7/28.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //内存泄漏检测
//        LeakCanary.install(this);
    }
}
