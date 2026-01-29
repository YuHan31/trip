package com.xr.traveltracker.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.xr.traveltracker.R;
import com.xr.traveltracker.utils.ToolbarHelper;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String WEATHER_API_KEY = "916a65799b8eee9ec593928cec3e62a6"; // 需要从 openweathermap.org 获取
    private static final String WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather?";

    private TextView tvCityName, tvTemperature, tvWeatherCondition, tvUpdateTime;
    private TextView tvWindDirection, tvWindSpeed, tvHumidity, tvVisibility;
    private TextView tvClothingSuggestion, tvTopClothing, tvBottomClothing, tvShoes, tvAccessories;
    private LinearLayout citySelector;
    private ImageView btnRefresh;

    private LocationManager locationManager;
    private RequestQueue requestQueue;
    private String currentCity = "广州"; // 默认城市

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("WeatherActivity", "onCreate 开始");

        try {
            setContentView(R.layout.activity_weather);

            // 使用ToolbarHelper设置Toolbar
            ToolbarHelper.setupToolbar(this, "天气查询", true, v -> {
                finish();
            });

            android.util.Log.d("WeatherActivity", "开始初始化视图");
            initViews();
            android.util.Log.d("WeatherActivity", "开始初始化定位");
            initLocation();
            android.util.Log.d("WeatherActivity", "开始设置监听器");
            setupListeners();

            // 请求定位权限
            android.util.Log.d("WeatherActivity", "请求定位权限");
            requestLocationPermission();
        } catch (Exception e) {
            android.util.Log.e("WeatherActivity", "onCreate 异常: " + e.getMessage(), e);
            Toast.makeText(this, "初始化失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        android.util.Log.d("WeatherActivity", "initViews 开始");

        tvCityName = findViewById(R.id.tvCityName);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvWeatherCondition = findViewById(R.id.tvWeatherCondition);
        tvUpdateTime = findViewById(R.id.tvUpdateTime);
        tvWindDirection = findViewById(R.id.tvWindDirection);
        tvWindSpeed = findViewById(R.id.tvWindSpeed);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvVisibility = findViewById(R.id.tvVisibility);
        tvClothingSuggestion = findViewById(R.id.tvClothingSuggestion);
        tvTopClothing = findViewById(R.id.tvTopClothing);
        tvBottomClothing = findViewById(R.id.tvBottomClothing);
        tvShoes = findViewById(R.id.tvShoes);
        tvAccessories = findViewById(R.id.tvAccessories);
        citySelector = findViewById(R.id.citySelector);
        btnRefresh = findViewById(R.id.btnRefresh);

        android.util.Log.d("WeatherActivity", "所有视图已找到，开始初始化 Volley");

        try {
            requestQueue = Volley.newRequestQueue(this);
            android.util.Log.d("WeatherActivity", "Volley RequestQueue 初始化成功");
        } catch (Exception e) {
            android.util.Log.e("WeatherActivity", "Volley RequestQueue 初始化失败: " + e.getMessage(), e);
        }
    }

    private void initLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    private void setupListeners() {
        // 城市选择器点击事件
        citySelector.setOnClickListener(v -> showCitySearchDialog());

        // 刷新按钮点击事件
        btnRefresh.setOnClickListener(v -> {
            v.animate().rotation(360f).setDuration(500).withEndAction(() -> {
                v.setRotation(0f);
                fetchWeatherData(currentCity);
            });
        });
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "正在定位...", Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            } else {
                Toast.makeText(this, "定位权限被拒绝，使用默认城市", Toast.LENGTH_SHORT).show();
                fetchWeatherData(currentCity);
            }
        }
    }

    private void getCurrentLocation() {
        android.util.Log.d("WeatherActivity", "开始获取位置");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            android.util.Log.w("WeatherActivity", "没有定位权限");
            return;
        }

        try {
            Location location = null;

            // 检查GPS是否可用
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // 检查网络定位是否可用
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            android.util.Log.d("WeatherActivity", "GPS可用: " + isGPSEnabled + ", 网络定位可用: " + isNetworkEnabled);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // 没有可用的定位服务
                android.util.Log.w("WeatherActivity", "没有可用的定位服务");
                Toast.makeText(this, "请开启定位服务", Toast.LENGTH_SHORT).show();
                fetchWeatherData(currentCity);
                return;
            }

            // 先尝试获取缓存的位置
            if (isNetworkEnabled) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    android.util.Log.d("WeatherActivity", "网络定位缓存成功 - 纬度: " + location.getLatitude() + ", 经度: " + location.getLongitude());
                    Toast.makeText(this, "网络定位成功", Toast.LENGTH_SHORT).show();
                    getCityNameFromLocation(location);
                    return;
                }
            }

            if (location == null && isGPSEnabled) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    android.util.Log.d("WeatherActivity", "GPS定位缓存成功 - 纬度: " + location.getLatitude() + ", 经度: " + location.getLongitude());
                    Toast.makeText(this, "GPS定位成功", Toast.LENGTH_SHORT).show();
                    getCityNameFromLocation(location);
                    return;
                }
            }

            // 如果缓存没有位置，主动请求一次位置更新
            android.util.Log.d("WeatherActivity", "缓存中没有位置，请求位置更新");
            Toast.makeText(this, "正在定位...", Toast.LENGTH_SHORT).show();

            android.location.LocationListener locationListener = new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location loc) {
                    android.util.Log.d("WeatherActivity", "收到位置更新 - 纬度: " + loc.getLatitude() + ", 经度: " + loc.getLongitude());
                    locationManager.removeUpdates(this);
                    getCityNameFromLocation(loc);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {}
            };

            // 优先使用网络定位（更快）
            if (isNetworkEnabled) {
                android.util.Log.d("WeatherActivity", "请求网络定位更新");
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0,
                        0,
                        locationListener
                );

                // 5秒后如果还没有结果，尝试GPS或使用默认城市
                new android.os.Handler().postDelayed(() -> {
                    locationManager.removeUpdates(locationListener);
                    if (isGPSEnabled) {
                        android.util.Log.d("WeatherActivity", "网络定位超时，尝试GPS");
                        requestGPSLocation();
                    } else {
                        android.util.Log.w("WeatherActivity", "定位超时，使用默认城市");
                        Toast.makeText(this, "定位超时，使用默认城市：" + currentCity, Toast.LENGTH_SHORT).show();
                        fetchWeatherData(currentCity);
                    }
                }, 5000);
            } else if (isGPSEnabled) {
                requestGPSLocation();
            } else {
                android.util.Log.w("WeatherActivity", "无可用定位服务，使用默认城市");
                Toast.makeText(this, "无法获取位置信息，使用默认城市：" + currentCity, Toast.LENGTH_LONG).show();
                fetchWeatherData(currentCity);
            }

        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e("WeatherActivity", "定位异常: " + e.getMessage());
            Toast.makeText(this, "定位失败：" + e.getMessage() + "，使用默认城市", Toast.LENGTH_SHORT).show();
            fetchWeatherData(currentCity);
        }
    }

    private void requestGPSLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            android.util.Log.d("WeatherActivity", "请求GPS定位更新");
            android.location.LocationListener gpsListener = new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location loc) {
                    android.util.Log.d("WeatherActivity", "GPS收到位置更新 - 纬度: " + loc.getLatitude() + ", 经度: " + loc.getLongitude());
                    locationManager.removeUpdates(this);
                    getCityNameFromLocation(loc);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {}
            };

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    gpsListener
            );

            // 10秒后如果还没有结果，使用默认城市
            new android.os.Handler().postDelayed(() -> {
                locationManager.removeUpdates(gpsListener);
                android.util.Log.w("WeatherActivity", "GPS定位超时，使用默认城市");
                Toast.makeText(this, "定位超时，使用默认城市：" + currentCity, Toast.LENGTH_SHORT).show();
                fetchWeatherData(currentCity);
            }, 10000);

        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e("WeatherActivity", "GPS定位异常: " + e.getMessage());
            fetchWeatherData(currentCity);
        }
    }

    private void getCityNameFromLocation(Location location) {
        android.util.Log.d("WeatherActivity", "开始地理编码 - 纬度: " + location.getLatitude() + ", 经度: " + location.getLongitude());

        // 在后台线程执行地理编码，避免阻塞UI
        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.CHINA);
                List<Address> addresses = geocoder.getFromLocation(
                        location.getLatitude(), location.getLongitude(), 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);

                    android.util.Log.d("WeatherActivity", "地址信息 - Locality: " + address.getLocality()
                        + ", SubAdminArea: " + address.getSubAdminArea()
                        + ", AdminArea: " + address.getAdminArea());

                    // 尝试获取城市名（多种方式）
                    String city = address.getLocality(); // 城市
                    if (city == null || city.isEmpty()) {
                        city = address.getSubAdminArea(); // 地级市
                    }
                    if (city == null || city.isEmpty()) {
                        city = address.getAdminArea(); // 省份
                    }

                    if (city != null && !city.isEmpty()) {
                        // 移除"市"字
                        String finalCity = city.replace("市", "");
                        android.util.Log.d("WeatherActivity", "解析到城市: " + finalCity);

                        // 回到主线程更新UI
                        runOnUiThread(() -> {
                            currentCity = finalCity;
                            Toast.makeText(this, "定位成功：" + finalCity, Toast.LENGTH_SHORT).show();
                            fetchWeatherData(currentCity);
                        });
                    } else {
                        android.util.Log.w("WeatherActivity", "无法从地址中提取城市名");
                        runOnUiThread(() -> {
                            Toast.makeText(this, "无法识别城市，使用默认城市", Toast.LENGTH_SHORT).show();
                            fetchWeatherData(currentCity);
                        });
                    }
                } else {
                    android.util.Log.w("WeatherActivity", "地理编码返回空结果");
                    runOnUiThread(() -> {
                        Toast.makeText(this, "地理编码失败，使用默认城市", Toast.LENGTH_SHORT).show();
                        fetchWeatherData(currentCity);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                android.util.Log.e("WeatherActivity", "地理编码异常: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "获取城市名失败，使用默认城市", Toast.LENGTH_SHORT).show();
                    fetchWeatherData(currentCity);
                });
            }
        }).start();
    }

    private void fetchWeatherData(String cityName) {
        try {
            android.util.Log.d("WeatherActivity", "fetchWeatherData 开始 - 输入城市: " + cityName);

            // 清理城市名（去除空格和"市"字）
            String cleanedCityName = cityName.trim().replace("市", "");

            // 更新当前城市名
            currentCity = cleanedCityName;

            // 将中文城市名转换为英文
            String englishCityName = convertCityNameToEnglish(cleanedCityName);

            // 调试日志
            android.util.Log.d("WeatherActivity", "原始城市名: " + cityName + ", 清理后: " + cleanedCityName + ", 英文: " + englishCityName);

            // 使用真实API获取天气数据
            String url = WEATHER_API_URL + "q=" + englishCityName + ",CN&appid=" + WEATHER_API_KEY
                    + "&units=metric&lang=zh_cn";

            android.util.Log.d("WeatherActivity", "API URL: " + url);

            // 检查 requestQueue 是否为 null
            if (requestQueue == null) {
                android.util.Log.e("WeatherActivity", "requestQueue 为 null!");
                Toast.makeText(this, "网络请求队列未初始化", Toast.LENGTH_SHORT).show();
                loadMockWeatherData(cleanedCityName);
                return;
            }

            android.util.Log.d("WeatherActivity", "开始创建 JsonObjectRequest");

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            android.util.Log.d("WeatherActivity", "API响应成功");
                            parseWeatherData(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            android.util.Log.e("WeatherActivity", "解析失败: " + e.getMessage());
                            Toast.makeText(this, "解析天气数据失败", Toast.LENGTH_SHORT).show();
                            loadMockWeatherData(cleanedCityName);
                        }
                    },
                    error -> {
                        android.util.Log.e("WeatherActivity", "API请求失败: " + error.toString());
                        if (error.networkResponse != null) {
                            android.util.Log.e("WeatherActivity", "HTTP状态码: " + error.networkResponse.statusCode);
                        }
                        Toast.makeText(this, "获取天气数据失败，显示模拟数据", Toast.LENGTH_SHORT).show();
                        // 如果API调用失败，使用模拟数据作为备用
                        loadMockWeatherData(cleanedCityName);
                    });

            android.util.Log.d("WeatherActivity", "JsonObjectRequest 创建成功，准备添加到队列");
            requestQueue.add(request);
            android.util.Log.d("WeatherActivity", "请求已添加到队列");

        } catch (Exception e) {
            android.util.Log.e("WeatherActivity", "fetchWeatherData 异常: " + e.getMessage(), e);
            Toast.makeText(this, "请求天气数据时出错: " + e.getMessage(), Toast.LENGTH_LONG).show();
            loadMockWeatherData(cityName);
        }
    }

    private String convertCityNameToEnglish(String chineseName) {
        // 清理输入：去除空格和"市"字
        String cleanName = chineseName.trim().replace("市", "");

        // 中文城市名转英文映射
        switch (cleanName) {
            case "北京": return "Beijing";
            case "上海": return "Shanghai";
            case "广州": return "Guangzhou";
            case "深圳": return "Shenzhen";
            case "成都": return "Chengdu";
            case "杭州": return "Hangzhou";
            case "武汉": return "Wuhan";
            case "西安": return "Xian";
            case "南京": return "Nanjing";
            case "重庆": return "Chongqing";
            case "天津": return "Tianjin";
            case "苏州": return "Suzhou";
            case "郑州": return "Zhengzhou";
            case "长沙": return "Changsha";
            case "沈阳": return "Shenyang";
            case "青岛": return "Qingdao";
            case "香港": return "Hong Kong";
            case "澳门": return "Macau";
            case "台北": return "Taipei";
            case "大连": return "Dalian";
            case "厦门": return "Xiamen";
            case "福州": return "Fuzhou";
            case "济南": return "Jinan";
            case "哈尔滨": return "Harbin";
            case "长春": return "Changchun";
            case "石家庄": return "Shijiazhuang";
            case "南昌": return "Nanchang";
            case "贵阳": return "Guiyang";
            case "昆明": return "Kunming";
            case "兰州": return "Lanzhou";
            case "太原": return "Taiyuan";
            case "长治": return "Changzhi";
            case "合肥": return "Hefei";
            case "南宁": return "Nanning";
            case "银川": return "Yinchuan";
            case "西宁": return "Xining";
            case "海口": return "Haikou";
            case "三亚": return "Sanya";
            case "拉萨": return "Lhasa";
            case "乌鲁木齐": return "Urumqi";
            case "呼和浩特": return "Hohhot";
            default:
                // 如果没有匹配，尝试返回清理后的名称（可能是英文名）
                android.util.Log.w("WeatherActivity", "未找到城市映射: " + cleanName);
                return cleanName;
        }
    }

    private void parseWeatherData(JSONObject response) throws Exception {
        // 解析真实API数据
        JSONObject main = response.getJSONObject("main");
        JSONObject wind = response.getJSONObject("wind");
        JSONObject weather = response.getJSONArray("weather").getJSONObject(0);

        int temp = (int) Math.round(main.getDouble("temp"));
        int humidity = main.getInt("humidity");
        String condition = weather.getString("description");
        double windSpeedMs = wind.getDouble("speed"); // m/s
        int windSpeed = convertWindSpeedToLevel(windSpeedMs); // 转换为风力等级
        int windDeg = wind.has("deg") ? wind.getInt("deg") : 0;
        int visibility = response.has("visibility") ? response.getInt("visibility") / 1000 : 10; // 转换为公里

        android.util.Log.d("WeatherActivity", "解析成功 - 城市: " + currentCity + ", 温度: " + temp + "°C");

        updateUI(currentCity, temp, condition, getWindDirection(windDeg),
                windSpeed, humidity, visibility);
    }

    private void loadMockWeatherData(String cityName) {
        // 模拟天气数据
        currentCity = cityName;
        int temperature = 9;
        String condition = "阴";
        String windDirection = "西北风";
        int windSpeed = 4;
        int humidity = 90;
        int visibility = 30;

        updateUI(cityName, temperature, condition, windDirection, windSpeed, humidity, visibility);
    }

    private void updateUI(String city, int temperature, String condition,
                         String windDirection, int windSpeed, int humidity, int visibility) {
        tvCityName.setText(city);
        tvTemperature.setText(temperature + "°C");
        tvWeatherCondition.setText(condition);
        tvWindDirection.setText(windDirection);
        tvWindSpeed.setText(windSpeed + "级");
        tvHumidity.setText(humidity + "%");
        tvVisibility.setText(visibility + " km");

        // 更新时间
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
        tvUpdateTime.setText("更新: " + sdf.format(new Date()));

        // 根据温度更新穿搭建议
        updateClothingSuggestion(temperature, condition);
    }

    private void updateClothingSuggestion(int temperature, String condition) {
        String suggestion, topClothing, bottomClothing, shoes, accessories;

        if (temperature < 5) {
            suggestion = "天气严寒，建议穿着厚重保暖的衣物，注意防寒保暖。";
            topClothing = "羽绒服/棉衣";
            bottomClothing = "加绒裤/厚棉裤";
            shoes = "保暖靴子";
            accessories = "围巾/手套/帽子";
        } else if (temperature < 15) {
            suggestion = "天气寒冷，建议穿着保暖的外套和裤子。";
            topClothing = "大衣/厚外套";
            bottomClothing = "厚牛仔裤/休闲裤";
            shoes = "保暖靴子";
            accessories = "围巾";
        } else if (temperature < 20) {
            suggestion = "天气凉爽，建议穿着薄外套，早晚注意保暖。";
            topClothing = "薄外套/卫衣";
            bottomClothing = "牛仔裤/休闲裤";
            shoes = "运动鞋/休闲鞋";
            accessories = "轻薄围巾（可选）";
        } else if (temperature < 28) {
            suggestion = "天气温暖舒适，适合穿着轻便的衣物。";
            topClothing = "T恤/衬衫";
            bottomClothing = "牛仔裤/休闲裤";
            shoes = "运动鞋/帆布鞋";
            accessories = "太阳镜（可选）";
        } else {
            suggestion = "天气炎热，建议穿着清凉透气的衣物，注意防晒。";
            topClothing = "短袖T恤/背心";
            bottomClothing = "短裤/薄长裤";
            shoes = "凉鞋/透气运动鞋";
            accessories = "遮阳帽/太阳镜";
        }

        // 根据天气状况调整建议
        if (condition.contains("雨")) {
            suggestion += " 今日有雨，记得带伞。";
            accessories += "/雨伞";
        }

        tvClothingSuggestion.setText(suggestion);
        tvTopClothing.setText(topClothing);
        tvBottomClothing.setText(bottomClothing);
        tvShoes.setText(shoes);
        tvAccessories.setText(accessories);
    }

    private String getWindDirection(int degree) {
        String[] directions = {"北风", "东北风", "东风", "东南风", "南风", "西南风", "西风", "西北风"};
        int index = (int) ((degree + 22.5) / 45) % 8;
        return directions[index];
    }

    private int convertWindSpeedToLevel(double speedMs) {
        // 将风速（米/秒）转换为风力等级（蒲福风级）
        if (speedMs < 0.3) return 0;
        else if (speedMs < 1.6) return 1;
        else if (speedMs < 3.4) return 2;
        else if (speedMs < 5.5) return 3;
        else if (speedMs < 8.0) return 4;
        else if (speedMs < 10.8) return 5;
        else if (speedMs < 13.9) return 6;
        else if (speedMs < 17.2) return 7;
        else if (speedMs < 20.8) return 8;
        else if (speedMs < 24.5) return 9;
        else if (speedMs < 28.5) return 10;
        else if (speedMs < 32.7) return 11;
        else return 12;
    }

    private void showCitySearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_city_search, null);

        EditText etCityName = dialogView.findViewById(R.id.etCityName);
        TextView tvBeijing = dialogView.findViewById(R.id.tvBeijing);
        TextView tvShanghai = dialogView.findViewById(R.id.tvShanghai);
        TextView tvGuangzhou = dialogView.findViewById(R.id.tvGuangzhou);
        TextView tvShenzhen = dialogView.findViewById(R.id.tvShenzhen);

        AlertDialog dialog = builder.setView(dialogView)
                .setTitle("搜索城市")
                .setPositiveButton("搜索", (d, which) -> {
                    String cityName = etCityName.getText().toString().trim();
                    if (!cityName.isEmpty()) {
                        fetchWeatherData(cityName);
                    } else {
                        Toast.makeText(this, "请输入城市名称", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .create();

        // 热门城市点击事件
        tvBeijing.setOnClickListener(v -> {
            fetchWeatherData("北京");
            dialog.dismiss();
        });

        tvShanghai.setOnClickListener(v -> {
            fetchWeatherData("上海");
            dialog.dismiss();
        });

        tvGuangzhou.setOnClickListener(v -> {
            fetchWeatherData("广州");
            dialog.dismiss();
        });

        tvShenzhen.setOnClickListener(v -> {
            fetchWeatherData("深圳");
            dialog.dismiss();
        });

        dialog.show();
    }
}
