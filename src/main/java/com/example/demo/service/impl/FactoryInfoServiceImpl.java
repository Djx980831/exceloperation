package com.example.demo.service.impl;

import com.example.demo.entity.FactoryInfo;
import com.example.demo.mapper.FactoryInfoMapper;
import com.example.demo.service.FactoryInfoService;
import com.example.demo.vo.request.FactoryVO;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.example.demo.util.JudegExcelEdition.judegExcelEdition;

@Service
public class FactoryInfoServiceImpl implements FactoryInfoService {

    @Autowired
    private FactoryInfoMapper factoryInfoMapper;

    @Override
    public void addFactoryInfo(FactoryVO vo) {
        FactoryInfo info = factoryInfoMapper.getInfoByCountryAndName(vo.getCountry(), vo.getName());
        if (info== null) {
            factoryInfoMapper.addFactoryInfo(vo);
        } else {
            info.setKind(info.getKind() + "," + vo.getKind());
            factoryInfoMapper.updateInfo(info);
        }
    }

    @Override
    public FactoryInfo getInfoByCountryAndName(String country, String name) {
        return factoryInfoMapper.getInfoByCountryAndName(country, name);
    }

    @Override
    public void updateInfo(FactoryInfo info) {
        factoryInfoMapper.updateInfo(info);
    }

    @Override
    public ArrayList<FactoryVO> readExcel(MultipartFile file) {
        ArrayList<FactoryVO> factoryArrayList = new ArrayList<>();
        //获取文件名称
        String fileName = file.getOriginalFilename();
        System.out.println(fileName);

        try {
            //获取输入流
            InputStream in = file.getInputStream();
            //判断excel版本
            Workbook workbook = null;
            if (judegExcelEdition(fileName)) {
                workbook = new XSSFWorkbook(in);
            } else {
                workbook = new HSSFWorkbook(in);
            }

            //获取第工作表
            Sheet sheet = workbook.getSheetAt(0);
            //从第2行开始获取
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                //循环获取工作表的每一行
                Row row = sheet.getRow(i);
                if (null == row) {
                    System.out.println("---------" + i);
                    continue;
                }
                //循环获取每一列
                ArrayList<String> rowList = new ArrayList<>();
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    //将每一个单元格的值装入列集合
                    rowList.add(row.getCell(j).toString());
                }
                FactoryVO vo = toFactoryVO(rowList);
                factoryArrayList.add(vo);
                //关闭资源
                workbook.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("===================未找到文件======================");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("===================上传失败======================");
        }
        return factoryArrayList;
    }

    private FactoryVO toFactoryVO(ArrayList<String> list) {
        FactoryVO vo = new FactoryVO();
        vo.setCountry(list.get(0));
        vo.setName(list.get(1));
        vo.setKind(list.get(2));

        return vo;
    }
}
