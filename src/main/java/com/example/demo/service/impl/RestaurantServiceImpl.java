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
    public ArrayList<String> getDish(Integer id) {
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
        ArrayList<HashMap<String, ArrayList<String>>> resultList = new ArrayList<>();
        for (int i = 0; i < restaurantArrayList.size(); i++) {
            HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
            int id = restaurantArrayList.get(i).getId();
            ArrayList<String> resDishList = getDish(id);
            hashMap.put(restaurantArrayList.get(i).getResName(), resDishList);
            resultList.add(hashMap);
        }
        int resIndex = RandomNumber.getRandomNumber(resultList.size());
        HashMap<String, ArrayList<String>> hashMap = resultList.get(resIndex);
        String resName = restaurantArrayList.get(resIndex).getResName();
        ArrayList<String> dishList = hashMap.get(resName);

        int wyvDishIndex = RandomNumber.getRandomNumber(dishList.size());
        String wyvDishName = dishList.get(wyvDishIndex);
        int djxDishIndex = RandomNumber.getRandomNumber(dishList.size());
        String djxDishName = dishList.get(djxDishIndex);

        ArrayList<String> punList = getPunishment();
        int punIndex = RandomNumber.getRandomNumber(punList.size());

        String punName = punList.get(punIndex);
        String pun = getString(punName);

        result.setRes(resName);
        result.setWyvDishName(wyvDishName);
        result.setDjxDishName(djxDishName);
        result.setPunishment(pun);
        return result;
    }

    private String getString(String punName) {
        StringBuilder sb = new StringBuilder();
        int wyvNum = RandomNumber.getRandomNumber();
        int djxNum = RandomNumber.getRandomNumber();
        if (wyvNum > djxNum) {
            sb.append("wyv的数字是: " + wyvNum + ", djx的数字是: " + djxNum + ", wyv赢，djx将接受的惩罚是：" + punName);
        } else if (wyvNum == djxNum) {
            sb.append("wyv的数字是: " + wyvNum + ", djx的数字是: " + djxNum + ", 平局。");
        } else {
            sb.append("wyv的数字是: " + wyvNum + ", djx的数字是: " + djxNum + ", djx赢，wyv将接受的惩罚是：" + punName);
        }
        return sb.toString();
    }
}
