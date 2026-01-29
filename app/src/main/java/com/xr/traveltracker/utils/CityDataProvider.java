package com.xr.traveltracker.utils;

import com.xr.traveltracker.models.City;
import java.util.ArrayList;
import java.util.List;

public class CityDataProvider {

    public static List<City> getChinaCities() {
        List<City> cities = new ArrayList<>();

        // 直辖市
        cities.add(new City("北京", "北京市", 39.9042, 116.4074));
        cities.add(new City("上海", "上海市", 31.2304, 121.4737));
        cities.add(new City("天津", "天津市", 39.3434, 117.3616));
        cities.add(new City("重庆", "重庆市", 29.4316, 106.9123));

        // 省会城市
        cities.add(new City("广州", "广东省", 23.1291, 113.2644));
        cities.add(new City("深圳", "广东省", 22.5431, 114.0579));
        cities.add(new City("杭州", "浙江省", 30.2741, 120.1551));
        cities.add(new City("南京", "江苏省", 32.0603, 118.7969));
        cities.add(new City("苏州", "江苏省", 31.2989, 120.5853));
        cities.add(new City("武汉", "湖北省", 30.5928, 114.3055));
        cities.add(new City("成都", "四川省", 30.5728, 104.0668));
        cities.add(new City("西安", "陕西省", 34.3416, 108.9398));
        cities.add(new City("郑州", "河南省", 34.7466, 113.6253));
        cities.add(new City("长沙", "湖南省", 28.2282, 112.9388));
        cities.add(new City("沈阳", "辽宁省", 41.8057, 123.4328));
        cities.add(new City("大连", "辽宁省", 38.9140, 121.6147));
        cities.add(new City("青岛", "山东省", 36.0671, 120.3826));
        cities.add(new City("济南", "山东省", 36.6512, 117.1205));
        cities.add(new City("哈尔滨", "黑龙江省", 45.8038, 126.5340));
        cities.add(new City("长春", "吉林省", 43.8171, 125.3235));
        cities.add(new City("福州", "福建省", 26.0745, 119.2965));
        cities.add(new City("厦门", "福建省", 24.4798, 118.0894));
        cities.add(new City("昆明", "云南省", 25.0406, 102.7123));
        cities.add(new City("南宁", "广西壮族自治区", 22.8170, 108.3665));
        cities.add(new City("贵阳", "贵州省", 26.6470, 106.6302));
        cities.add(new City("太原", "山西省", 37.8706, 112.5489));
        cities.add(new City("石家庄", "河北省", 38.0428, 114.5149));
        cities.add(new City("南昌", "江西省", 28.6829, 115.8579));
        cities.add(new City("合肥", "安徽省", 31.8206, 117.2272));
        cities.add(new City("呼和浩特", "内蒙古自治区", 40.8414, 111.7519));
        cities.add(new City("兰州", "甘肃省", 36.0611, 103.8343));
        cities.add(new City("银川", "宁夏回族自治区", 38.4872, 106.2309));
        cities.add(new City("西宁", "青海省", 36.6171, 101.7782));
        cities.add(new City("乌鲁木齐", "新疆维吾尔自治区", 43.8256, 87.6168));
        cities.add(new City("拉萨", "西藏自治区", 29.6520, 91.1721));
        cities.add(new City("海口", "海南省", 20.0444, 110.1999));
        cities.add(new City("三亚", "海南省", 18.2528, 109.5117));

        // 其他重要城市
        cities.add(new City("珠海", "广东省", 22.2711, 113.5767));
        cities.add(new City("佛山", "广东省", 23.0218, 113.1219));
        cities.add(new City("东莞", "广东省", 23.0205, 113.7518));
        cities.add(new City("宁波", "浙江省", 29.8683, 121.5440));
        cities.add(new City("温州", "浙江省", 28.0006, 120.6994));
        cities.add(new City("无锡", "江苏省", 31.4912, 120.3119));
        cities.add(new City("常州", "江苏省", 31.8122, 119.9692));
        cities.add(new City("扬州", "江苏省", 32.3912, 119.4121));
        cities.add(new City("南通", "江苏省", 32.0085, 120.8945));
        cities.add(new City("徐州", "江苏省", 34.2044, 117.2844));
        cities.add(new City("烟台", "山东省", 37.4638, 121.4478));
        cities.add(new City("威海", "山东省", 37.5128, 122.1201));
        cities.add(new City("洛阳", "河南省", 34.6197, 112.4540));
        cities.add(new City("桂林", "广西壮族自治区", 25.2736, 110.2900));
        cities.add(new City("丽江", "云南省", 26.8721, 100.2330));
        cities.add(new City("大理", "云南省", 25.6064, 100.2675));
        cities.add(new City("张家界", "湖南省", 29.1167, 110.4792));
        cities.add(new City("黄山", "安徽省", 29.7144, 118.3377));

        return cities;
    }
}