package com.example.demo.vo.response;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ECNResult {

    private Boolean flag;
    private ResultInfo resultInfo;

    @Data
    public static class ResultInfo{
        private Integer rowNum ;
        private Boolean plmResult;
        private Integer plmBefore;
        private Integer plmAfter;
        private Boolean sapResult;
        private Integer sapBefore;
        private Integer sapAfter;
    }
}
