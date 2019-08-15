package com.uml.lexueschedule.ScheduleModule.Data.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDatabasehelper extends SQLiteOpenHelper {
    public static final String CREATE_COURSE="create table Course("
            +"lesson_id integer primary key,"
            +"course_id integer,"
            +"day_of_week integer,"
            +"starttime integer,"
            +"endtime integer,"
            +"course_name text,"
            +"startweek integer,"
            +"endweek integer,"
            +"address text,"
            +"teacher text)";
    private Context mcontext;
    public MyDatabasehelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version)
    {
        super(context,name,factory,version);
        mcontext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_COURSE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
