package com.example.demo.mapper;

import com.example.demo.entity.CountryData;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface CountryDataMapper {

    void addCountry(ArrayList<CountryData> data);
}
