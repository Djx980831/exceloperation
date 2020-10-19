package com.example.demo.service;

import com.example.demo.entity.User;

public interface RedisService {

    String testRedis(String key, String value);

    String addUser(Integer id, String userName, String password);

    User getUser(String key);
}
