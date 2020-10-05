package com.example.demo.controller;

import com.example.demo.entity.Student;
import com.example.demo.service.RedisService;
import com.example.demo.service.StudentService;
import com.example.demo.service.impl.AnalysisService;
import com.example.demo.util.ErrorInfo;
import com.example.demo.util.RpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private RedisService redisService;

    @PostMapping("/upLoadFile")
    @ResponseBody
    public RpcResponse<String> upLoadFile(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        System.out.println(file);

        ArrayList<Student> studentArrayList = AnalysisService.analysis(file);
//        for (int i = 0; i < studentArrayList.size(); i++) {
//            System.out.println(studentArrayList.get(i));
//        }
        studentService.addStudent(studentArrayList);

        return RpcResponse.success("success");
    }

    @PostMapping("/test")
    public RpcResponse<String> test(String file) {
        System.out.println(file);
        String name = "djx";
        return RpcResponse.success(name);
    }

    @PostMapping("/testRedis")
    public RpcResponse<String> testRedis(String key, String value) {


        return RpcResponse.success("success");
    }
}
