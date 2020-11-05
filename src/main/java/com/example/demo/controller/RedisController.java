package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.RedisService;
import com.example.demo.util.RpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisService redisService;

    @PostMapping("/testRedis")
    public RpcResponse<String> testRedis(String key, String value) {
        return RpcResponse.success(redisService.testRedis(key, value));
    }

    @PostMapping("addUser")
    public RpcResponse<String> addUser(Integer id, String userName, String password) {
        return RpcResponse.success(redisService.addUser(id, userName, password));
    }

    @PostMapping("/getUser")
    public RpcResponse<User> getUser(String key) {
       return RpcResponse.success(redisService.getUser(key));
    }

    @PostMapping("/addListKey")
    public RpcResponse<String> addListKey() {
        List<String> list = new ArrayList<>();
        list.add("ddd");
        list.add("dd");
        list.add("ddssd");
        list.add("s");
        list.add("ff");
        redisService.addListKey(list);
        return RpcResponse.success("success");
    }
}
