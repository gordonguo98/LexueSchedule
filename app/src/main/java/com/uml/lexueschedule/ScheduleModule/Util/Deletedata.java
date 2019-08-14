package com.uml.lexueschedule.ScheduleModule.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.uml.lexueschedule.ScheduleModule.Data.Model.Course;
import com.uml.lexueschedule.ScheduleModule.Data.Model.Schedule;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Deletedata {
    public static void deleteCourse(final Activity activity, int id)
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
            return;
        }
        //在云端数据库中删除
        final String deletestring="{\"email\":\""+userId+"\",\"lesson_id\":"+id+"}";
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                BufferedReader reader = null;
                try{
                    URL url= new URL("http://212.64.92.236:20000/api/v1/lesson/");
                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("DELETE");
                    connection.setConnectTimeout(8000);
                    connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                    connection.setReadTimeout(8000);
                    connection.setDoOutput(true);
                    DataOutputStream out=new DataOutputStream(connection.getOutputStream());
                    out.write(deletestring.getBytes("UTF-8"));//否则会出现中文乱码
                    int responseCode = connection.getResponseCode();//获得连接的状态码
                    if(responseCode == 200){//200表示连接服务器成功，且获得正确响应
                        Log.e("tag","连接服务器成功");
                    }else{
                        Log.e("tag","连接服务器失败"+responseCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //本地删除
        Schedule mySchedule=Schedule.getInstance();
        for(Course course:mySchedule.courses)
        {
            if(course.getLessonID()==id)
            {
                mySchedule.courses.remove(course);
                break;
            }
        }
    }
}
