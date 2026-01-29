package com.xr.traveltracker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CityPreferences {
    private static final String PREF_NAME = "travel_footprint_prefs";
    private static final String KEY_VISITED_CITIES = "visited_cities";

    private SharedPreferences preferences;
    private Gson gson;

    public CityPreferences(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    /**
     * 保存已访问的城市列表
     */
    public void saveVisitedCities(Set<String> cityNames) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(KEY_VISITED_CITIES, cityNames);
        editor.apply();
    }

    /**
     * 获取已访问的城市列表
     */
    public Set<String> getVisitedCities() {
        return preferences.getStringSet(KEY_VISITED_CITIES, new HashSet<>());
    }

    /**
     * 添加一个已访问的城市
     */
    public void addVisitedCity(String cityName) {
        Set<String> cities = new HashSet<>(getVisitedCities());
        cities.add(cityName);
        saveVisitedCities(cities);
    }

    /**
     * 移除一个已访问的城市
     */
    public void removeVisitedCity(String cityName) {
        Set<String> cities = new HashSet<>(getVisitedCities());
        cities.remove(cityName);
        saveVisitedCities(cities);
    }

    /**
     * 检查城市是否已访问
     */
    public boolean isCityVisited(String cityName) {
        return getVisitedCities().contains(cityName);
    }

    /**
     * 获取已访问城市的数量
     */
    public int getVisitedCityCount() {
        return getVisitedCities().size();
    }

    /**
     * 清空所有已访问的城市
     */
    public void clearAllVisitedCities() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_VISITED_CITIES);
        editor.apply();
    }
}