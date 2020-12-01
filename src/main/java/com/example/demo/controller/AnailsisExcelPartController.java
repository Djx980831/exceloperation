package com.example.demo.controller;

import com.example.demo.service.impl.AnalysisExcelPartService;
import com.example.demo.util.ErrorInfo;
import com.example.demo.util.RpcResponse;
import com.example.demo.vo.request.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@RestController
@RequestMapping("/part")
public class AnailsisExcelPartController {

    @Autowired
    private AnalysisExcelPartService service;

    @PostMapping("/addPart")
    public RpcResponse<String> addPart(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        ArrayList<ArrayList<String>> arrayLists = service.getPartStringList(file);
        ArrayList<Part> partArrayList = service.toPartList(arrayLists);
        service.addPart(partArrayList);
        service.writeFanYi(service.getFanYiData());
        return RpcResponse.success("success");
    }

    @PostMapping("/dealLic")
    public RpcResponse<String> dealLic(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        service.writeSet(service.getLicStringList(file));
        return RpcResponse.success("success");
    }

    @PostMapping("/dealRange")
    public RpcResponse<String> dealRange(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        service.writeFanYi(service.getFanYiValue(service.getRangeAndAttributeArrayList(file)));
        return RpcResponse.success("success");
    }
}
