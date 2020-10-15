package com.example.demo.controller;

import com.example.demo.service.RestaurantService;
import com.example.demo.util.RpcResponse;
import com.example.demo.vo.response.ResDIshResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @param
 * @Description TODO
 * @Author dongjingxiong
 * @return
 * @Date 2020-10-16 00:04
 */
@RestController
@RequestMapping("/res")
public class RestaurantController {

    @Autowired
    private RestaurantService service;

    @PostMapping("/getResult")
    public RpcResponse<ResDIshResponse> getResult() {
        return RpcResponse.success(service.getResult());
    }
}
