package com.kaist.delforyou.activity;

/**
 * Created by birmjin.in on 2016. 8. 7..
 */
public class ListItem {
    private String date;
    private String dayOfWeek;
    private String itemDescription;
    private String personInfo;
    private String status;

    public ListItem(String date, String dayOfWeek, String itemDescription, String personInfo, String status) {
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.itemDescription = itemDescription;
        this.personInfo = personInfo;
        this.status = status;
    }

    public String getDate() {
        return this.date;
    }
    public String getDayOfWeek() {
        return this.dayOfWeek;
    }
    public String getItemDescription(){
        return this.itemDescription;
    }
    public String getPersonInfo(){
        return this.personInfo;
    }
    public String getStatus(){
        return this.status;
    }
}
