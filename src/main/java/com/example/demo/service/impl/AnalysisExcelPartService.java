package com.example.demo.service.impl;

import com.example.demo.mapper.AnalysisExcelPartMapper;
import com.example.demo.vo.request.Part;
import com.example.demo.vo.response.BOMOwner;
import com.example.demo.vo.response.FanYiResponse;
import com.example.demo.vo.response.SortReqResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.demo.util.JudegExcelEdition.judegExcelEdition;

@Service
public class AnalysisExcelPartService {

    ArrayList<String> indexList = new ArrayList<String>() {
        {
            add("A001");
            add("A002");
            add("A003");
            add("A004");
            add("A005");
            add("A006");
            add("A007");
            add("A008");
            add("A009");
            add("A010");
            add("A011");
            add("A012");
            add("A013");
            add("A014");
            add("A015");
            add("A016");
            add("A017");
            add("A018");
            add("A019");
            add("A020");
            add("A021");
            add("A022");
            add("A023");
            add("A024");
            add("A025");
            add("A026");
            add("A027");
            add("A028");
            add("A029");
            add("A030");
        }
    };

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
            for (int i = 2; i < sheet.getPhysicalNumberOfRows(); i++) {
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
                    if (isAllNum(row.getCell(j)) && subString4Index(row.getCell(j)) != 0) {
                        rowList.add(row.getCell(j).toString().substring(0, subString4Index(row.getCell(j))));
                    } else {
                        rowList.add((row.getCell(j) == null || "".equals(row.getCell(j))) ? "dddddddddddddd" : row.getCell(j).toString());
                    }
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

    private boolean isAllNum(Object obj) {
        String str = obj.toString();
        char[] chars = str.toCharArray();
        boolean flag = false;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '.' || Character.isDigit(chars[i])) {
                flag = true;
            } else {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private int subString4Index(Object obj) {
        String str = obj.toString();
        if (str.contains(".")) {
            int index = str.indexOf('.');
            return index;
        }
        return 0;
    }

    public ArrayList<Part> toPartList(ArrayList<ArrayList<String>> lists) {
        ArrayList<Part> reaultList = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            Part part = new Part();
            part.setAttributeName(getAttributeName(lists.get(i).get(2), lists.get(i).get(4), lists.get(i).get(6)));
            part.setRegistryName(getAttributeName(lists.get(i).get(2), lists.get(i).get(4), lists.get(i).get(6)));
            part.setType("string");
            part.setDefalutValue(lists.get(i).get(12));
            part.setRangeEN(lists.get(i).get(18));
            part.setRangeCN(lists.get(i).get(17));
            if (lists.get(i).get(18) != null && !lists.get(i).get(18).equals("ddddddddddddd")) {
                part.setRangeSystem(getRangeSystem(lists.get(i).get(18)));
            }
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
                if (lists.get(i).get(12) != null && !lists.get(i).get(12).equals("")) {
                    unit.setDefalutValue(lists.get(i).get(12));
                }
                if (lists.get(i).get(16) != null && !"ddddddddddddd".equals(lists.get(i).get(16))) {
                    unit.setRangeSystem(getRangeSystem(lists.get(i).get(16)));
                }
                unit.setRangeEN(lists.get(i).get(16));
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

    private String getRangeSystem(String str) {
        StringBuilder sb = new StringBuilder();
        String[] strings = str.split("\\|");
        List<String> list = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].contains("-") || strings[i].contains(".") || strings[i].contains("*") || strings[i].contains("=")
                    || strings[i].contains(":") || strings[i].contains("：") || strings[i].contains(" ")) {
                list.add(indexList.get(j));
                j++;
            } else {
                list.add(strings[i]);
            }
        }
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i)).append("|");
        }
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

    public ArrayList<ArrayList<String>> getFanYiData() {
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<FanYiResponse> fanYiResponseArrayList = mapper.getAttributeNameAndRange();
        String attribute = "emxFramework.Attribute.";
        String range = "emxFramework.Range.";
        String inter = "mod interface ";
        String add_attribute = " add attribute ";
        ArrayList<String> chineseFanYi = new ArrayList<>();
        ArrayList<String> enligshFanYi = new ArrayList<>();
        ArrayList<String> chineseRangeFanYi = new ArrayList<>();
        ArrayList<String> englishRangeFanYi = new ArrayList<>();
        ArrayList<String> interMql = new ArrayList<>();
        for (FanYiResponse info : fanYiResponseArrayList) {
            if (info.getInstructions() != null && !"ddddddddddddd".equals(info.getInstructions())) {
                String ch = attribute + info.getAttributeName() + "=" + unicodeEncode(info.getAttributeChineseName() + "（" + info.getInstructions() + "）");
                chineseFanYi.add(ch);
            } else {
                String ch = attribute + info.getAttributeName() + "=" + unicodeEncode(info.getAttributeChineseName());
                chineseFanYi.add(ch);
            }
            String en = attribute + info.getAttributeName() + "=" + unicodeEncode(info.getAttributeEnglishName());
            enligshFanYi.add(en);
            String in = inter + info.getClassCode() + add_attribute + info.getAttributeName() + ";";
            interMql.add(in);
            if (info.getRangeSystem() != null && !"ddddddddddddd".equals(info.getRangeSystem())) {
                String[] rangeSystems = info.getRangeSystem().split("\\|");
                String[] rangeCNs = info.getRangeCN().split("\\|");
                String[] rangeENs = info.getRangeEN().split("\\|");
                for (int i = 0; i < rangeSystems.length; i++) {
                    String chRange = range + info.getAttributeName() + "." + rangeSystems[i] + "=" + unicodeEncode(rangeCNs[i]);
                    chineseRangeFanYi.add(chRange);
                    String enRange = range + info.getAttributeName() + "." + rangeSystems[i] + "=" + unicodeEncode(rangeENs[i]);
                    englishRangeFanYi.add(enRange);
                }
            }
        }
        result.add(chineseFanYi);
        result.add(enligshFanYi);
        result.add(chineseRangeFanYi);
        result.add(englishRangeFanYi);
        result.add(interMql);

        return result;
    }

    /*
    中文转unicode
     */
    public String unicodeEncode(String string) {
        char[] utfBytes = string.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }

    public boolean writeFanYi(ArrayList<ArrayList<String>> lists) {
        //String filename = "E:\\txt\\abc.txt";
        String filename = "E:\\txt\\ccc.txt";

        try {
            File f = new File(filename);
            if (!f.exists()) {
                f.createNewFile();
            }
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f));
            BufferedWriter writer = new BufferedWriter(write);
            for (int i = 0; i < lists.size(); i++) {
                for (int j = 0; j < lists.get(i).size(); j++) {
                    writer.write(lists.get(i).get(j) + "\r\n");
                    writer.flush();
                }
                write.write("\r\n");
                writer.flush();
            }
            write.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
