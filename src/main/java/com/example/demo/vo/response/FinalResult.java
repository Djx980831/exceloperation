package com.example.demo.vo.response;

import lombok.Data;

import java.util.ArrayList;

@Data
public class FinalResult {
    private String gid;
    private Boolean flag;
    private ArrayList<String> errorList;
}
