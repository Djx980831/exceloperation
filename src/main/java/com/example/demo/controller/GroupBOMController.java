package com.example.demo.controller;

import com.example.demo.entity.Bom;
import com.example.demo.entity.GroupInfo;
import com.example.demo.service.GroupBOMService;
import com.example.demo.service.impl.AnalysisService;
import com.example.demo.util.ErrorInfo;
import com.example.demo.util.RpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/groupBom")
public class GroupBOMController {

    @Autowired
    private GroupBOMService service;

    @Autowired
    private AnalysisService analysisService;

    @PostMapping("/addGroup")
    @ResponseBody
    public RpcResponse<String> addGroup(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }

        ArrayList<ArrayList<GroupInfo>> groupInfoArrayList = AnalysisService.analysisGroup(file);
        for (ArrayList<GroupInfo> infos : groupInfoArrayList) {
            service.addGrouping(infos);
        }
        return RpcResponse.success("success");
    }

    @PostMapping("/addBom")
    @ResponseBody
    public RpcResponse<String> addBom(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        ArrayList<ArrayList<Bom>> bomInfoArrayList = AnalysisService.analysisBom(file);
        for (ArrayList<Bom> infos : bomInfoArrayList) {
            service.addBom(infos);
        }
        return RpcResponse.success("success");
    }

    @PostMapping("/chenkGroup")
    public RpcResponse<HashMap<String, Boolean>> chenkGroup(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        ArrayList<ArrayList<String>> arrayLists = AnalysisService.getGroupAndBomList(file);
        HashMap<String, Boolean> booleanHashMap = analysisService.chenkGroup(arrayLists.get(0));
        return RpcResponse.success(booleanHashMap);
    }
}
