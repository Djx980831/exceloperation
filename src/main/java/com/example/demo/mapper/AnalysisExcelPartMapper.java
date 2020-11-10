package com.example.demo.mapper;

import com.example.demo.vo.request.Part;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface AnalysisExcelPartMapper {
    void addPart(ArrayList<Part> parts);
}
