package com.example.demo.service.impl;

import com.example.demo.entity.Student;
import com.microsoft.schemas.office.visio.x2012.main.CellType;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.xml.ws.ServiceMode;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @Author：dong
 * @Description：excel文件解析类
 */
@Service
public class AnalysisService {

    /**
     * 获取并解析excel文件，返回一个二维集合
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
//                    cell.add(sheetRow.getCell(j).getStringCellValue());
//                    sheetRow.getCell(j)
                    Cell cell = row.getCell(j);
                    String cellValue = "";
                    if (null != cell) {
                        // 以下是判断数据的类型
                        switch (cell.getCellType()) {
                            case HSSFCell.CELL_TYPE_NUMERIC: // 数字
                                DecimalFormat df = new DecimalFormat("0");
                                cellValue = df.format(cell.getNumericCellValue());
                                break;
                            case HSSFCell.CELL_TYPE_STRING: // 字符串
                                cellValue = cell.getStringCellValue();
                                break;
                            case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
                                cellValue = cell.getBooleanCellValue() + "";
                                break;
                            case HSSFCell.CELL_TYPE_FORMULA: // 公式
                                cellValue = cell.getCellFormula() + "";
                                break;
                            case HSSFCell.CELL_TYPE_BLANK: // 空值
                                cellValue = "";
                                break;
                            case HSSFCell.CELL_TYPE_ERROR: // 故障
                                cellValue = "非法字符";
                                break;
                            default:
                                cellValue = "未知类型";
                                break;
                        }
                    }
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
     * @param fileName 文件路径
     * @return excel2007及以上版本返回true，excel2007以下版本返回false
     */
    private static boolean judegExcelEdition(String fileName){
        if (fileName.matches("^.+\\.(?i)(xls)$")){
            return false;
        }else {
            return true;
        }
    }

    private static Student listToStudent(ArrayList list) {
        Student student = new Student();
        student.setStudentId(list.get(0).toString());
        student.setStudentName(list.get(1).toString());
        student.setSex(list.get(2).toString().trim() == "男" ? "M" : "F");
        student.setGrade(list.get(3).toString());
        student.setGradeClass(list.get(4).toString());
        student.setArea(list.get(5).toString());

        return student;
    }

    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        // resolveLazily属性启用是为了推迟文件解析，以在在UploadAction中捕获文件大小异常
        resolver.setResolveLazily(true);
        resolver.setMaxInMemorySize(40960);
        // 上传文件大小 5G
        resolver.setMaxUploadSize(5 * 1024 * 1024 * 1024);
        return resolver;
    }
}