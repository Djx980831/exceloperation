package com.example.demo.service.impl;

import com.example.demo.entity.CountryData;
import com.example.demo.mapper.CountryDataMapper;
import com.example.demo.service.CountryDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CountryDataServiceImpl implements CountryDataService {

    @Autowired
    private CountryDataMapper mapper;

    @Override
    public void addCountry(ArrayList<CountryData> data) {
        mapper.addCountry(data);
    }
}
