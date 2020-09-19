package com.example.demo.service;

import com.example.demo.entity.Bom;
import com.example.demo.entity.GroupInfo;

import java.util.ArrayList;

public interface GroupBOMService {

    void addGrouping(ArrayList<GroupInfo> groupingArrayList);

    void addBom(ArrayList<Bom> bomArrayList);
}
