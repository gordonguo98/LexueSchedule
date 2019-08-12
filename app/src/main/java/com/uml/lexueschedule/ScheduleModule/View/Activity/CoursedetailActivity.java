package com.uml.lexueschedule.ScheduleModule.View.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.uml.lexueschedule.ForumModule.View.Activity.ForumActivity;
import com.uml.lexueschedule.R;
import com.uml.lexueschedule.ScheduleModule.Data.Model.Course;
import com.uml.lexueschedule.ScheduleModule.Data.Model.Schedule;
import com.uml.lexueschedule.ScheduleModule.Util.Deletedata;

public class CoursedetailActivity extends AppCompatActivity {

    private String userId;
    private int lessonID=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coursedetail);

        Intent intent=getIntent();
        lessonID=intent.getIntExtra("lessonID",-1);
        userId = intent.getStringExtra("userId");

        init();
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
                intentToEdit.putExtra("lessonID",lessonID);
                Log.e("tag","lessonID in detailActivity"+lessonID);
                startActivity(intentToEdit);
            }
        });
        Button delete=(Button)findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Deletedata.deleteCourse(lessonID);
                finish();
            }
        });
        Button toForum=(Button)findViewById(R.id.toForum);
        toForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Course course=Schedule.getcoursebyid(lessonID);
                //论坛部分需要的courseid
                int courseid=course.getCourseID();
                Toast.makeText(CoursedetailActivity.this, "courseId is "+courseid+"\n"
                        +"userId is "+userId, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CoursedetailActivity.this, ForumActivity.class);
                intent.putExtra("courseId", courseid);
                intent.putExtra("userId", userId);
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
