package com.example.demo.mapper;

import com.example.demo.entity.Dish;
import com.example.demo.entity.Punishment;
import com.example.demo.entity.Restaurant;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface RestaurantMapper {

    ArrayList<Restaurant> getRestaurant();

    ArrayList<Dish> getDish(Integer id);

    ArrayList<String> getPunishment();
}
