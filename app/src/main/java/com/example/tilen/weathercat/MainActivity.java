package com.example.tilen.weathercat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tilen.weathercat.model.Cities;
import com.example.tilen.weathercat.model.WeatherData;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private CitiesAdapter adapter;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v(TAG, "onCreate was called");

        Button button = (Button) findViewById(R.id.button);
        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Refresh was clicked");
                TextView temperatureText = (TextView) findViewById(R.id.textView);
                temperatureText.setText("Temperature refreshed");
            }
        });

        adapter = new CitiesAdapter(this);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WeatherData item = adapter.getItem(position);
                Intent intent  = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("city", item);
                startActivity(intent);
            }
        });

        makeNetworkRequest();

    }

    private void makeNetworkRequest() {

        String cityIds = TextUtils.join(",", new Integer[] {
                3239318, 3186843, 3192062, 3197378, 3194351, 3198647, 3192241, 3195506, 5128638,
                1689973, 3186886, 2759794, 5056033, 2950159, 2988507, 292223, 1609350, 1138958
        });

        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host("api.openweathermap.org")
                .addPathSegment("/data/2.5/group")
                .addQueryParameter("id", cityIds)
                .addQueryParameter("units","metric")
                .addQueryParameter("appid", "07fcd5fe2102a2741bb2676f98c0ed57")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("unexpected code "+response);
                }

                Gson gson = new Gson();
                final Cities cities = gson.fromJson(response.body().string(), Cities.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setItems(cities.getList());
                    }
                });
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        Log.v(TAG, "Main activity is on pause... Chill...");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "Main activity has been resumed.");
    }
}
