package com.kaist.delforyou.activity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by basoc on 2016-08-07.
 */
public class DeliveryItem implements Parcelable{
    public String categoryId;
    public String categoryName;
    public String description;
    public int count;
    public String dimension;
    public String dimensionName;

    public DeliveryItem(Parcel parcel){
        readFromParcel(parcel);
    }
    public DeliveryItem(){
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(categoryId);
        parcel.writeString(categoryName);
        parcel.writeString(description);

        parcel.writeInt(count);

        parcel.writeString(dimension);
        parcel.writeString(dimensionName);
    }

    public void readFromParcel(Parcel parcel){
        categoryId = parcel.readString();
        categoryName = parcel.readString();
        description = parcel.readString();

        count = parcel.readInt();

        dimension = parcel.readString();
        dimensionName = parcel.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DeliveryItem createFromParcel(Parcel in) {
            return new DeliveryItem(in);
        }

        public DeliveryItem[] newArray(int size) {
            return new DeliveryItem[size];
        }
    };
}
