package com.example.demo.mapper;

import com.example.demo.vo.request.Part;
import com.example.demo.vo.response.FanYiResponse;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface AnalysisExcelPartMapper {
    void addPart(ArrayList<Part> parts);

    ArrayList<FanYiResponse> getAttributeNameAndRange();
}
