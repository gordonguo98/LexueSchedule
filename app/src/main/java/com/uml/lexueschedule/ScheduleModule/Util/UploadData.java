//孙瑞洲
package com.uml.lexueschedule.ScheduleModule.Util;
import android.util.Log;

import com.uml.lexueschedule.ScheduleModule.Data.Model.Course;
import com.uml.lexueschedule.ScheduleModule.Data.Model.Schedule;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class UploadData {
    private static Schedule mySchedule=Schedule.getInstance();
    private static String allcoursestoString(ArrayList<Course> courses)
    {

        String str="{\"email\":\"1127125637@qq.com\",\"lesson_list\":[";
        for(int i=0;i<courses.size();i++)
        {
            Course course=courses.get(i);
            str+="{"+"\"course_name\""+":"+"\""+course.getTitle()+"\""+","
                    +"\"year\""+":"+2020+","
                    +"\"semester\""+":"+1+","
                    +"\"day_of_week\""+":"+course.getWeekday()+","
                    +"\"week_num\""+":"+"\""+course.getStartweek()+"-"+course.getEndweek()+"\""+","
                    +"\"day_slot\""+":"+"\""+course.getStarttime()+"-"+course.getEndtime()+"\""+","
                    +"\"teacher\""+":"+"\""+course.getTeacher()+"\""+","
                    +"\"classroom\""+":"+"\""+course.getAddress()+"\""+","
                    +"\"description\""+":"+"\""+""+"\""
                    +"},";
        }
        str=str.substring(0,str.length()-1);
        str+="]}";
        Log.e("tag",str);
        return str;
    }
    //测试用字符串
    //private static String str="email=1127125637@qq.com&lesson_num=1&course_name_1=大学物理&week_num_1=1-12&weekday_1=2&class_num_1=6-7&teacher_1=黄敏兴&classroom_1=A2-302";
    public static void  upload(final ArrayList<Course> courses)
    {
        Log.e("tag","yes");
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("http://212.64.92.236:20000/api/v1/lesson/");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                    connection.setDoOutput(true);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.write(allcoursestoString(courses).getBytes("UTF-8"));//否则会出现中文乱码
                    int responseCode = connection.getResponseCode();//获得连接的状态码
                    if (responseCode == 200) {//200表示连接服务器成功，且获得正确响应
                        Log.e("tag","连接服务器成功");
                    } else {
                        Log.e("tag","连接服务器失败" + responseCode);
                    }
                } catch (Exception e) {
                    Log.e("tag","报告错误");
                    e.printStackTrace();
                }
            }
        }
        ).start();
    }
    public static void upload(Course course)
    {
        ArrayList<Course> courses=new ArrayList<>();
        courses.add(course);
        upload(courses);
    }

}
