package com.example.demo.controller;

import com.example.demo.entity.GroupInfo;
import com.example.demo.service.GroupBOMService;
import com.example.demo.util.RpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/groupBom")
public class GroupBOMController {

    @Autowired
    private GroupBOMService service;

    @PostMapping("/addGroup")
    public RpcResponse<String> addGroup(ArrayList<GroupInfo> groupInfoArrayList) {
        return Rpc
    }
}
