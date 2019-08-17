//孙瑞洲
package com.uml.lexueschedule.Util;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.uml.lexueschedule.Data.Model.Course;
import com.uml.lexueschedule.Data.Model.Schedule;
import com.uml.lexueschedule.View.Activity.ImportscheduleActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * by 孙瑞洲， 郭晓凡
 */

public class UploadData {
    private static Schedule mySchedule=Schedule.getInstance();

    private static String allcoursestoString(final Activity activity, ArrayList<Course> courses)
    {
        String userId = activity.getSharedPreferences("loginlog", Context.MODE_PRIVATE)
                .getString("userId", "");
        if(userId.equals("")){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "账号未登录", Toast.LENGTH_LONG).show();
                }
            });
            return null;
        }
        String str="{\"email\":\""+userId+"\",\"lesson_list\":[";
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
    public static void  upload(final Activity activity, final ArrayList<Course> courses, final boolean isImportAC)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(UrlHelper.URL_FOR_POSTING_SCHEDULE);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                    connection.setDoOutput(true);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    String courseToStr = allcoursestoString(activity, courses);
                    if(courseToStr == null)
                        return;
                    out.write(courseToStr.getBytes("UTF-8"));//否则会出现中文乱码
                    int responseCode = connection.getResponseCode();//获得连接的状态码
                    if (responseCode == 200) {//200表示连接服务器成功，且获得正确响应
                        Log.e("tag","连接服务器成功");
                    } else {
                        Log.e("tag","连接服务器失败" + responseCode);
                    }
                    //修改201908100955
                    //接收反馈
                    InputStream in=connection.getInputStream();
                    reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String line;
                    while((line=reader.readLine())!=null) {
                        response.append(line);
                    }
                    Log.e("tag",response.toString());
                    JSONObject jsonObject=new JSONObject(response.toString());
                    //解析lessonid
                    String strforlist=jsonObject.getString("lesson_id_list").replace("[","").replace("]","");
                    ArrayList<String> lessonIDlist =new ArrayList<String>(Arrays.asList(strforlist.split(",")));
                    Log.e("tag",lessonIDlist.toString());
                    //解析courseid
                    strforlist=jsonObject.getString("course_id_list").replace("[","").replace("]","");
                    ArrayList<String> courseIDlist =new ArrayList<String>(Arrays.asList(strforlist.split(",")));
                    Log.e("tag",courseIDlist.toString());
                    for(int i=0;i<courses.size();i++)
                    {
                        courses.get(i).setLessonID(Integer.valueOf(lessonIDlist.get(i)));
                        courses.get(i).setCourseID(Integer.valueOf(courseIDlist.get(i)));
                    }

                    Log.e("tag", "run: 调用上传");

                    //修改到此结束
                    if(isImportAC)
                        ((ImportscheduleActivity) activity).toBaseFunAC();
                } catch (Exception e) {
                    Log.e("tag","报告错误");
                    e.printStackTrace();
                }
            }
        }
        ).start();
    }
    public static void upload(final Activity activity, Course course)
    {
        ArrayList<Course> courses=new ArrayList<>();
        courses.add(course);
        upload(activity, courses, false);
    }

    public static void updateSchedule(final Activity activity, final ArrayList<Course> courses, final boolean isImportAC){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    String userId = activity.getSharedPreferences("loginlog", Context.MODE_PRIVATE)
                            .getString("userId", "");
                    if(userId.equals("")){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "账号未登录", Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                    URL url = new URL(UrlHelper.URL_FOR_DELETING_SCHEDULE);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("DELETE");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                    connection.setDoOutput(true);
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    String deleteStr = "{\"email\": \"" + userId + "\",\"method\": \"time\",\"year\": 2020,\"semester\": 1}";
                    out.write(deleteStr.getBytes("UTF-8"));//否则会出现中文乱码
                    int responseCode = connection.getResponseCode();//获得连接的状态码
                    if (responseCode == 200) {//200表示连接服务器成功，且获得正确响应
                        Log.e("tag","连接服务器成功");
                    } else {
                        Log.e("tag","连接服务器失败" + responseCode);
                    }
                    //修改201908100955
                    //接收反馈
                    InputStream in=connection.getInputStream();
                    reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String line;
                    while((line=reader.readLine())!=null) {
                        response.append(line);
                    }
                    Log.e("tag",response.toString());
                    JSONObject jsonObject=new JSONObject(response.toString());
                    upload(activity, courses, isImportAC);
                    Log.e("tag", "run: 调用删除课表");
                } catch (Exception e) {
                    Log.e("tag","报告错误");
                    e.printStackTrace();
                }
            }
        }
        ).start();
    }

}
