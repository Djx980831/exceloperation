package com.example.demo.service.impl;

import checkers.units.quals.A;
import com.example.demo.entity.ECN;
import com.example.demo.mapper.SourceDataMapper;
import com.example.demo.service.SourceDataService;
import com.example.demo.util.JudegExcelEdition;
import com.example.demo.util.RedisUtils;
import com.example.demo.vo.request.SourceData;
import com.example.demo.vo.response.ECNResult;
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
import java.util.HashSet;
import java.util.List;

@Service
public class SourceDataServiceImpl implements SourceDataService {

    static final HashSet<String> stringSet = new HashSet<>();

    static {
        stringSet.add("组件");
        stringSet.add("名称");
        stringSet.add("数量");
        stringSet.add("位置");
        stringSet.add("组件代码");
        stringSet.add("物料编码");
        stringSet.add("单机数量");
    }

    static final HashSet<String> ecnStringSet = new HashSet<>();

    static {
        ecnStringSet.add("上层物料编码");
        ecnStringSet.add("改前物料编码");
        ecnStringSet.add("改前数量");
        ecnStringSet.add("改前位置");
        ecnStringSet.add("改后物料编码");
        ecnStringSet.add("改后数量");
        ecnStringSet.add("改后位置");
    }

    @Autowired
    private SourceDataMapper mapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void addData(ArrayList<String> data) {
        mapper.addData(data);
    }

    public ArrayList<String> addSourceData(MultipartFile file) {
        ArrayList<String> resultList = new ArrayList<>(10000);
        //获取文件名称
        String fileName = file.getOriginalFilename();
        System.out.println(fileName);

        try {
            //获取输入流
            InputStream in = file.getInputStream();
            //判断excel版本
            Workbook workbook = null;
            if (JudegExcelEdition.judegExcelEdition(fileName)) {
                workbook = new XSSFWorkbook(in);
            } else {
                workbook = new HSSFWorkbook(in);
            }
            //获取第一张工作表
            Sheet sheet = workbook.getSheetAt(0);
            //循环获取工作表的每一行
            Row row = sheet.getRow(0);
            //记录需要读取的下标
            ArrayList<Integer> indexList = new ArrayList<>();
            for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                if (stringSet.contains(row.getCell(i).toString())) {
                    indexList.add(i);
                }
            }
            // 第二行开始
            for (int m = 1; m < sheet.getPhysicalNumberOfRows(); m++) {
                StringBuilder sb = new StringBuilder();
                //循环获取每一列
                Row sheetRow = sheet.getRow(m);
                for (int j = 0; j < sheetRow.getPhysicalNumberOfCells(); j++) {
                    //将每一个单元格的值装入列集合
                    if (!indexList.contains(j)) {
                        continue;
                    } else {
                        sb.append(sheetRow.getCell(j).toString());
                    }
//                    resultList.add(row.getCell(j).toString());
//                    row.getCell(j);
                }
                resultList.add(sb.toString());
            }
            //关闭资源
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("===================未找到文件======================");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("===================上传失败======================");
        }
        return resultList;
    }

    public void groupAddData(ArrayList<String> data) {
//        ArrayList<List<String>> dataList = processedNumder(500, data);
//        for (int i = 0; i < data.size(); i++) {
//            List<String> stringList = dataList.get(i);
//            ArrayList<String> resultList = new ArrayList<>();
//            resultList.addAll(stringList);
//            //addData(resultList);
//            redisUtils.insertListKey(stringList);
//        }
        for (int i = 0; i < data.size(); i++) {
            redisUtils.set(data.get(i), data.get(i));
        }
    }

    @Override
    public void addECNData(ArrayList<ECN> data) {
        mapper.addECNData(data);
    }

    private ArrayList<List<String>> processedNumder(int processedNum, ArrayList<String> proceData) {
        //每次处理几条
        ArrayList<String> list = proceData;//new ArrayList<String>();
        ArrayList<List<String>> listPut = new ArrayList<>();
//        for (int i = 0; i < proceData.size(); i++) {
////            list.add(i+"");
////        }
        int num = processedNum;//每次多少条
        int nums = processedNum;//每次最大到多少条
        int s = list.size() / num;
        int w = list.size() % num;
        for (int i = 0; i < s; i++) {
//            System.out.println(list.subList(i*num,nums));
            listPut.add(list.subList(i * num, nums));
            nums += num;
        }
        if (w > 0) {
//            System.out.println(list.subList(nums-num,nums-num+w));
            listPut.add(list.subList(nums - num, nums - num + w));
        }
        return listPut;
    }

    public ArrayList<ECN> addECNData(MultipartFile file) {
        ArrayList<ECN> resultList = new ArrayList<>(200);
        //获取文件名称
        String fileName = file.getOriginalFilename();
        System.out.println(fileName);

        try {
            //获取输入流
            InputStream in = file.getInputStream();
            //判断excel版本
            Workbook workbook = null;
            if (JudegExcelEdition.judegExcelEdition(fileName)) {
                workbook = new XSSFWorkbook(in);
            } else {
                workbook = new HSSFWorkbook(in);
            }
            //获取第一张工作表
            Sheet sheet = workbook.getSheetAt(0);
            //循环获取工作表的每一行
            Row row = sheet.getRow(0);
            //记录需要读取的下标
            ArrayList<Integer> indexList = new ArrayList<>();
            for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                if (ecnStringSet.contains(row.getCell(i).toString())) {
                    indexList.add(i);
                }
            }
            ArrayList<Integer> beforeIndexList = new ArrayList<>();
            beforeIndexList.add(indexList.get(0));
            beforeIndexList.add(indexList.get(1));
            beforeIndexList.add(indexList.get(2));
            beforeIndexList.add(indexList.get(3));

            ArrayList<Integer> afterIndexList = new ArrayList<>();
            afterIndexList.add(indexList.get(0));
            afterIndexList.add(indexList.get(4));
            afterIndexList.add(indexList.get(5));
            afterIndexList.add(indexList.get(6));
            // 第二行开始
            for (int m = 1; m < sheet.getPhysicalNumberOfRows(); m++) {
                ECN ecn = new ECN();
                StringBuilder sbBefore = new StringBuilder();
                StringBuilder sbAfter = new StringBuilder();
                //循环获取每一列
                Row sheetRow = sheet.getRow(m);
                for (int j = 0; j < sheetRow.getPhysicalNumberOfCells(); j++) {
                    if (beforeIndexList.contains(j)) {
                        sbBefore.append(sheetRow.getCell(j).toString());
                    }
                    if (afterIndexList.contains(j) && sheetRow.getCell(afterIndexList.get(1)) != null) {
                        sbAfter.append(sheetRow.getCell(j).toString());
                    }
                }
                ecn.setRowNum(m + 1);
                ecn.setBeforeString(sbBefore.toString());
                ecn.setAfterString(sbAfter.toString() == null ? "null" : sbAfter.toString());

                resultList.add(ecn);
            }
            //关闭资源
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("===================未找到文件======================");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("===================上传失败======================");
        }
        return resultList;
    }

    public ArrayList<ECNResult> getECNCompareResult(MultipartFile file) {
        ArrayList<ECNResult> results = new ArrayList<>();
        ArrayList<ECN> ecnArrayList = addECNData(file);
        for (int i = 0; i < ecnArrayList.size(); i++) {
            ECNResult ecnResult = new ECNResult();
            ECN ecn = ecnArrayList.get(i);
            int beforeCount = redisUtils.get(ecn.getBeforeString()) == null ? 0 : 1;
            int afterCount = 1;
            if (ecn.getAfterString() != null) {
                afterCount = redisUtils.get(ecn.getAfterString()) == null ? 0 : 1;
            }
            if (beforeCount != 0 || afterCount == 0) {
                ecnResult.setFlag(false);
                ECNResult.ResultInfo info = new ECNResult.ResultInfo();
                info.setPlmResult(false);
                info.setPlmAfter(afterCount);
                info.setPlmBefore(beforeCount);
                ecnResult.setResultInfo(info);
            } else {
                ecnResult.setFlag(true);
            }
            results.add(ecnResult);
        }
        return results;
    }
}
