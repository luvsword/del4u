package com.kaist.delforyou.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kaist.delforyou.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by birmjin.in on 2016. 8. 7..
 */
public class ListViewAdapter extends BaseAdapter{

    private ArrayList<ListItem> itemList = new ArrayList<ListItem>();

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
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView dayOfWeek = (TextView) convertView.findViewById(R.id.dayOfWeek);
        TextView itemDescription = (TextView) convertView.findViewById(R.id.itemDescription);
        TextView personInfo = (TextView) convertView.findViewById(R.id.personInfo);
        TextView status = (TextView) convertView.findViewById(R.id.status);

        ListItem listItem = itemList.get(position);

        date.setText(listItem.getDate());
        dayOfWeek.setText(listItem.getDayOfWeek());
        itemDescription.setText(listItem.getItemDescription());
        personInfo.setText(listItem.getPersonInfo());
        status.setText(listItem.getStatus());

        return convertView;
    }

    public void addItems(String date, String dayOfWeek, String itemDescription, String personInfo, String status) {
        ListItem item = new ListItem(date, dayOfWeek, itemDescription, personInfo, status);
        itemList.add(item);
    }
}
