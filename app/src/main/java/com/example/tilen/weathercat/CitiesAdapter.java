package com.example.tilen.weathercat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tilen.weathercat.model.WeatherData;

/**
 * Created by Tilen on 12.5.2016.
 */
public class CitiesAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final Resources resources;
    private WeatherData[] items;

    public CitiesAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        resources = context.getResources();
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
            view = inflater.inflate(R.layout.list_item_city, parent, false);
        }

        WeatherData item = getItem(position);

        TextView textView = (TextView) view.findViewById(R.id.title);
        textView.setText(item.getName());

        final ImageView imageView = (ImageView) view.findViewById(R.id.image);
        new ImageTask(resources,imageView).execute(R.drawable.sun);

        return view;
    }

    private static class ImageTask extends AsyncTask<Integer, Void, Drawable> {

        private final Resources resources;
        private final ImageView imageView;

        private ImageTask(Resources resources, ImageView imageView) {
            this.resources = resources;
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            imageView.setImageDrawable(null);
        }

        @Override
        protected Drawable doInBackground(Integer... params) {
            return resources.getDrawable(params[0], null);
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            imageView.setImageDrawable(drawable);
        }
    }

}
