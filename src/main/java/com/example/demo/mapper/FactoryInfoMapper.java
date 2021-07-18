package com.example.demo.mapper;

import com.example.demo.entity.FactoryInfo;
import com.example.demo.vo.request.FactoryVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FactoryInfoMapper {

    void addFactoryInfo(FactoryVO vo);

    FactoryInfo getInfoByCountryAndName(@Param("country") String country, @Param("name") String name);

    void updateInfo(FactoryInfo info);
}
