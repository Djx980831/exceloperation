package com.example.demo.controller;

import com.example.demo.entity.Bom;
import com.example.demo.entity.GroupInfo;
import com.example.demo.service.GroupBOMService;
import com.example.demo.service.impl.AnalysisService;
import com.example.demo.util.ErrorInfo;
import com.example.demo.util.RpcResponse;
import com.example.demo.vo.response.FinalResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@RestController
@RequestMapping("/groupBom")
public class GroupBOMController {

    @Autowired
    private GroupBOMService service;

    @Autowired
    private AnalysisService analysisService;

    @PostMapping("/addGroup")
    @ResponseBody
    public RpcResponse<String> addGroup(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }

        ArrayList<ArrayList<GroupInfo>> groupInfoArrayList = AnalysisService.analysisGroup(file);
        for (ArrayList<GroupInfo> infos : groupInfoArrayList) {
            service.addGrouping(infos);
        }
        return RpcResponse.success("success");
    }

    @PostMapping("/addBom")
    @ResponseBody
    public RpcResponse<String> addBom(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        ArrayList<ArrayList<Bom>> bomInfoArrayList = AnalysisService.analysisBom(file);
        for (ArrayList<Bom> infos : bomInfoArrayList) {
            service.addBom(infos);
        }
        return RpcResponse.success("success");
    }

    @PostMapping("/checkGroup")
    public RpcResponse<ArrayList<FinalResult>> checkGroup(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        ArrayList<ArrayList<String>> arrayLists = AnalysisService.getGroupAndBomList(file);
        HashSet<String> groupSet = new HashSet<>();
        groupSet.addAll(arrayLists.get(1));
        ArrayList<String> groupList = new ArrayList<>();
        groupList.addAll(groupSet);
        HashMap<String, Boolean> booleanHashMap = analysisService.chenkGroup(groupList);
        ArrayList<FinalResult> booleanHashSet = analysisService.secondGroupCheck(booleanHashMap);
        return RpcResponse.success(booleanHashSet);
    }

    @PostMapping("/checkGroupFromDataBases")
    public RpcResponse<ArrayList<FinalResult>> checkGroupFromDataBases() {
        ArrayList<String> groupIdsLists = service.getAllGroupIds();
        HashMap<String, Boolean> booleanHashMap = analysisService.chenkGroup(groupIdsLists);
        ArrayList<FinalResult> booleanHashSet = analysisService.secondGroupCheck(booleanHashMap);
        return RpcResponse.success(booleanHashSet);
    }

    @PostMapping("/secondGroupCheck")
    public RpcResponse<ArrayList<FinalResult>> secondGroupCheck(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        //ArrayList<ArrayList<String>> arrayLists = AnalysisService.getGroupAndBomList(file);

        HashMap<String, Boolean> booleanHashMap = new HashMap<>();
        booleanHashMap.put( "g254", false);
        booleanHashMap.put( "g7", false);
        booleanHashMap.put( "g1349", false);
        booleanHashMap.put( "g510", false);
        ArrayList<FinalResult> result = analysisService.secondGroupCheck(booleanHashMap);
        return RpcResponse.success(result);
    }

    @PostMapping("/checkNotSameGroup")
    public RpcResponse<HashMap<String, Boolean>> checkNotSameGroup() {
        ArrayList<String> groupIdsLists = service.getAllGroupIds();
        HashMap<String, Boolean> result = analysisService.isNotSameGroup(groupIdsLists);
        return RpcResponse.success(result);
    }

    @PostMapping("/checkInv")
    public RpcResponse<ArrayList<String>> checkInv(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        ArrayList<String> result = analysisService.checkInv(analysisService.getInvAndItemsSet(file));
        return RpcResponse.success(result);
    }

    @PostMapping("/writeFile")
    public RpcResponse<String> writeFile(MultipartFile file) {
        if (null == file) {
            return RpcResponse.error(new ErrorInfo(101, "未上传文件"));
        }
        analysisService.writeFile(analysisService.getStringList(analysisService.assembBomId(file)));
        return RpcResponse.success("success");
    }

    @PostMapping("/getRowIndex")
    public RpcResponse<ArrayList<Integer>> getRowIndex(MultipartFile source, MultipartFile target) {
        return RpcResponse.success(analysisService.getRowIndex(source, target));
    }
}
