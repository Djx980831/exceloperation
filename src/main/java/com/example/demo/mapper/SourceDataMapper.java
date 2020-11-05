package com.example.demo.mapper;

import com.example.demo.entity.ECN;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface SourceDataMapper {

    void addData(ArrayList<String> data);

    void addECNData(ArrayList<ECN> data);

    int getPLMTargetAfterCount(String afterValue);

    int getPLMTargetBeforeCount(String afterString);
}
