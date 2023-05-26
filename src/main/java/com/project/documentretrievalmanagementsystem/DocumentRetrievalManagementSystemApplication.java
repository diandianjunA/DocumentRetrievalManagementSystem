package com.project.documentretrievalmanagementsystem;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DocumentRetrievalManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentRetrievalManagementSystemApplication.class, args);
    }
}
