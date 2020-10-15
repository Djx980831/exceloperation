package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.RpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/user")
public class UserController {

//    @Autowired
//    private UserService userService;
//
//    @GetMapping("/login")
//    public RpcResponse<HashMap<String, String>> login(String userName, String password) {
//        JSONObject jsonObject = userService.login(userName, password);
//        HashMap<String, String> data = (HashMap<String, String>) jsonObject.get("data");
//
//        return RpcResponse.success(data);
//    }
}
