//孙瑞洲 改
package com.uml.lexueschedule.ScheduleModule.Data.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private static Schedule mySchedule =new Schedule();
    public ArrayList<Course> courses;
    private Schedule()
    {
        courses=new ArrayList<Course>();
    }
    public void addcourse(Course course)
    {
        courses.add(course);
    }
    public static Schedule getInstance(){return mySchedule;}

    public static Course getcoursebyid(int lessonID)
    {
        for(int i=0;i<lessonID;i++)
        {
            if(getInstance().courses.get(i).getLessonID()==lessonID)
                return getInstance().courses.get(i);
        }
        return null;
    }

    public List<MySubject> toList()
    {
        List<MySubject> courseslist = new ArrayList<>();
        Log.e("tag",""+courses.size());
        for (int i = 0; i < courses.size(); i++) {
            Course course=courses.get(i);
            String term="2017-2018学年秋";
            String name=course.getTitle();
            String room=course.getAddress();
            String teacher=course.getTeacher();
            List<Integer> weeks=getweekList(course.getStartweek(),course.getEndweek());
            int start=course.getStarttime();
            int step=course.getEndtime()-start+1;
            int day=course.getWeekday();
            MySubject subject=new MySubject(term,name, room, teacher, weeks, start, step, day, -1,null);
            subject.setId(course.getLessonID());
            courseslist.add(subject);
        }
        Log.e("tag","在Schedule中还有"+courseslist.size()+"门课");
        return courseslist;
    }

    private List<Integer> getweekList(int start,int end)
    {
        List<Integer> weeks=new ArrayList<>();
        for(int i=start;i<=end;i++)
        {
            weeks.add(i);
        }
        return weeks;
    }

}
