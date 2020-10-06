package com.example.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.User;
import com.example.demo.service.RedisService;
import com.example.demo.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public String testRedis(String key, String value) {
        boolean flag = redisUtils.set(key, value);
        System.out.println(flag);
        return "success";
    }

    @Override
    public String addUser(Integer id, String userName, String password) {
        User user = new User();
        user.setId(id);
        user.setUserName(userName);
        user.setPassword(password);

        redisUtils.set("u1", JSONObject.toJSONString(user));
        return "success";
    }

    @Override
    public User getUser(String key) {
        User user = JSONObject.parseObject((String) redisUtils.get(key), User.class);
        return user;
    }
}
