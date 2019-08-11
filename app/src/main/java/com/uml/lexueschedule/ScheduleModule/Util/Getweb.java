package com.uml.lexueschedule.ScheduleModule.Util;

import com.uml.lexueschedule.ScheduleModule.Data.Model.Course;
import com.uml.lexueschedule.ScheduleModule.Data.Model.Schedule;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Iterator;

public class Getweb {

    private static Schedule mySchedule=Schedule.getInstance();

    public static void get(String html)
    {

        Getweb t=new Getweb();//实例化
        Document doc= Jsoup.parse(html);  ;//双引号里面填写网址

        Elements elements1=doc.select("#kblist_table");
        Elements elements2=elements1.select("tbody");//获取希望的kblist_table模块

        for(int i=1;i<8;i++) {//对每周进行分析
            Elements elements3 = elements2.select("#xq_"+i);
            Elements elements4 = elements3.select("tr");
            Iterator it = elements4.iterator();
            Element element = (Element) it.next();
            while (it.hasNext()) {//对每个课程进行分析
                element = (Element) it.next();
                Elements elements5 = element.getAllElements();
                String time = elements5.select("td").get(0).text();
                int starttime = Integer.valueOf(time.substring(0, time.indexOf('-')));
                int endtime = Integer.valueOf(time.substring(time.indexOf('-') + 1));
                Elements elements6 = elements5.select("font");
                String title = elements6.get(0).text();
                String week = elements6.get(1).text();
                int startweek = Integer.valueOf(week.substring(week.indexOf('：') + 1, week.indexOf('-')));
                int endweek = Integer.valueOf(week.substring(week.indexOf('-') + 1, week.lastIndexOf('周')));
                String address = elements6.get(2).text();
                String t_teacher = elements6.get(3).text();
                String teacher = t_teacher.substring(t_teacher.indexOf('：') + 1);
                //将得到的课程加入Schedule
                Course course = new Course(i, starttime, endtime, title, startweek, endweek, address, teacher);
                mySchedule.addcourse(course);
            }
        }
    }
}