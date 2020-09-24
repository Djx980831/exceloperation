package com.example.demo.controller;

import com.example.demo.entity.CountryData;
import com.example.demo.service.CountryDataService;
import com.example.demo.service.impl.AnalysisService;
import com.example.demo.util.ErrorInfo;
import com.example.demo.util.RpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@RestController
@RequestMapping("/country")
public class CountryDataController {

    @Autowired
    private CountryDataService service;

    @PostMapping("/addCountryData")
    @ResponseBody
    public RpcResponse<String> addCountryData(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }

        ArrayList<CountryData> countryDataArrayList = AnalysisService.analysisCountry(file);
        service.addCountry(countryDataArrayList);

        return RpcResponse.success("success");
    }
}
