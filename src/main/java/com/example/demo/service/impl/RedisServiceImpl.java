package com.example.demo.service.impl;

import com.example.demo.service.RedisService;
import com.example.demo.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
