package com.example.tilen.weathercat;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.tilen.weathercat.R;
import com.example.tilen.weathercat.model.WeatherData;
/**
 * Created by Tilen on 26.5.2016.
 */
public class CityDetailFragment extends Fragment {

    public static final String EXTRA_WEATHER_DATA = "weather_data";

    private WeatherData weatherData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_city_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle extras = getArguments();
        weatherData = extras.getParcelable(EXTRA_WEATHER_DATA);

        getActivity().setTitle(weatherData.getName());

        TextView tempView = (TextView) view.findViewById(R.id.temperature);
        tempView.setText(getString(R.string.temp, weatherData.getMain().getTemp()));

        final SharedPreferences preferences = getContext().getSharedPreferences("weather_cat", Context.MODE_PRIVATE);

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.cb_favourite);
        boolean isFavourite = weatherData.getId() == preferences.getLong("favourite_city_id", -1);
        checkBox.setChecked(isFavourite);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    preferences.edit()
                            .putLong("favourite_city_id", weatherData.getId())
                            .apply();
                } else {
                    preferences.edit()
                            .putLong("favourite_city_id", -1)
                            .apply();
                }
            }
        });
    }
}
