package com.example.demo.util;

public class JudegExcelEdition {

    public static boolean judegExcelEdition(String fileName) {
        if (fileName.matches("^.+\\.(?i)(xls)$")) {
            return false;
        } else {
            return true;
        }
    }
}
