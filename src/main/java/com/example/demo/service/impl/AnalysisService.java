package com.example.demo.service.impl;

import checkers.units.quals.A;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.*;
import com.example.demo.mapper.GroupBOMMapper;
import com.example.demo.util.RedisUtils;
import com.example.demo.vo.response.FinalResult;
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
import java.util.stream.Collectors;

/**
 * @Author：dong
 * @Description：excel文件解析类
 */
@Service
public class AnalysisService {

    @Autowired
    private GroupBOMMapper groupBOMMapper;

    @Autowired
    private RedisUtils redisUtils;

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

    public ArrayList<FinalResult> secondGroupCheck(HashMap<String, Boolean> hashMap) {
        ArrayList<FinalResult> finalResultList = new ArrayList<>();
        Set<String> groupIdSet = getKeysByStream(hashMap, false);
        for (String gid : groupIdSet) {
//            if (!gid.equals("G1398")) {
//                continue;
//            }
            FinalResult finalResult = new FinalResult();
            HashSet<String> sonIdsDoneSet = new HashSet<>(64);
            ArrayList<String> errorBomIdsList = new ArrayList<>();
            ArrayList<String> bomIds = groupBOMMapper.getBomIdsByGroupId(gid);
            Boolean flag = null;
            for (int i = 0; i < bomIds.size(); i++) {
                flag = false;
                ArrayList<String> sonIds = groupBOMMapper.getSonIdsByBomId(bomIds.get(i));
                if (sonIdsDoneSet != null && sonIdsDoneSet.size() > 0) {
                    for (int j = 0; j < sonIds.size(); j++) {
                        if (sonIdsDoneSet.contains(sonIds.get(j))) {
                            flag = true;
                            sonIdsDoneSet.addAll(sonIds);
                            break;
                        }
                    }
                }
                if (flag == true) {
                    break;
                }
                if (i == bomIds.size() - 1) {
                    flag = false;
                    errorBomIdsList.add(bomIds.get(i));
                    System.out.println("groupId:" + gid + "-------" + "index:" + i + "-------" + bomIds.get(i));
                    break;
                }
                ArrayList<String> mid = getAfterBomId4SonIds(bomIds, i);
                for (String id : sonIds) {
                    if (mid.contains(id)) {
                        sonIdsDoneSet.addAll(sonIds);
                        flag = true;
                        break;
                    }
                }
                if (flag == false) {
                    errorBomIdsList.add(bomIds.get(i));
                    System.out.println("groupId:" + gid + "-------" + "index:" + i + "-------" + bomIds.get(i));
                    //break;
                }
            }

            finalResult.setGid(gid);
            finalResult.setErrorBomIdList(errorBomIdsList);
            finalResult.setFlag((errorBomIdsList == null || errorBomIdsList.size() == 0) ? true : false);

            finalResultList.add(finalResult);
        }

        return finalResultList;
    }

    private ArrayList<String> getAfterBomId4SonIds(ArrayList<String> list, int index) {
        ArrayList<String> result = new ArrayList<>(200);
        for (int i = index + 1; i < list.size(); i++) {
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
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
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
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
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
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
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
        if (strings.length == 1) {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setGroupId(list.get(0).toString());
            groupInfo.setBomId(strings[0]);

            info.add(groupInfo);
            return info;
        }
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

    //测试不同组分组是否准确
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
                ArrayList<String> singleBomGroupList = new ArrayList<>();
                ArrayList<String> allGroupList = new ArrayList<>();
                Sheet sheet = workbook.getSheetAt(m);
                //从第二行开始获取
                for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
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
                    } else {
                        singleBomGroupList.add(rowList.get(0));
                    }
                    allGroupList.add(rowList.get(0));
                    //关闭资源
                    workbook.close();
                }
                stringArrayList.add(strings);
                stringArrayList.add(singleBomGroupList);
                stringArrayList.add(allGroupList);
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

    public static ArrayList<HashSet<String>> getInvAndItemsSet(MultipartFile file) {
        ArrayList<HashSet<String>> stringArrayList = new ArrayList<>();
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
            for (int m = 0; m < 2; m++) {
                Sheet sheet = workbook.getSheetAt(m);
                HashSet<String> rowSet = new HashSet<>();
                //从第一行开始获取
                for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                    //循环获取工作表的每一行
                    Row row = sheet.getRow(i);
                    if (null == row) {
                        System.out.println("---------" + i);
                        continue;
                    }
                    //循环获取每一列
                    for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                        //将每一个单元格的值装入列集合
                        rowSet.add(row.getCell(j).toString());
                        row.getCell(j);
                    }

                    //将装有每一列的集合装入大集合

                    //关闭资源
                    workbook.close();
                }
                stringArrayList.add(rowSet);
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

    public ArrayList<String> checkInv(ArrayList<HashSet<String>> list) {
        HashSet<String> invSet = list.get(0);
        HashSet<String> itemsSet = list.get(1);
        ArrayList<String> result = new ArrayList<>();
        for (String str : itemsSet) {
            if (!invSet.contains(str)) {
                result.add(str);
            }
        }
        return result;
    }

    public HashMap<String, Boolean> isNotSameGroup(ArrayList<ArrayList<String>> list) {
        ArrayList<String> groupIdList = list.get(2);
        HashSet<String> groupIdSet = new HashSet<>();
        ArrayList<String> resultGroupIdList = new ArrayList<>();
        groupIdSet.addAll(groupIdList);
        resultGroupIdList.addAll(groupIdSet);
        int length = resultGroupIdList.size();
        HashMap<String, Boolean> result = new HashMap<>();
        HashMap<String, ArrayList<String>> groupAndSonIdsMap = new HashMap<>();
        for (int i = 0; i < length; i++) {
            //判断redis中是否有group BomIds缓存
            String groupIdKey = resultGroupIdList.get(i) + "BomIdsList";
            ArrayList<String> bomIds = new ArrayList<>(100);
            if (redisUtils.get(groupIdKey) != null) {
                bomIds = (ArrayList) JSONObject.parseObject((String) redisUtils.get(groupIdKey), ArrayList.class);
            } else {
                 bomIds =  groupBOMMapper.getBomIdsByGroupId(resultGroupIdList.get(i));
                 redisUtils.set(groupIdKey, JSONObject.toJSONString(bomIds));
            }

            //判断redis中是否有group onIds缓存
            String sonIdsKey = resultGroupIdList.get(i) + "SonIdsList";
            ArrayList<String> sonIds = new ArrayList<>(500);
            if (redisUtils.get(sonIdsKey) != null) {
                sonIds = (ArrayList) JSONObject.parseObject((String) redisUtils.get(sonIdsKey), ArrayList.class);
            } else {
                sonIds = groupBOMMapper.getSonIdsByBomIds(bomIds);
                redisUtils.set(sonIdsKey, JSONObject.toJSONString(sonIds));
            }
            groupAndSonIdsMap.put(resultGroupIdList.get(i), sonIds);
        }
        for (int i = 0; i < length; i++) {
            Boolean flag = false;
            for (int j = i + 1; j < length; j++) {
                flag = bomGroupIsTrueOrFalse(groupAndSonIdsMap.get(resultGroupIdList.get(i)), groupAndSonIdsMap.get(resultGroupIdList.get(j)));
                if (flag == true) {
                    System.out.println(resultGroupIdList.get(i) + "---" + resultGroupIdList.get(j));
                    break;
                }
            }
            result.put(resultGroupIdList.get(i), flag);
        }

        return result;
    }

    private boolean bomGroupIsTrueOrFalse(ArrayList<String> list1, ArrayList<String> list2) {
        boolean flag = false;
        if (list1 == null || list1.size() == 0 || list2 == null || list2.size() == 0) {
            return false;
        }
        for (String sonId : list1) {
            if (list2.contains(sonId)) {
                flag = true;
                break;
            }
        }
        return flag;
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