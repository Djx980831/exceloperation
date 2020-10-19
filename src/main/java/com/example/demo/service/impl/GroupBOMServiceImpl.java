package com.example.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Bom;
import com.example.demo.entity.GroupInfo;
import com.example.demo.mapper.GroupBOMMapper;
import com.example.demo.service.GroupBOMService;
import com.example.demo.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GroupBOMServiceImpl implements GroupBOMService {

    @Autowired
    private GroupBOMMapper groupBOMMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void addGrouping(ArrayList<GroupInfo> groupingArrayList) {
        groupBOMMapper.addGroupInfo(groupingArrayList);
    }

    @Override
    public void addBom(ArrayList<Bom> bomArrayList) {
        groupBOMMapper.addBOMInfo(bomArrayList);
    }

    @Override
    public ArrayList<String> getAllGroupIds() {
        String key = "allGroupIds";
        ArrayList<String> groupIdsList = new ArrayList<>(100);
        if (redisUtils.get(key) != null) {
            groupIdsList = (ArrayList) JSONObject.parseObject((String) redisUtils.get(key), ArrayList.class);
            return groupIdsList;
        }
        groupIdsList = groupBOMMapper.getAllGroupIds();
        redisUtils.set(key, JSONObject.toJSONString(groupIdsList));
        return groupIdsList;
    }
}
