package com.example.demo.controller;

import com.example.demo.entity.Student;
import com.example.demo.service.impl.AnalysisService;
import com.example.demo.util.ErrorInfo;
import com.example.demo.util.RpcResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@RestController
@RequestMapping("/testBoot")
public class StudentController {

    @PostMapping("/upLoadFile")
    @ResponseBody
    public RpcResponse<ArrayList<Student>> upLoadFile(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        System.out.println(file);
        return RpcResponse.success(AnalysisService.analysis(file));
    }

    @PostMapping("/test")
    public RpcResponse<String> test(String file) {
        System.out.println(file);
        String name = "djx";
        return RpcResponse.success(name);
    }
}
