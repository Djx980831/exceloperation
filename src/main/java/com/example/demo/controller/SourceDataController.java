package com.example.demo.controller;

import com.example.demo.entity.ECN;
import com.example.demo.service.SourceDataService;
import com.example.demo.service.impl.AnalysisService;
import com.example.demo.util.ErrorInfo;
import com.example.demo.util.RpcResponse;
import com.example.demo.vo.response.BOMOwner;
import com.example.demo.vo.response.ECNResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@RestController
@RequestMapping("/sourceData")
public class SourceDataController {

    @Autowired
    private SourceDataService sourceDataService;

    @Autowired
    private AnalysisService analysisService;

    @PostMapping("/addData")
    public RpcResponse<String> addData(MultipartFile file) {
        if ((null == file)) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        ArrayList<String> list = sourceDataService.addSourceData(file);
        sourceDataService.groupAddData(list);
        return RpcResponse.success("success");
    }

    @PostMapping("/addECNData")
    public RpcResponse<String> addECNData(MultipartFile file) {
        if ((null == file)) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        ArrayList<ECN> list = sourceDataService.addECNData(file);
        sourceDataService.addECNData(list);
        return RpcResponse.success("success");
    }

    @PostMapping("/getECNCompareResult")
    public RpcResponse<ArrayList<ECNResult>> getECNCompareResult(MultipartFile file) {
        if ((null == file)) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        return RpcResponse.success(sourceDataService.getECNCompareResult(file));
    }

    @PostMapping("/writeBOMOwner")
    public RpcResponse<String> writeBOMOwner(MultipartFile file) {
        if ((null == file)) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        BOMOwner owner = analysisService.analysisBOMOwner(file);
        analysisService.writeBOMOwner(owner);
        return RpcResponse.success("success");
    }
}
