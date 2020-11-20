package com.example.demo.entity;


import lombok.Data;

/**
 * @param
 * @Description
 * @Author dongjingxiong
 * @return
 * @Date 2020-10-15 23:11
 */
@Data
public class Dish {
    private Integer id;
    private String resId;
    private String name;
    private Integer isShollot;
    private Integer isHot;
    private Integer isSoySauce;
    private Integer isMustard;
    private Integer isGarlic;
}
