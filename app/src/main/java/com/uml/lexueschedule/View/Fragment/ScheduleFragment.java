package com.uml.lexueschedule.View.Fragment;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.uml.lexueschedule.R;
import com.uml.lexueschedule.Data.Database.MyDBhelper;
import com.uml.lexueschedule.Data.Model.Course;
import com.uml.lexueschedule.Data.Model.MySubject;
import com.uml.lexueschedule.Data.Model.SubjectRepertory;
import com.uml.lexueschedule.Util.Connect;
import com.uml.lexueschedule.Util.Getdata;
import com.uml.lexueschedule.Util.UploadData;
import com.uml.lexueschedule.View.Activity.AddCourseActivity;
import com.uml.lexueschedule.View.Activity.CoursedetailActivity;
import com.uml.lexueschedule.View.Activity.OpenEsActivity;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.view.WeekView;

import java.util.List;

/**
 * 重构by 郭晓凡
 *
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment implements View.OnClickListener {

    private View view;

    //控件
    private TimetableView mTimetableView;
    private WeekView mWeekView;

    private Button moreButton;
    private LinearLayout layout;
    private TextView titleTextView;
    private List<MySubject> mySubjects;

    private MyDBhelper dbhelper;

    private FloatingActionButton importSchedule;
    private FloatingActionButton insertDb;
    private FloatingActionButton fromCloud;
    private FloatingActionButton fromLocal;

    //记录切换的周次，不一定是当前周
    int target = -1;

    public ScheduleFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent)
                parent.removeView(view);
        }
        else {
            view = inflater.inflate(R.layout.fragment_schedule, container, false);
            initView(view);
        }
        return view;
    }

    private void initView(View view){
        //从服务器获取课程信息
        try {
            //判断网络是否可用
            if(!Connect.isConnectIsNomarl(getContext())) {
                Toast.makeText(getContext(),"网络无连接, 显示本地课表",Toast.LENGTH_SHORT).show();
                getCoursesFromLocal();
            }else {
                Getdata.getdata(getActivity(), this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        moreButton = view.findViewById(R.id.id_more);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopmenu();
            }
        });
        //给mysubject添加内容
        mySubjects = SubjectRepertory.myloadDefaultSubjects();
        Log.e("tag", "initView: "+mySubjects.size());

        titleTextView = view.findViewById(R.id.id_title);
        layout = view.findViewById(R.id.id_layout);
        layout.setOnClickListener(this);
        initTimetableView(view);

        importSchedule = (FloatingActionButton) view.findViewById(R.id.fab_import);
        insertDb = (FloatingActionButton) view.findViewById(R.id.fab_insert);
        fromCloud = (FloatingActionButton) view.findViewById(R.id.fab_fromcloud);
        fromLocal = (FloatingActionButton) view.findViewById(R.id.fab_fromlocal);

        importSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Connect.isConnectIsNomarl(getContext())){
                    Toast.makeText(getContext(), "无网络连接", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent=new Intent(getActivity(), OpenEsActivity.class);
                startActivity(intent);
            }
        });

        insertDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertIntoLocalDb();
            }
        });

        final ScheduleFragment sf = this;

        fromCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Getdata.getdata(getActivity(), sf);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });

        fromLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCoursesFromLocal();
                mySubjects = SubjectRepertory.myloadDefaultSubjects();
                mWeekView.source(mySubjects).showView();
                mTimetableView.source(mySubjects).updateView();
            }
        });
    }

    public void afterGettingData(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mySubjects = SubjectRepertory.myloadDefaultSubjects();
                mWeekView.source(mySubjects).showView();
                mTimetableView.source(mySubjects).updateView();
            }
        });
    }

    //清空表中数据后再插入
    public void insertIntoLocalDb() {
        Log.e("tag","进入test函数");
        dbhelper=new MyDBhelper(getContext(),"Course.db",null,1);
        SQLiteDatabase db=dbhelper.getWritableDatabase();
        db.execSQL("delete from " + "Course");
        ContentValues values=new ContentValues();
        Log.e("tag","yes0");
        for(int i = 0; i< com.uml.lexueschedule.Data.Model.Schedule.getInstance().courses.size(); i++)
        {
            Course course=
                    com.uml.lexueschedule.Data.Model.Schedule.getInstance().courses.get(i);
            values.put("lesson_id",course.getLessonID());
            Log.e("tag","在内存中lessonid="+course.getLessonID());
            Log.e("tag","在内存中courseid="+course.getCourseID());
            values.put("course_id",course.getCourseID());
            values.put("day_of_week",course.getWeekday());
            values.put("starttime",course.getStarttime());
            values.put("endtime",course.getEndtime());
            values.put("course_name",course.getTitle());
            values.put("startweek",course.getStartweek());
            values.put("endweek",course.getEndweek());
            values.put("address",course.getAddress());
            values.put("teacher",course.getTeacher());
            db.insert("Course",null,values);
            Log.e("tag","课程信息");
            course.print();
        }
        Toast.makeText(getContext(), "保存成功", Toast.LENGTH_LONG).show();
    }

    public void getCoursesFromLocal() {
        dbhelper=new MyDBhelper(getContext(),"Course.db",null,1);
        SQLiteDatabase db=dbhelper.getReadableDatabase();
        Cursor cursor=db.query("Course",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            com.uml.lexueschedule.Data.Model.Schedule.getInstance().courses.clear();
            do{
                int lesson_id=cursor.getInt(cursor.getColumnIndex("lesson_id"));
                int course_id=cursor.getInt(cursor.getColumnIndex("course_id"));
                int day_of_week=cursor.getInt(cursor.getColumnIndex("day_of_week"));
                int starttime=cursor.getInt(cursor.getColumnIndex("starttime"));
                int endtime=cursor.getInt(cursor.getColumnIndex("endtime"));
                String course_name=cursor.getString(cursor.getColumnIndex("course_name"));
                int startweek=cursor.getInt(cursor.getColumnIndex("startweek"));
                int endweek=cursor.getInt(cursor.getColumnIndex("endweek"));
                String address=cursor.getString(cursor.getColumnIndex("address"));
                String teacher=cursor.getString(cursor.getColumnIndex("teacher"));
                Course course=new Course(day_of_week,starttime,endtime,course_name,startweek,endweek,address,teacher,course_id);
                course.setLessonID(lesson_id);
                course.setCourseID(course_id);
                Log.e("tag","课程信息");
                course.print();
                com.uml.lexueschedule.Data.Model.Schedule.getInstance().courses.add(course);
            }while (cursor.moveToNext());
        }
        Toast.makeText(getContext(), "成功读取课表", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        insertIntoLocalDb();
        SystemClock.sleep(1000);
        super.onDestroy();
    }

    /**
     * 初始化课程控件
     */
    private void initTimetableView(View view) {
        //获取控件
        mWeekView = view.findViewById(R.id.id_weekview);
        mTimetableView = view.findViewById(R.id.id_timetableView);

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
                .callback(new IWeekView.OnWeekLeftClickedListener() {
                    @Override
                    public void onWeekLeftClicked() {
                        onWeekLeftLayoutClicked();
                    }
                })
                .isShow(false)//设置隐藏，默认显示
                .showView();

        mTimetableView.source(mySubjects)
                .curWeek(1)
                .curTerm("大三下学期")
                .maxSlideItem(12)
                .monthWidthDp(30)
                //透明度
                //日期栏0.1f、侧边栏0.1f，周次选择栏0.6f
                //透明度范围为0->1，0为全透明，1为不透明
//                .alpha(0.1f, 0.1f, 0.6f)
                .callback(new ISchedule.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, List<Schedule> scheduleList) {
                        display(scheduleList);
                    }
                })
                .callback(new ISchedule.OnItemLongClickListener() {
                    @Override
                    public void onLongClick(View v, int day, int start) {
                        Toast.makeText(getActivity(),
                                "长按:周" + day  + ",第" + start + "节",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .callback(new ISchedule.OnWeekChangedListener() {
                    @Override
                    public void onWeekChanged(int curWeek) {
                        titleTextView.setText("第" + curWeek + "周");
                    }
                })
                //旗标布局点击监听
                .callback(new ISchedule.OnFlaglayoutClickListener() {
                    @Override
                    public void onFlaglayoutClick(int day, int start) {
                        mTimetableView.hideFlaglayout();
                        Intent intent=new Intent(getActivity(), AddCourseActivity.class);
                        startActivity(intent);
                    }
                })
                .showView();
    }

    /**
     * 更新一下，防止因程序在后台时间过长（超过一天）而导致的日期或高亮不准确问题。
     */
    @Override
    public void onStart() {
        super.onStart();
        mTimetableView.onDateBuildListener()
                .onHighLight();
    }

    @Override
    public void onResume() {
        super.onResume();
        mySubjects = SubjectRepertory.myloadDefaultSubjects();
        mWeekView.source(mySubjects).showView();
        mTimetableView.source(mySubjects).updateView();
    }

    /**
     * 周次选择布局的左侧被点击时回调<br/>
     * 对话框修改当前周次
     */
    protected void onWeekLeftLayoutClicked() {
        final String items[] = new String[20];
        int itemCount = mWeekView.itemCount();
        for (int i = 0; i < itemCount; i++) {
            items[i] = "第" + (i + 1) + "周";
        }
        target = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("设置当前周");
        builder.setSingleChoiceItems(items, mTimetableView.curWeek() - 1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        target = i;
                    }
                });
        builder.setPositiveButton("设置为当前周", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (target != -1) {
                    mWeekView.curWeek(target + 1).updateView();
                    mTimetableView.changeWeekForce(target + 1);
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    /**
     * 点击课程触发
     * 对于所有在这一个时间段上课的课程
     * @param beans
     */
    protected void display(List<Schedule> beans) {
        String str = "";
        for (Schedule bean : beans) {
            List<Integer> weeklist=bean.getWeekList();
            for(int count:weeklist)
            {
                if(count==mTimetableView.curWeek())
                {
                    Intent intent=new Intent(getContext(), CoursedetailActivity.class);
                    intent.putExtra("lessonID",bean.getCourseId());
                    Log.e("tag","lessonID in baseactivity"+bean.getCourseId());
                    startActivity(intent);
                }
            }
        }
    }

    /**
     * 显示弹出菜单
     */
    public void showPopmenu() {
        PopupMenu popup = new PopupMenu(getContext(), moreButton);
        popup.getMenuInflater().inflate(R.menu.popmenu_base_func, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.top1:
                        addSubject();
                        break;
                    case R.id.top2:
                        deleteSubject();
                        break;

                    case R.id.top4:
                        hideNonThisWeek();
                        break;
                    case R.id.top5:
                        showNonThisWeek();
                        break;
                    case R.id.top6:
                        setMaxItem(8);
                        break;
                    case R.id.top7:
                        setMaxItem(10);
                        break;
                    case R.id.top8:
                        setMaxItem(12);
                        break;
                    case R.id.top9:
                        showTime();
                        break;
                    case R.id.top10:
                        hideTime();
                        break;
                    case R.id.top11:
                        showWeekView();
                        break;
                    case R.id.top12:
                        hideWeekView();
                        break;
                    case R.id.top13:
                        setMonthWidth();
                        break;
                    case R.id.top16:
                        resetMonthWidth();
                        break;
                    case R.id.top14:
                        hideWeekends();
                        break;
                    case R.id.top15:
                        showWeekends();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        popup.show();
    }
    //上传到数据库
    protected void upload() {
        com.uml.lexueschedule.Data.Model.Schedule mySchedule=
                com.uml.lexueschedule.Data.Model.Schedule.getInstance();
        UploadData.upload(getActivity(), mySchedule.courses, false, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_layout:
                //如果周次选择已经显示了，那么将它隐藏，更新课程、日期
                //否则，显示
                if (mWeekView.isShowing()) {
                    mWeekView.isShow(false);
                    titleTextView.setTextColor(getResources().getColor(R.color.app_course_textcolor_blue));
                    int cur = mTimetableView.curWeek();
                    mTimetableView.onDateBuildListener()
                            .onUpdateDate(cur, cur);
                    mTimetableView.changeWeekOnly(cur);
                } else {
                    mWeekView.isShow(true);
                    titleTextView.setTextColor(getResources().getColor(R.color.app_red));
                }
                break;
        }
    }

    /**
     * 删除课程
     * 内部使用集合维护课程数据，操作集合的方法来操作它即可
     * 最后更新一下视图（全局更新）
     */
    protected void deleteSubject() {
        int size = mTimetableView.dataSource().size();
        int pos = (int) (Math.random() * size);
        if (size > 0) {
            mTimetableView.dataSource().remove(pos);
            mTimetableView.updateView();
        }
    }

    /**
     * 添加课程
     * 内部使用集合维护课程数据，操作集合的方法来操作它即可
     * 最后更新一下视图（全局更新）
     */
    protected void addSubject() {
        List<Schedule> dataSource = mTimetableView.dataSource();
        int size = dataSource.size();
        if (size > 0) {
            Schedule schedule = dataSource.get(0);
            dataSource.add(schedule);
            mTimetableView.updateView();
        }
    }

    /**
     * 隐藏非本周课程
     * 修改了内容的显示，所以必须更新全部（性能不高）
     * 建议：在初始化时设置该属性
     * <p>
     * updateView()被调用后，会重新构建课程，课程会回到当前周
     */
    protected void hideNonThisWeek() {
        mTimetableView.isShowNotCurWeek(false).updateView();
    }

    /**
     * 显示非本周课程
     * 修改了内容的显示，所以必须更新全部（性能不高）
     * 建议：在初始化时设置该属性
     */
    protected void showNonThisWeek() {
        mTimetableView.isShowNotCurWeek(true).updateView();
    }

    /**
     * 设置侧边栏最大节次，只影响侧边栏的绘制，对课程内容无影响
     *
     * @param num
     */
    protected void setMaxItem(int num) {
        mTimetableView.maxSlideItem(num).updateSlideView();
    }

    /**
     * 显示时间
     * 设置侧边栏构建监听，TimeSlideAdapter是控件实现的可显示时间的侧边栏
     */
    protected void showTime() {
        String[] times = new String[]{
                "8:00", "9:00", "10:10", "11:00",
                "15:00", "16:00", "17:00", "18:00",
                "19:30", "20:30", "21:30", "22:30"
        };
        OnSlideBuildAdapter listener = (OnSlideBuildAdapter) mTimetableView.onSlideBuildListener();
        listener.setTimes(times)
                .setTimeTextColor(Color.BLACK);
        mTimetableView.updateSlideView();
    }

    /**
     * 隐藏时间
     * 将侧边栏监听置Null后，会默认使用默认的构建方法，即不显示时间
     * 只修改了侧边栏的属性，所以只更新侧边栏即可（性能高），没有必要更新全部（性能低）
     */
    protected void hideTime() {
        mTimetableView.callback((ISchedule.OnSlideBuildListener) null);
        mTimetableView.updateSlideView();
    }

    /**
     * 显示WeekView
     */
    protected void showWeekView() {
        mWeekView.isShow(true);
    }

    /**
     * 隐藏WeekView
     */
    protected void hideWeekView() {
        mWeekView.isShow(false);
    }

    /**
     * 设置月份宽度
     */
    private void setMonthWidth() {
        mTimetableView.monthWidthDp(50).updateView();
    }

    /**
     * 设置月份宽度,默认40dp
     */
    private void resetMonthWidth() {
        mTimetableView.monthWidthDp(40).updateView();
    }

    /**
     * 隐藏周末
     */
    private void hideWeekends() {
        mTimetableView.isShowWeekends(false).updateView();
    }

    /**
     * 显示周末
     */
    private void showWeekends() {
        mTimetableView.isShowWeekends(true).updateView();
    }
}
