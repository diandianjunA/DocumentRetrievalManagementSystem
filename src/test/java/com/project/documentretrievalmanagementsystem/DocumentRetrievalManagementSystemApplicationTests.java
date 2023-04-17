package com.project.documentretrievalmanagementsystem;

import com.project.documentretrievalmanagementsystem.entity.User;
import com.project.documentretrievalmanagementsystem.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DocumentRetrievalManagementSystemApplicationTests {

    @Autowired
    private UserMapper userMapper;
    @Test
    void contextLoads() {
        for (User user : userMapper.selectList(null)) {
            System.out.println(user);
        }
    }

}
