package com.example.demo.service.impl;

import com.example.demo.mapper.AnalysisExcelPartMapper;
import com.example.demo.vo.request.Part;
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
public class AnalysisExcelPartService {

    @Autowired
    private AnalysisExcelPartMapper mapper;

    public ArrayList<ArrayList<String>> getPartStringList(MultipartFile file) {
        ArrayList<ArrayList<String>> stringArrayList = new ArrayList<>();
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
            //从第4行开始获取
            for (int i = 3; i < sheet.getPhysicalNumberOfRows(); i++) {
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
                    rowList.add((row.getCell(j) == null || "".equals(row.getCell(j)))? "dddddddddddddd" : row.getCell(j).toString());
                }
                stringArrayList.add(rowList);
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
        return stringArrayList;
    }

    public ArrayList<Part> toPartList(ArrayList<ArrayList<String>> lists) {
        ArrayList<Part> reaultList = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            Part part = new Part();
            part.setAttributeName(getAttributeName(lists.get(i).get(2), lists.get(i).get(4), lists.get(i).get(6)));
            part.setRegistryName(getAttributeName(lists.get(i).get(2), lists.get(i).get(4), lists.get(i).get(6)));
            part.setType("string");
            part.setDefalutValue(lists.get(i).get(12));
            part.setRangeSystem(lists.get(i).get(18));
            part.setRange(lists.get(i).get(18));
            part.setRangeCN(lists.get(i).get(17));
            part.setMultiline(lists.get(i).get(10).equals("复选框") ? "TRUE" : "FALSE");
            part.setAttributeChineseName(lists.get(i).get(5));
            part.setIsRequire(lists.get(i).get(8));
            part.setClassCode(lists.get(i).get(2));
            part.setAttributeCode(lists.get(i).get(4));
            part.setInstructions(lists.get(i).get(13));
            part.setAttributeEnglishName(lists.get(i).get(6));
            reaultList.add(part);
            if (lists.get(i).get(16) != null && !(lists.get(i).get(16).equals("ddddddddddddd"))) {
                Part unit = new Part();
                unit.setAttributeName(getAttributeName(lists.get(i).get(2), lists.get(i).get(4), lists.get(i).get(6)) + "_UNIT");
                unit.setRegistryName(getAttributeName(lists.get(i).get(2), lists.get(i).get(4), lists.get(i).get(6)) + "_UNIT");
                unit.setType("string");
                unit.setRangeSystem(lists.get(i).get(16));
                unit.setRange(lists.get(i).get(16));
                unit.setRangeCN(lists.get(i).get(16));
                unit.setMultiline("FALSE");
                unit.setAttributeChineseName(lists.get(i).get(5) + "单位");
                unit.setIsRequire("是");
                unit.setMeasureUnit(lists.get(i).get(16));
                unit.setClassCode(lists.get(i).get(2));
                unit.setAttributeCode(lists.get(i).get(4) + "_UNIT");
                unit.setAttributeEnglishName(lists.get(i).get(6));
                reaultList.add(unit);
            }
        }
        return reaultList;
    }

    public void addPart(ArrayList<Part> parts) {
        mapper.addPart(parts);
    }

    private String getAttributeName(String kindCode, String attrCode, String engligshName) {
        StringBuilder sb = new StringBuilder();
        sb.append("tcl_").append(getLowerName(engligshName)).append("_").append(attrCode).append("_").append(kindCode);
        return sb.toString();
    }

    private String getLowerName(String enligshName) {
        String[] strings = enligshName.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            sb.append(strings[i].substring(0, 1) + strings[i].substring(1).toLowerCase());
        }
        return sb.toString();
    }
}
