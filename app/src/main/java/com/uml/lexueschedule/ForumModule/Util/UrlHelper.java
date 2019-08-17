package com.uml.lexueschedule.ForumModule.Util;

public class UrlHelper {

    public static String SERVER_IP = "http://139.159.225.151:8080/api/v1/";

    //Forum Module
    public static String URL_FOR_GETTING_POST = SERVER_IP + "post/";
    public static String URL_FOR_POSTING_POST = SERVER_IP + "post/";
    public static String URL_FOR_DELETING_POST = SERVER_IP + "post/";
    public static String URL_FOR_GETTING_COMMENT = SERVER_IP + "message/";
    public static String URL_FOR_POSTING_COMMENT = SERVER_IP + "message/";
    public static String URL_FOR_DELETING_COMMENT = SERVER_IP + "message/";
    public static String URL_FOR_POSTING_LIKE = SERVER_IP + "post/";
    public static String URL_FOR_DELETING_LIKE = SERVER_IP + "post/";

    //Main Module
    public static String URL_FOR_LOGIN = SERVER_IP + "auth/";
    public static String URL_FOR_CHECK_CODE= SERVER_IP + "register/";
    public static String URL_FOR_REGISTER = SERVER_IP + "register/";
    public static String URL_FOR_POSTING_INFO = SERVER_IP + "user/";
    public static String URL_FOR_GETTING_INFO = SERVER_IP + "user/";

    //Schedule Module
    public static String URL_FOR_POSTING_SCHEDULE = SERVER_IP + "lesson/";
    public static String URL_FOR_GETTING_SCHEDULE = SERVER_IP + "lesson/";
    public static String URL_FOR_DELETING_SCHEDULE = SERVER_IP + "lesson/";
    public static String URL_FOR_MODIFYING_LESSON = SERVER_IP + "lesson/";
    public static String URL_FOR_DELETING_LESSON = SERVER_IP + "lesson/";
}
