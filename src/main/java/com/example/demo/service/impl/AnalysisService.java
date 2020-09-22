package com.example.demo.service.impl;

import com.example.demo.entity.Bom;
import com.example.demo.entity.CountryData;
import com.example.demo.entity.GroupInfo;
import com.example.demo.entity.Student;
import com.example.demo.mapper.GroupBOMMapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @Author：dong
 * @Description：excel文件解析类
 */
@Service
public class AnalysisService {

    @Autowired
    private GroupBOMMapper groupBOMMapper;

    public Boolean isCheckTrue(String groupId) {
        ArrayList<String> bomIds = groupBOMMapper.getBomIdsByGroupId(groupId);
        ArrayList<ArrayList<String>> bomAllSonIds = new ArrayList<>();
        for (int i = 0; i < bomIds.size(); i++) {
            ArrayList<String> sonIds = groupBOMMapper.getSonIdsByBomId(bomIds.get(i));
            bomAllSonIds.add(sonIds);
        }
        int index = getMaxCount(bomAllSonIds);
        ArrayList resultBomIds = groupBOMMapper.getBomIdsBySonIds(bomAllSonIds.get(index));
        if (!isTrueOrFalse(bomIds, resultBomIds)) {
            return false;
        }
        return true;
    }

    private int getMaxCount(ArrayList<ArrayList<String>> bomAllSonIds) {
        int max = bomAllSonIds.get(0).size();
        int index = 0;
        for (int i = 1; i < bomAllSonIds.size(); i++) {
            if (max < bomAllSonIds.get(i).size()) {
                max = bomAllSonIds.get(i).size();
                index = i;
            }

        }
        return index;
    }

    public HashMap<String, Boolean> chenkGroup(ArrayList<String> groupIds) {
        HashMap<String, Boolean> result = new HashMap<>();
        for (String id : groupIds) {
            if (isCheckTrue(id)) {
                result.put(id, true);
            } else {
                result.put(id, false);
            }
        }
        return result;
    }

    //通过value获得key值
    private <K, V> Set<K> getKeysByStream(Map<K, V> map, V value) {
        return map.entrySet()
                .stream()
                .filter(kvEntry -> Objects.equals(kvEntry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public HashMap<String, Boolean> secondGroupCheck(HashMap<String, Boolean> hashMap) {
        HashMap<String, Boolean> result = new HashMap<>();
        Set<String> griupSet = getKeysByStream(hashMap, false);
        for (String gid : griupSet) {
            ArrayList<String> sonIdsDone = new ArrayList<>(300);
            ArrayList<String> bomIds = groupBOMMapper.getBomIdsByGroupId(gid);
            Boolean flag = null;
            for (int i = 0; i < bomIds.size(); i++) {
                flag = false;
                ArrayList<String> sonIds = groupBOMMapper.getSonIdsByBomId(bomIds.get(i));
                if (sonIdsDone != null && sonIdsDone.size() > 0) {
                    for (int j = 0; j < sonIds.size(); j++) {
                        if (sonIdsDone.contains(sonIds.get(j))) {
                            flag = true;
                            sonIdsDone.addAll(sonIds);
                            break;
                        }
                    }
                }
                ArrayList<String> mid = getExceptOneBomId4SonIds(bomIds, i);
                for (String id : sonIds) {
                    if (mid.contains(id)) {
                        sonIdsDone.addAll(sonIds);
                        flag = true;
                        break;
                    }
                }
                if (flag == false) {
                    System.out.println("index:" + i + "-------" + bomIds.get(i));
                    break;
                }
            }
            result.put(gid, flag);
        }

        return result;
    }

    private ArrayList<String> getExceptOneBomId4SonIds(ArrayList<String> list, int index) {
        ArrayList<String> result = new ArrayList<>(200);
        for (int i = 0; i < list.size(); i++) {
            if (i == index) {
                continue;
            }
            ArrayList<String> mid = groupBOMMapper.getSonIdsByBomId(list.get(i));
            result.addAll(mid);
        }
        return result;
    }

    private Boolean isTrueOrFalse(ArrayList<String> target, ArrayList<String> target1) {
        for (String str : target) {
            if (!target1.contains(str)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 获取并解析excel文件，返回一个二维集合
     *
     * @param file 上传的文件
     * @return 二维集合（第一重集合为行，第二重集合为列，每一行包含该行的列集合，列集合包含该行的全部单元格的值）
     */
    public static ArrayList<Student> analysis(MultipartFile file) {
        ArrayList<Student> studentArrayList = new ArrayList<>();
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

            //获取第一张工作表
            Sheet sheet = workbook.getSheetAt(0);
            //从第二行开始获取
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                //循环获取工作表的每一行
                Row row = sheet.getRow(i);
                if (null == row) {
                    continue;
                }
                //循环获取每一列
                ArrayList<String> rowList = new ArrayList<>();
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    //将每一个单元格的值装入列集合
                    rowList.add(row.getCell(j).toString());
                    row.getCell(j);
//                    Cell cell = row.getCell(j);
//                    String cellValue = "";
//                    if (null != cell) {
//                        // 以下是判断数据的类型
//                        switch (cell.getCellType()) {
//                            case HSSFCell.CELL_TYPE_NUMERIC: // 数字
//                                DecimalFormat df = new DecimalFormat("0");
//                                cellValue = df.format(cell.getNumericCellValue());
//                                break;
//                            case HSSFCell.CELL_TYPE_STRING: // 字符串
//                                cellValue = cell.getStringCellValue();
//                                break;
//                            case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
//                                cellValue = cell.getBooleanCellValue() + "";
//                                break;
//                            case HSSFCell.CELL_TYPE_FORMULA: // 公式
//                                cellValue = cell.getCellFormula() + "";
//                                break;
//                            case HSSFCell.CELL_TYPE_BLANK: // 空值
//                                cellValue = "";
//                                break;
//                            case HSSFCell.CELL_TYPE_ERROR: // 故障
//                                cellValue = "非法字符";
//                                break;
//                            default:
//                                cellValue = "未知类型";
//                                break;
//                        }
//                    }
                }
                //将装有每一列的集合装入大集合
                Student student = listToStudent(rowList);
                studentArrayList.add(student);

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

        return studentArrayList;
    }

    /**
     * 判断上传的excel文件版本（xls为2003，xlsx为2017）
     *
     * @param fileName 文件路径
     * @return excel2007及以上版本返回true，excel2007以下版本返回false
     */
    private static boolean judegExcelEdition(String fileName) {
        if (fileName.matches("^.+\\.(?i)(xls)$")) {
            return false;
        } else {
            return true;
        }
    }

    public static ArrayList<ArrayList<GroupInfo>> analysisGroup(MultipartFile file) {
        ArrayList<ArrayList<GroupInfo>> groupInfo = new ArrayList<>();
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

            //获取第一张工作表
            Sheet sheet = workbook.getSheetAt(0);
            //从第二行开始获取
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                //循环获取工作表的每一行
                Row row = sheet.getRow(i);
                if (null == row) {
                    continue;
                }
                //循环获取每一列
                ArrayList<String> rowList = new ArrayList<>();
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    //将每一个单元格的值装入列集合
                    rowList.add(row.getCell(j).toString());
                    row.getCell(j);
//                    Cell cell = row.getCell(j);
//                    String cellValue = "";
//                    if (null != cell) {
//                        // 以下是判断数据的类型
//                        switch (cell.getCellType()) {
//                            case HSSFCell.CELL_TYPE_NUMERIC: // 数字
//                                DecimalFormat df = new DecimalFormat("0");
//                                cellValue = df.format(cell.getNumericCellValue());
//                                break;
//                            case HSSFCell.CELL_TYPE_STRING: // 字符串
//                                cellValue = cell.getStringCellValue();
//                                break;
//                            case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
//                                cellValue = cell.getBooleanCellValue() + "";
//                                break;
//                            case HSSFCell.CELL_TYPE_FORMULA: // 公式
//                                cellValue = cell.getCellFormula() + "";
//                                break;
//                            case HSSFCell.CELL_TYPE_BLANK: // 空值
//                                cellValue = "";
//                                break;
//                            case HSSFCell.CELL_TYPE_ERROR: // 故障
//                                cellValue = "非法字符";
//                                break;
//                            default:
//                                cellValue = "未知类型";
//                                break;
//                        }
//                    }
                }
                ArrayList<GroupInfo> groupInfoArrayList = listToGroupInfoList(rowList);
                //将装有每一列的集合装入大集合

                groupInfo.add(groupInfoArrayList);
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

        return groupInfo;
    }

    public static ArrayList<ArrayList<Bom>> analysisBom(MultipartFile file) {
        ArrayList<ArrayList<Bom>> bomInfo = new ArrayList<>();
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

            //获取第一张工作表
            Sheet sheet = workbook.getSheetAt(1);
            //从第二行开始获取
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                //循环获取工作表的每一行
                Row row = sheet.getRow(i);
                if (null == row) {
                    continue;
                }
                //循环获取每一列
                ArrayList<String> rowList = new ArrayList<>();
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    //将每一个单元格的值装入列集合
                    rowList.add(row.getCell(j).toString());
                    row.getCell(j);
//                    Cell cell = row.getCell(j);
//                    String cellValue = "";
//                    if (null != cell) {
//                        // 以下是判断数据的类型
//                        switch (cell.getCellType()) {
//                            case HSSFCell.CELL_TYPE_NUMERIC: // 数字
//                                DecimalFormat df = new DecimalFormat("0");
//                                cellValue = df.format(cell.getNumericCellValue());
//                                break;
//                            case HSSFCell.CELL_TYPE_STRING: // 字符串
//                                cellValue = cell.getStringCellValue();
//                                break;
//                            case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
//                                cellValue = cell.getBooleanCellValue() + "";
//                                break;
//                            case HSSFCell.CELL_TYPE_FORMULA: // 公式
//                                cellValue = cell.getCellFormula() + "";
//                                break;
//                            case HSSFCell.CELL_TYPE_BLANK: // 空值
//                                cellValue = "";
//                                break;
//                            case HSSFCell.CELL_TYPE_ERROR: // 故障
//                                cellValue = "非法字符";
//                                break;
//                            default:
//                                cellValue = "未知类型";
//                                break;
//                        }
//                    }
                }
                ArrayList<Bom> bomrrayList = listToBomList(rowList);
                //将装有每一列的集合装入大集合

                bomInfo.add(bomrrayList);
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

        return bomInfo;
    }

    private static Student listToStudent(ArrayList list) {
        Student student = new Student();
        student.setStudentId(list.get(0).toString());
        student.setStudentName(list.get(1).toString());
        student.setSex(list.get(2).toString().trim().equals("男") ? "M" : "F");
        student.setGrade(list.get(3).toString());
        student.setGradeClass(list.get(4).toString());
        student.setArea(list.get(5).toString());

        return student;
    }

    private static ArrayList<GroupInfo> listToGroupInfoList(ArrayList list) {
        ArrayList<GroupInfo> info = new ArrayList<>();
        String[] strings = list.get(1).toString().split(",");
        if (null != strings) {
            for (int i = 0; i < strings.length; i++) {
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setGroupId(list.get(0).toString());
                groupInfo.setBomId(strings[i]);

                info.add(groupInfo);
            }
        }

        return info;
    }

    private static ArrayList<Bom> listToBomList(ArrayList list) {
        ArrayList<Bom> info = new ArrayList<>();
        String[] strings = list.get(1).toString().split(",");
        if (strings != null) {
            for (int i = 0; i < strings.length; i++) {
                Bom bom = new Bom();
                bom.setBomId(list.get(0).toString());
                bom.setSonId(strings[i]);

                info.add(bom);
            }
        }

        return info;
    }

    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        // resolveLazily属性启用是为了推迟文件解析，以在UploadAction中捕获文件大小异常
        resolver.setResolveLazily(true);
        resolver.setMaxInMemorySize(40960);
        // 上传文件大小 5G
        resolver.setMaxUploadSize(5 * 1024 * 1024 * 1024);
        return resolver;
    }

    public static ArrayList<ArrayList<String>> getGroupAndBomList(MultipartFile file) {
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

            //获取第一张工作表
            for (int m = 0; m < 2; m++) {
                ArrayList<String> strings = new ArrayList<>();
                Sheet sheet = workbook.getSheetAt(m);
                //从第二行开始获取
                for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                    //循环获取工作表的每一行
                    Row row = sheet.getRow(i);
                    if (null == row) {
                        continue;
                    }
                    //循环获取每一列
                    ArrayList<String> rowList = new ArrayList<>();
                    for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                        //将每一个单元格的值装入列集合
                        rowList.add(row.getCell(j).toString());
                        row.getCell(j);
                    }

                    //将装有每一列的集合装入大集合
                    if (getCount4List(rowList) != null) {
                        strings.add(getCount4List(rowList));
                    }

                    //关闭资源
                    workbook.close();
                }
                stringArrayList.add(strings);
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

    private static String getCount4List(ArrayList<String> list) {
        String[] strings = list.get(1).split(",");
        if (strings.length != 1) {
            return list.get(0);
        }
        return null;
    }

    public static ArrayList<CountryData> analysisCountry(MultipartFile file) {
        ArrayList<CountryData> countryDataArrayList = new ArrayList<>();
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

            //获取第一张工作表
            Sheet sheet = workbook.getSheetAt(5);
            //从第二行开始获取
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                //循环获取工作表的每一行
                Row row = sheet.getRow(i);
                if (null == row) {
                    continue;
                }
                //循环获取每一列
                ArrayList<String> rowList = new ArrayList<>();
                for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                    //将每一个单元格的值装入列集合
                    rowList.add(row.getCell(j).toString());
                    row.getCell(j);
                }
                //将装有每一列的集合装入大集合
                CountryData data = listToCountryData(rowList);
                countryDataArrayList.add(data);

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

        return countryDataArrayList;
    }

    private static CountryData listToCountryData(ArrayList list) {
        CountryData data = new CountryData();
        data.setSx(list.get(0).toString());
        data.setCoun(list.get(1).toString());
        data.setYw(list.get(2).toString());

        return data;
    }
}