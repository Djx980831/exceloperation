package com.example.demo.service.impl;

import com.example.demo.service.RedisService;
import com.example.demo.util.RedisUtils;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService {

    @Override
    public String testRedis(String key, String value) {
        RedisUtils utils = new RedisUtils();
        utils.set(key, value);
        return "success";
    }
}
