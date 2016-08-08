package com.kaist.delforyou.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kaist.delforyou.R;

import java.util.ArrayList;

/**
 * Created by birmjin.in on 2016. 8. 9..
 */
public class UnassignedListViewAdapter extends BaseAdapter{
    private ArrayList<UnassignedListItem> itemList = new ArrayList<>();

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.unassigned_item, parent, false);
        }

        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView dayOfWeek = (TextView) convertView.findViewById(R.id.dayOfWeek);
        TextView itemDescription = (TextView) convertView.findViewById(R.id.itemDescription);
        TextView pickupBuilding = (TextView) convertView.findViewById(R.id.pickupBuilding);
        TextView shippingBuilding = (TextView) convertView.findViewById(R.id.shippingBuilding);

        UnassignedListItem listItem = itemList.get(position);

        date.setText(listItem.getDate());
        dayOfWeek.setText(listItem.getDayOfWeek());
        itemDescription.setText(listItem.getItemDescription());
        pickupBuilding.setText("출발) " + listItem.getPickupBuilding());
        shippingBuilding.setText("도착) " + listItem.getShippingBuilding());

        return convertView;
    }

    public void addItems(String date, String dayOfWeek, String itemDescription, String pickupBuilding, String shippingBuilding) {
        UnassignedListItem item = new UnassignedListItem(date, dayOfWeek, itemDescription, pickupBuilding, shippingBuilding);
        itemList.add(item);
    }
}
