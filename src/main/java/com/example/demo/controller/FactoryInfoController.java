package com.example.demo.controller;

import com.example.demo.service.FactoryInfoService;
import com.example.demo.util.ErrorInfo;
import com.example.demo.util.RpcResponse;
import com.example.demo.vo.request.FactoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@RestController
@RequestMapping("/factoryinfo")
public class FactoryInfoController {

    @Autowired
    private FactoryInfoService service;

    @PostMapping("/addInfo")
    public RpcResponse<String> addInfo(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        ArrayList<FactoryVO> list = service.readExcel(file);
        for (int i = 0; i < list.size(); i++) {
            service.addFactoryInfo(list.get(i));
        }
        return RpcResponse.success("success");
    }
}
