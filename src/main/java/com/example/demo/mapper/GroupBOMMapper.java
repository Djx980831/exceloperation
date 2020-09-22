package com.example.demo.mapper;

import com.example.demo.entity.Bom;
import com.example.demo.entity.GroupInfo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface GroupBOMMapper {

    void addGroupInfo(ArrayList<GroupInfo> groupingArrayList);

    void addBOMInfo(ArrayList<Bom> bomArrayList);
    
    ArrayList<String> getBomIdsByGroupId(String groupId);

    ArrayList<String> getSonIdsByBomId(String bomId);

    ArrayList<String> getBomIdsBySonIds(List<String> sonIds);

    ArrayList<String> getSonIdsByBomIds(List<String> bomIds);
}
