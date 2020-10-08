package com.example.demo.mapper;

import com.example.demo.entity.Person;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface PersonMapper {

    void addPerson(ArrayList<Person> personList);
}
