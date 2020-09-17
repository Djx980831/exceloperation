package com.example.demo.controller;

import com.example.demo.entity.Student;
import com.example.demo.service.impl.AnalysisService;
import com.example.demo.util.ErrorInfo;
import com.example.demo.util.RpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@RestController
@RequestMapping("/testBoot")
public class StudentController {

    @PostMapping("/upLoadFile")
    public RpcResponse<ArrayList<Student>> upLoadFile(MultipartFile file) {
        if (file.isEmpty()) {
            return RpcResponse.error(new ErrorInfo(101, "文件为空"));
        }
        return RpcResponse.success(AnalysisService.analysis(file));
    }
}
