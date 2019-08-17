package com.uml.lexueschedule.View.Fragment;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.uml.lexueschedule.R;
import com.uml.lexueschedule.Data.Model.MySubject;
import com.uml.lexueschedule.Data.Model.SubjectRepertory;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.view.WeekView;

import java.util.List;

/*
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;*/

/**
 * Created by Liu ZhuangFei on 2018/12/27.
 */
public class WakeupFragment extends Fragment {

    //控件
    TimetableView mTimetableView;
    WeekView mWeekView;
    List<MySubject> mySubjects;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_wakeup,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        mySubjects = SubjectRepertory.loadDefaultSubjects2();
        mySubjects.addAll(SubjectRepertory.loadDefaultSubjects());
        initTimetableView(v);
    }

    /**
     * 初始化课程控件
     */
    private void initTimetableView(View v) {
        //获取控件
        mWeekView = v.findViewById(R.id.id_weekview);
        mTimetableView = v.findViewById(R.id.id_timetableView);

        //设置周次选择属性
        mWeekView.source(mySubjects)
                .curWeek(1)
                .callback(new IWeekView.OnWeekItemClickedListener() {
                    @Override
                    public void onWeekClicked(int week) {
                        int cur = mTimetableView.curWeek();
                        //更新切换后的日期，从当前周cur->切换的周week
                        mTimetableView.onDateBuildListener()
                                .onUpdateDate(cur, week);
                        mTimetableView.changeWeekOnly(week);
                    }
                })
                .isShow(false)//设置隐藏，默认显示
                .showView();

        mTimetableView.source(mySubjects)
                .curWeek(1)
                .curTerm("大三下学期")
                .maxSlideItem(10)
                .monthWidthDp(30)
                //旗标布局点击监听
                .callback(new ISchedule.OnFlaglayoutClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onFlaglayoutClick(int day, int start) {
                        mTimetableView.hideFlaglayout();
                        Toast.makeText(getContext(),
                                "点击了旗标:周" + (day + 1) + ",第" + start + "节",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .showView();
    }
}
