package com.example.tilen.weathercat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tilen.weathercat.model.WeatherData;

/**
 * Created by Tilen on 12.5.2016.
 */
public class CitiesAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private WeatherData[] items;

    public CitiesAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setItems(WeatherData[] items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (items == null) {
            return 0;
        } else {
            return items.length;
        }
    }

    @Override
    public WeatherData getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            // create new view
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        WeatherData item = getItem(position);

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(item.getName());

        return view;
    }
}
