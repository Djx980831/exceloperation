package com.example.demo.service.impl;

import com.example.demo.entity.Bom;
import com.example.demo.entity.GroupInfo;
import com.example.demo.mapper.GroupBOMMapper;
import com.example.demo.service.GroupBOMService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class GroupBOMServiceImpl implements GroupBOMService {

    @Autowired
    private GroupBOMMapper groupBOMMapper;

    @Override
    public void addGrouping(ArrayList<GroupInfo> groupingArrayList) {
        groupBOMMapper.addGroupInfo(groupingArrayList);
    }

    @Override
    public void addBom(ArrayList<Bom> bomArrayList) {
        groupBOMMapper.addBOMInfo(bomArrayList);
    }
}
