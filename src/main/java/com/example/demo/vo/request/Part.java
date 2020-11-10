package com.example.demo.vo.request;

import lombok.Data;

@Data
public class Part {
    private String attributeName;
    private String registryName;
    private String type;
    private String defalutValue;
    private String rangeSystem;
    private String range;
    private String rangeCN;
    private String multiline;
    private Integer maxLength;
    private String attributeChineseName;
    private String isRequire;
    private String measureUnit;
    private String defaultUnit;
    private String classCode;
    private String attributeCode;
    private String instructions;
    private String attributeEnglishName;
}
