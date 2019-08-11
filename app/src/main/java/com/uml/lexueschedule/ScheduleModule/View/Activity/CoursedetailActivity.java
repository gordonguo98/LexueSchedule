package com.uml.lexueschedule.ScheduleModule.View.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.uml.lexueschedule.ForumModule.View.Activity.ForumActivity;
import com.uml.lexueschedule.R;
import com.uml.lexueschedule.ScheduleModule.Data.Model.Course;
import com.uml.lexueschedule.ScheduleModule.Data.Model.Schedule;
import com.uml.lexueschedule.ScheduleModule.Util.Deletedata;

public class CoursedetailActivity extends AppCompatActivity {


    private int courseID=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coursedetail);

        Intent intent=getIntent();
        courseID=intent.getIntExtra("courseID",-1);

        initToolbar();
        initView();
    }

    private void initToolbar(){
        //设置状态栏，工具栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_coursedetail_toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        addMiddleTitle(this, "课程详情", toolbar);
    }

    private void addMiddleTitle(Context context, CharSequence title, Toolbar toolbar) {

        TextView textView = new TextView(context);
        textView.setText(title);
        textView.setTextColor(getResources().getColor(R.color.textColor));
        textView.setTextSize(20);

        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        toolbar.addView(textView, params);

    }

    //初始化界面
    public void initView()
    {
        Schedule mySchedule=Schedule.getInstance();
        TextView courseName=(TextView)findViewById(R.id.courseNameIndetail);
        TextView classroom=(TextView)findViewById(R.id.classroomInDetail);
        TextView dayOfWeek=(TextView)findViewById(R.id.dayOfWeekInDetail);
        TextView daySlot=(TextView)findViewById(R.id.daySlotInDetail);
        TextView teacher=(TextView)findViewById(R.id.teacherInDetail);
        Course course=new Course();

        for(int i=0;i<mySchedule.courses.size();i++)
        {
            course=mySchedule.courses.get(i);
            if(courseID==course.getCourseId())
                break;
        }

        courseName.setText(course.getTitle());
        classroom.setText("教室： "+course.getAddress());
        dayOfWeek.setText("周数： "+course.getStartweek()+"-"+course.getEndweek());
        String dayofWeek="";
        switch (course.getWeekday())
        {
            case 1:
                dayofWeek="周一";
                break;
            case 2:
                dayofWeek="周二";
                break;
            case 3:
                dayofWeek="周三";
                break;
            case 4:
                dayofWeek="周四";
                break;
            case 5:
                dayofWeek="周五";
                break;
            case 6:
                dayofWeek="周六";
                break;
            case 7:
                dayofWeek="周日";
                break;
            default:
                break;
        }
        daySlot.setText("节数： "+dayofWeek+course.getStarttime()+"-"+course.getEndtime());
        teacher.setText("老师： "+course.getTeacher());

        Button edit=(Button)findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToEdit=new Intent(CoursedetailActivity.this,EditCourseActivity.class);
                intentToEdit.putExtra("CourseID",courseID);
                Log.e("tag","courseID in detailActivity"+courseID);
                startActivity(intentToEdit);
            }
        });

        Button toForum=(Button)findViewById(R.id.toForum);
        toForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CoursedetailActivity.this, ForumActivity.class);
                //假数据测试
                intent.putExtra("courseId", 1);
                intent.putExtra("userId", "1127125637@qq.com");
                startActivity(intent);
            }
        });

        Button delete=(Button)findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Deletedata.deleteCourse(courseID);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
    }
}
