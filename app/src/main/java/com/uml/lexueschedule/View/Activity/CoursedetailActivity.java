package com.uml.lexueschedule.View.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uml.lexueschedule.R;
import com.uml.lexueschedule.Data.Model.Course;
import com.uml.lexueschedule.Data.Model.Schedule;
import com.uml.lexueschedule.Util.Connect;
import com.uml.lexueschedule.Util.Deletedata;

/**
 * by 孙瑞洲，郭晓凡
 */

public class CoursedetailActivity extends AppCompatActivity {

    private int lessonID=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coursedetail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar)
            actionBar.setDisplayShowTitleEnabled(false);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        Intent intent=getIntent();
        lessonID=intent.getIntExtra("lessonID",-1);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //初始化界面
    public void init()
    {
        final Schedule mySchedule=Schedule.getInstance();
        TextView courseName=(TextView)findViewById(R.id.courseNameIndetail);
        TextView classroom=(TextView)findViewById(R.id.classroomInDetail);
        TextView dayOfWeek=(TextView)findViewById(R.id.dayOfWeekInDetail);
        TextView daySlot=(TextView)findViewById(R.id.daySlotInDetail);
        TextView teacher=(TextView)findViewById(R.id.teacherInDetail);

        Course course=Schedule.getcoursebyid(lessonID);

        courseName.setText(course.getTitle());
        classroom.setText(course.getAddress());
        dayOfWeek.setText(course.getStartweek()+"-"+course.getEndweek());
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
        daySlot.setText(dayofWeek+course.getStarttime()+"-"+course.getEndtime());
        teacher.setText(course.getTeacher());

        FloatingActionButton edit=(FloatingActionButton)findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToEdit=new Intent(CoursedetailActivity.this,EditCourseActivity.class);
                intentToEdit.putExtra("lessonID",lessonID);
                Log.e("tag","lessonID in detailActivity"+lessonID);
                startActivity(intentToEdit);
            }
        });
        final AppCompatActivity ac=this;
        FloatingActionButton delete=(FloatingActionButton)findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断网络是否可用
                if(!Connect.isConnectIsNomarl(ac))
                {
                    Toast.makeText(CoursedetailActivity.this,"网络无连接",Toast.LENGTH_SHORT).show();
                    return;
                }
                Deletedata.deleteCourse(CoursedetailActivity.this, lessonID);
                finish();
            }
        });
        FloatingActionButton toForum=(FloatingActionButton)findViewById(R.id.toForum);
        toForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断网络是否可用
                if(!Connect.isConnectIsNomarl(ac))
                {
                    Toast.makeText(CoursedetailActivity.this,"网络无连接",Toast.LENGTH_SHORT).show();
                    return;
                }
                Course course=Schedule.getcoursebyid(lessonID);
                //论坛部分需要的courseid
                int courseid=course.getCourseID();
                Intent intent = new Intent(CoursedetailActivity.this, ForumActivity.class);
                intent.putExtra("courseId", courseid);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }
}
