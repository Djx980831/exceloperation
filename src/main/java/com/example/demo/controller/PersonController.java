package com.example.demo.controller;

import com.example.demo.service.PersonService;
import com.example.demo.service.impl.AnalysisService;
import com.example.demo.util.ErrorInfo;
import com.example.demo.util.RpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private AnalysisService service;

    @Autowired
    private PersonService personService;

    @PostMapping("/addPerson")
    public RpcResponse<String> addPerson(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        personService.addPerson(service.analysisMail(file));
        return RpcResponse.success("success");
    }
}
