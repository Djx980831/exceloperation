package com.example.demo.service.impl;

import com.example.demo.entity.Dish;
import com.example.demo.entity.Punishment;
import com.example.demo.entity.Restaurant;
import com.example.demo.mapper.RestaurantMapper;
import com.example.demo.service.RestaurantService;
import com.example.demo.util.RandomNumber;
import com.example.demo.vo.response.ResDIshResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @param
 * @Description
 * @Author dongjingxiong
 * @return
 * @Date 2020-10-15 23:30
 */
@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantMapper mapper;

    @Override
    public ArrayList<Restaurant> getRestaurant() {
        return mapper.getRestaurant();
    }

    @Override
    public ArrayList<Dish> getDish(Integer id) {
        return mapper.getDish(id);
    }

    @Override
    public ArrayList<String> getPunishment() {
        return mapper.getPunishment();
    }

    @Override
    public ResDIshResponse getResult() {

        ResDIshResponse result = new ResDIshResponse();
        ArrayList<Restaurant> restaurantArrayList = getRestaurant();
        ArrayList<HashMap<String, ArrayList<Dish>>> resultList = new ArrayList<>();
        for (int i = 0; i < restaurantArrayList.size(); i++) {
            HashMap<String, ArrayList<Dish>> hashMap = new HashMap<>();
            int id = restaurantArrayList.get(i).getId();
            ArrayList<Dish> resDishList = getDish(id);
            hashMap.put(restaurantArrayList.get(i).getResName(), resDishList);
            resultList.add(hashMap);
        }
        int resIndex = RandomNumber.getRandomNumber(resultList.size());
        HashMap<String, ArrayList<Dish>> hashMap = resultList.get(resIndex);
        String resName = restaurantArrayList.get(resIndex).getResName();
        ArrayList<Dish> dishList = hashMap.get(resName);

        //控制放料的量
        HashMap<String, String> quantityHashMap = new HashMap<>();
        quantityHashMap.put("0", "多");
        quantityHashMap.put("1", "不放");
        quantityHashMap.put("2", "少");

        int wyvDishIndex = RandomNumber.getRandomNumber(dishList.size());
        Dish wyvDish = dishList.get(wyvDishIndex);
        String wyvDishName = wyvDish.getName();
        HashMap<String, String> wyvDishReqMap = getDishRequireHap(wyvDish);
        String wyvDishReq = "";
        if (wyvDishReqMap.size() > 0) {
            String qu = quantityHashMap.get(String.valueOf(RandomNumber.getRandomNumber(quantityHashMap.size())));
            wyvDishReq = qu + wyvDishReqMap.get(String.valueOf(RandomNumber.getRandomNumber(wyvDishReqMap.size())));
        } else {
            wyvDishReq = "到店视具体情况而定。";
        }
        int djxDishIndex = RandomNumber.getRandomNumber(dishList.size());
        Dish djxDish = dishList.get(djxDishIndex);
        String djxDishName = djxDish.getName();
        HashMap<String, String> djxDishReqMap = getDishRequireHap(djxDish);
        String djxDishReq = "";
        if (djxDishReqMap.size() > 0) {
            String qu = quantityHashMap.get(String.valueOf(RandomNumber.getRandomNumber(quantityHashMap.size())));
            djxDishReq = qu + djxDishReqMap.get(String.valueOf(RandomNumber.getRandomNumber(djxDishReqMap.size())));
        } else {
            djxDishReq = "到店视具体情况而定。";
        }

        result.setPunisgment(getString(wyvDishReq, djxDishReq));
        result.setRes(resName);
        result.setWyvDishName(wyvDishName);
        result.setDjxDishName(djxDishName);
        return result;
    }

    private String getString(String wyvDishPunName, String djxDishPunName) {
        StringBuilder sb = new StringBuilder();
        int wyvNum = RandomNumber.getRandomNumber();
        int djxNum = RandomNumber.getRandomNumber();
        if (wyvNum > djxNum) {
            sb.append("wyv的数字是: " + wyvNum + ", djx的数字是: " + djxNum + ", wyv赢，djx将接受的惩罚是：" + djxDishPunName);
        } else if (wyvNum == djxNum) {
            sb.append("wyv的数字是: " + wyvNum + ", djx的数字是: " + djxNum + ", 平局。");
        } else {
            sb.append("wyv的数字是: " + wyvNum + ", djx的数字是: " + djxNum + ", djx赢，wyv将接受的惩罚是：" + wyvDishPunName);
        }
        return sb.toString();
    }

    private HashMap<String, String> getDishRequireHap(Dish dish) {
        HashMap<String, String> dishRequireMap = new HashMap<>();
        Integer i = 0;
        if (dish.getIsGarlic() == 1) {
            dishRequireMap.put(i.toString(), "蒜");
            i++;
        }
        if (dish.getIsHot() == 1) {
            dishRequireMap.put(i.toString(), "辣");
            i++;
        }
        if (dish.getIsMustard() == 1) {
            dishRequireMap.put(i.toString(), "芥末");
            i++;
        }
        if (dish.getIsShollot() == 1) {
            dishRequireMap.put(i.toString(), "葱");
            i++;
        }
        if (dish.getIsSoySauce() == 1) {
            dishRequireMap.put(i.toString() ,"酱油");
            i++;
        }

        return dishRequireMap;
    }
}
