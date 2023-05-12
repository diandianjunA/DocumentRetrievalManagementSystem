package com.project.documentretrievalmanagementsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.documentretrievalmanagementsystem.common.UserHolder;
import com.project.documentretrievalmanagementsystem.dto.EsQueryDto;
import com.project.documentretrievalmanagementsystem.dto.MaterialDto;
import com.project.documentretrievalmanagementsystem.dto.ProjectDto;
import com.project.documentretrievalmanagementsystem.dto.UserDto;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.entity.Scheme;
import com.project.documentretrievalmanagementsystem.entity.User;
import com.project.documentretrievalmanagementsystem.mapper.UserMapper;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.project.documentretrievalmanagementsystem.service.ISchemeService;
import com.project.documentretrievalmanagementsystem.service.impl.MaterialServiceImpl;
import com.project.documentretrievalmanagementsystem.service.impl.ProjectServiceImpl;
import com.project.documentretrievalmanagementsystem.service.impl.SchemeServiceImpl;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.service.IService;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)

public class MaterialServiceTest {
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

    @Test
    public void addMaterial() {
        try {

            String filename = "test.txt";
            String filePath = mybasePath + filename;
            File file = new File(filePath);
            FileInputStream input = new FileInputStream(file);

            MultipartFile multipartfile = new MockMultipartFile("hello.txt", filename,
                    "text/plain",
                    IOUtils.toByteArray(input));
        } catch (Exception e) {

        }

    }

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
        }
    }

    @Test
    public void getPagedMaterialTest() throws Exception {
        int pageNum = 1;
        int pageSize = 10;
        int navSize = 5;
        String materialName = "Linux系统简介";
        Integer projectId = 1;
        String projectName = "Linux操作系统";
        // UserHolder userHolder = Mockito.mock(UserHolder.class);
        // MockedStatic<UserHolder> userHolder = Mockito.mockStatic(UserHolder.class);

        UserDto user = Mockito.mock(UserDto.class);
        // Mockito.when(userHolder.getUser()).thenReturn(user);
        Mockito.when(user.getId()).thenReturn(1);

        PageHelper.startPage(pageNum, pageSize);
        Integer currentId = user.getId();

        LambdaQueryWrapper<Material> materialLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (materialName != null && !materialName.equals("")) {
            materialLambdaQueryWrapper.or().like(Material::getName, materialName);
        }
        if (projectId != null) {
            materialLambdaQueryWrapper.and(i -> i.eq(Material::getProjectId, projectId));
        }
        if (projectName != null && !projectName.equals("")) {
            LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            projectLambdaQueryWrapper.like(Project::getName, projectName);
            for (Project project : projectService2.list(projectLambdaQueryWrapper)) {
                materialLambdaQueryWrapper.or().eq(Material::getProjectId, project.getId());
            }
        }
        materialLambdaQueryWrapper.and(i -> i.eq(Material::getUserId, currentId));
        List<Material> list = materialService2.list(materialLambdaQueryWrapper);
        ArrayList<MaterialDto> dtoList = new ArrayList<>();
        Map<Integer, Project> projectMap = projectService2.getProjectMap();
        for (Material material : list) {
            MaterialDto materialDto = new MaterialDto(material);
            materialDto.setProjectName(projectMap.get(material.getProjectId()).getName());
            dtoList.add(materialDto);
        }

    }

}
