package com.example.demo.entity;

import lombok.Data;

import java.util.ArrayList;

@Data
public class FinalResult {
    private String gid;
    private Boolean flag;
    private ArrayList<String> errorList;
}
