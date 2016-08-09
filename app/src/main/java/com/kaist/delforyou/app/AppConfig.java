package com.kaist.delforyou.app;

/**
 * Created by luvsword on 2016-07-27.
 */
public class AppConfig {
    public static String HOST ="http://";
    // Server user login url
    public static String URL_LOGIN = HOST + "/login.php";
    // Server user register url
    public static String URL_REGISTER = HOST + "/register.php";
    // Server reservation url
    public static String URL_GETCATINFO = HOST + "/get_company.php";
    public static String URL_GETCATINFO1 = HOST + "/company_info.php";
    public static String URL_SETREVINFO = HOST + "/set_reservation.php";
    public static String URL_GETDELIVERYINFO = HOST + "/get_deliveryinfo.php";
    public static String URL_GETSTATUSINFO = HOST + "/get_statuslog.php";
    public static String URL_GETDELIVERY = HOST + "/delivery_list.php";
    public static String URL_GETDELIVERYJOBS = HOST + "/deliveryJob_list.php";
    public static String URL_GETUNASSIGNED = HOST + "/unassigned_deliveries.php";
    public static String URL_INSERTSTATUSLOG = HOST + "/insert_statuslog.php";
    public static String URL_SETSTATUSINFO = "";
}

