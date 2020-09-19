package com.example.demo.mapper;

import com.example.demo.entity.Bom;
import com.example.demo.entity.GroupInfo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface GroupBOMMapper {

    void addGroupInfo(ArrayList<GroupInfo> groupingArrayList);

    void addBOMInfo(ArrayList<Bom> bomArrayList);
}
