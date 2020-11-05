package com.example.demo.service;

import com.example.demo.entity.ECN;
import com.example.demo.vo.response.ECNResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public interface SourceDataService {
    void addData(ArrayList<String> data);

    ArrayList<String> addSourceData(MultipartFile file);

    void groupAddData(ArrayList<String> data);

    void addECNData(ArrayList<ECN> data);

    ArrayList<ECN> addECNData(MultipartFile file);

    ArrayList<ECNResult> getECNCompareResult(MultipartFile file);
}
