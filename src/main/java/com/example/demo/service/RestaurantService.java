package com.example.demo.service;

import com.example.demo.entity.Dish;
import com.example.demo.entity.Punishment;
import com.example.demo.entity.Restaurant;
import com.example.demo.vo.response.ResDIshResponse;

import java.util.ArrayList;

public interface RestaurantService {

    ArrayList<Restaurant> getRestaurant();

    ArrayList<String> getDish(Integer id);

    ArrayList<String> getPunishment();

    ResDIshResponse getResult();
}
