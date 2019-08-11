package com.uml.lexueschedule.ScheduleModule.Util;

import android.util.Log;

import com.uml.lexueschedule.ScheduleModule.Data.Model.Course;
import com.uml.lexueschedule.ScheduleModule.Data.Model.Schedule;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.MalformedURLException;
import java.nio.charset.Charset;

public class Getdata {
    private static Schedule mySchedule=Schedule.getInstance();
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
    public static void getdata() throws MalformedURLException, IOException
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                Log.e("tag","yes");
                try {
                    is = new URL("http://212.64.92.236:20000/api/v1/lesson/?email=1127125637@qq.com").openStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("tag","yes0");
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String html = null;
                try {
                    html = readAll(rd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("tag",html);
                try {
                   JSONObject jsonObject=new JSONObject(html);
                   int lessons_num=jsonObject.getInt("lessons_num");
                   for(int i=1;i<lessons_num+1;i++)
                   {
                       JSONObject lesson=new JSONObject(jsonObject.getString("lesson_"+i));
                       int weekday=lesson.getInt("day_of_week");
                       String time=lesson.getString("day_slot");
                       int starttime = Integer.valueOf(time.substring(0, time.indexOf('-')));
                       int endtime = Integer.valueOf(time.substring(time.indexOf('-') + 1));
                       String title=lesson.getString("course_name");
                       String week=lesson.getString("week_num");
                       int startweek=Integer.valueOf(week.substring(0, week.indexOf('-')));
                       int endweek=Integer.valueOf(week.substring(week.indexOf('-') + 1));
                       String adderss=lesson.getString("classroom");
                       String teacher=lesson.getString("teacher");
                       int id=lesson.getInt("lesson_id");
                       Course course=new Course(weekday,starttime,endtime,title,startweek,endweek,adderss,teacher);
                       course.setCourseId(id);
                       mySchedule.addcourse(course);
                   }
                   Log.e("tag","code="+jsonObject.getString("code"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}