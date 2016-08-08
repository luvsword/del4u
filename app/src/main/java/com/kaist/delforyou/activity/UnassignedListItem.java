package com.kaist.delforyou.activity;

/**
 * Created by birmjin.in on 2016. 8. 9..
 */
public class UnassignedListItem {
    private String date;
    private String dayOfWeek;
    private String itemDescription;
    private String pickupBuilding;
    private String shippingBuilding;

    public UnassignedListItem(String date, String dayOfWeek, String itemDescription, String pickup, String shippping) {
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.itemDescription = itemDescription;
        this.pickupBuilding = pickup;
        this.shippingBuilding = shippping;
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
    public String getPickupBuilding(){
        return this.pickupBuilding;
    }
    public String getShippingBuilding() {return this.shippingBuilding;}
}
