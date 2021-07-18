package com.example.demo.service;

import com.example.demo.entity.FactoryInfo;
import com.example.demo.vo.request.FactoryVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public interface FactoryInfoService {
    void addFactoryInfo(FactoryVO vo);

    FactoryInfo getInfoByCountryAndName(String country, String name);

    void updateInfo(FactoryInfo info);

    ArrayList<FactoryVO> readExcel(MultipartFile file);
}
