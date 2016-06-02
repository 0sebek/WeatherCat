package com.example.tilen.weathercat;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.tilen.weathercat.db.MySQLiteHelper;
import com.example.tilen.weathercat.model.Main;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import com.example.tilen.weathercat.model.Cities;
import com.example.tilen.weathercat.model.WeatherData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by blaz on 26/05/16.
 */

public class CityListFragment extends Fragment {

    private CitiesAdapter adapter;

    private final OkHttpClient client = new OkHttpClient();

    private ShowDetail detailInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ShowDetail) {
            detailInterface = (ShowDetail) context;
        } else {
            throw new RuntimeException("Parent activity should implement ShowDetail");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button refresh = (Button) view.findViewById(R.id.button);

        refresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new LoadTask(getContext()).execute();
            }
        });

        adapter = new CitiesAdapter(getActivity());
        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WeatherData item = adapter.getItem(position);
                detailInterface.showDetail(item);

            }
        });

        new LoadTask(getContext()).execute();
    }



    private void requestCities(List<Long> cities) {

        String cityIds = TextUtils.join(",", cities);

        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host("api.openweathermap.org")
                .addPathSegment("/data/2.5/group")
                .addQueryParameter("id", cityIds)
                .addQueryParameter("units", "metric")
                .addQueryParameter("appid", "264eb32663a1e4e9cb406b10f7186248")
                .build();


        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Gson gson = new Gson();
                final Cities citiesResponse = gson.fromJson(response.body().string(), Cities.class);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setItems(citiesResponse.getList());
                    }
                });
            }
        });

    }

    private class LoadTask extends AsyncTask<Void,Void, Cities> {

        private final Context context;

        public LoadTask(Context context) {
            this.context = context;
        }

        @Override
        protected Cities doInBackground(Void... params) {

            MySQLiteHelper helper = new MySQLiteHelper(context);

            Cursor cursor = helper.getReadableDatabase()
                    .query("cities", new String[] {"city_id"}, null, null, null,null, "_id ASC");

            List<Long> cities = new ArrayList<>();
            int columnId = cursor.getColumnIndex("city_id");

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                long cityId = cursor.getLong(columnId);
                cities.add(cityId);

                cursor.moveToNext();
            }

            cursor.close();

            requestCities(cities);

            return null;
        }
    }

}