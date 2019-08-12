//孙瑞洲
package com.uml.lexueschedule.ScheduleModule.Data.Model;

import android.util.Log;

import java.io.Serializable;


public class Course implements Serializable {
    private int weekday;
    private int starttime;
    private int endtime;
    private String title;
    private int startweek;
    private int endweek;
    private String address;
    private String teacher;
    private int lessonID;
    private int courseID;
    public Course(){}
    public Course(int wday,int stime,int etime,String ti,int sweek,int eweek,String a,String te,int courseId)
    {
        weekday=wday;
        starttime=stime;
        endtime=etime;
        title=ti;
        startweek=sweek;
        endweek=eweek;
        address=a;
        teacher=te;
        lessonID=-1;
        courseID=courseId;
    }
    public int getCourseID(){return courseID;}
    public void setCourseID(int id){courseID=id;}
    public int getLessonID(){return lessonID;}
    public int getWeekday()
    {
        return weekday;
    }
    public int getStarttime()
    {
        return starttime;
    }
    public int getEndtime()
    {
        return endtime;
    }
    public String getTitle()
    {
        return title;
    }
    public int getStartweek()
    {
        return startweek;
    }
    public int getEndweek()
    {
        return endweek;
    }
    public String getAddress()
    {
        return address;
    }
    public String getTeacher()
    {
        return teacher;
    }
    public void setLessonID(int id){lessonID=id;}
    public void setWeekday(int weekday){this.weekday=weekday;}
    public void setStarttime(int time){starttime=time;}
    public void setEndtime(int time){endtime=time;}
    public void setTitle(String title){this.title=title;}
    public void setStartweek(int start){startweek=start;}
    public void setEndweek(int end){endweek=end;}
    public void setAddress(String address){this.address=address;}
    public void setTeacher(String teacher){this.teacher=teacher;}
    public void print()
    {
        Log.e("tag",weekday+" "+starttime+" "+endtime+" "+title+" "+startweek+" "+endweek+" "+address+" "+teacher);
    }


}
