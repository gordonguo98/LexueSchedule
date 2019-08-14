package com.uml.lexueschedule.ScheduleModule.Util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.uml.lexueschedule.ScheduleModule.Data.Model.Course;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Editcourse {
    public static void edit(final Activity activity, final Course oldcourse, final Course newcourse)
    {
       ///修改云端数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
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
                    String modifystring="{\"email\":\""+userId+"\",\"lesson_id\":"+oldcourse.getLessonID()+",";
                    modifystring+="\"course_name\""+":"+"\""+newcourse.getTitle()+"\""+","
                            +"\"day_of_week\""+":"+newcourse.getWeekday()+","
                            +"\"week_num\""+":"+"\""+newcourse.getStartweek()+"-"+newcourse.getEndweek()+"\""+","
                            +"\"day_slot\""+":"+"\""+newcourse.getStarttime()+"-"+newcourse.getEndtime()+"\""+","
                            +"\"teacher\""+":"+"\""+newcourse.getTeacher()+"\""+","
                            +"\"classroom\""+":"+"\""+newcourse.getAddress()+"\""
                            +"}";
                    Log.e("tag",modifystring);
                    HttpURLConnection connection=null;
                    BufferedReader reader = null;
                    URL url= new URL("http://212.64.92.236:20000/api/v1/lesson/");
                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("PUT");
                    Log.e("tag","yes1");
                    connection.setConnectTimeout(8000);
                    connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                    connection.setReadTimeout(8000);
                    connection.setDoOutput(true);
                    DataOutputStream out=new DataOutputStream(connection.getOutputStream());
                    out.write(modifystring.getBytes("UTF-8"));//否则会出现中文乱码
                    int responseCode = connection.getResponseCode();//获得连接的状态码
                    if(responseCode == 200){//200表示连接服务器成功，且获得正确响应
                        Log.e("tag","连接服务器成功");
                    }else{
                        Log.e("tag","连接服务器失败"+responseCode);
                    }

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
                    JSONObject jsoncourse=jsonObject.getJSONObject("course");
                    int course_id=jsoncourse.getInt("course_id");
                    oldcourse.setCourseID(course_id);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
        //修改本地course
        oldcourse.setWeekday(newcourse.getWeekday());
        oldcourse.setStarttime(newcourse.getStarttime());
        oldcourse.setEndtime(newcourse.getEndtime());
        oldcourse.setTitle(newcourse.getTitle());
        oldcourse.setStartweek(newcourse.getStartweek());
        oldcourse.setEndweek(newcourse.getEndweek());
        oldcourse.setAddress(newcourse.getAddress());
        oldcourse.setTeacher(newcourse.getTeacher());
    }

}
