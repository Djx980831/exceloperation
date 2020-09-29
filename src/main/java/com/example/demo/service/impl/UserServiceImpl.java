package com.example.demo.service.impl;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RestTemplate restTemplate ;

    public JSONObject login(String userName, String password){
        String url = "http://127.0.0.1:8080/user/login?userName=" + userName +"&password=" + password;
        return this.restTemplate.getForObject(url, JSONObject.class);
    }
}
