package com.project.documentretrievalmanagementsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.documentretrievalmanagementsystem.dto.EsQueryDto;
import com.project.documentretrievalmanagementsystem.dto.FuzzyQueryDto;
import com.project.documentretrievalmanagementsystem.dto.MaterialDto;
import com.project.documentretrievalmanagementsystem.dto.UserDto;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.entity.User;
import com.project.documentretrievalmanagementsystem.service.impl.MaterialServiceImpl;
import com.project.documentretrievalmanagementsystem.service.impl.ProjectServiceImpl;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IMaterialServiceTest {
    @Value("${my.basePathT}")
    private String mybasePath;

    @InjectMocks
    private MaterialServiceImpl materialService;

    @InjectMocks
    ProjectServiceImpl projectService;
    @Autowired
    IMaterialService materialService2;
    @Autowired
    IProjectService projectService2;
    @Autowired
    IUserService userService;
    @Mock
    HttpSession session;


   /* @Test
    public void addMaterial() {
        try {

            String filename = "test.txt";
            String filePath = mybasePath + filename;
            File file = new File(filePath);
            FileInputStream input = new FileInputStream(file);

            MultipartFile multipartfile = new MockMultipartFile("hello.txt", filename,
                    "text/plain",
                    IOUtils.toByteArray(input));
            materialService2.addMaterial("test", 1, multipartfile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }*/

    @Test
    public void deleteMaterial() {
        Integer id = 10;
        Material material = materialService2.getById(id);
        String filename = "test.txt";
        String filePath = mybasePath + filename;
        try {
            File file = new File(material.getLocation());
            if (file.exists()) {
                file.delete();
            }
            materialService2.removeById(id);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void fuzzyQuery() throws Exception {
        EsQueryDto esQueryDto = new EsQueryDto();
        esQueryDto.setFrom(2);
        esQueryDto.setSize(5);
        esQueryDto.setWord("");
        FuzzyQueryDto materialDtos = materialService.fuzzyQuery(esQueryDto);
    }

    @Test
    void getPagedMaterial() {
        int pageNum = 1;
        int pageSize = 10;
        int navSize = 5;
        String materialName = "Linux系统简介";
        Integer projectId = 1;
        String projectName = "Linux操作系统";
        HashMap<String, String> map = new HashMap<>();
        map.put("userName","admin");
        map.put("password","123456");
        User user = userService.login(map, session);
        PageInfo<MaterialDto> pagedMaterial = materialService2.getPagedMaterial(pageNum, pageSize, navSize, materialName, projectId, projectName);
        for (MaterialDto materialDto : pagedMaterial.getList()) {
            System.out.println(materialDto);
        }
    }

    @Test
    void getMaterialMap() {
        Map<Integer, Material> materialMap = materialService2.getMaterialMap();
        System.out.println(materialMap);
    }

    @Test
    void similarity() throws IOException {
        Integer materialId = 1;
        Integer projectId = 1;
        double similarity = materialService2.similarity(materialId, projectId);
        System.out.println(similarity);
    }
}