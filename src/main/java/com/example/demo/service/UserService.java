package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;

public interface UserService {
    JSONObject login(String userName, String password);
}
