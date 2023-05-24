package com.project.documentretrievalmanagementsystem;

import com.github.pagehelper.PageInfo;
import com.project.documentretrievalmanagementsystem.controller.MaterialController;
import com.project.documentretrievalmanagementsystem.dto.EsQueryDto;
import com.project.documentretrievalmanagementsystem.dto.FuzzyQueryDto;
import com.project.documentretrievalmanagementsystem.dto.MaterialDto;
import com.project.documentretrievalmanagementsystem.dto.ProjectDto;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.mapper.MaterialMapper;
import com.project.documentretrievalmanagementsystem.mapper.UserMapper;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.project.documentretrievalmanagementsystem.service.ISchemeService;
import com.project.documentretrievalmanagementsystem.service.impl.ProjectServiceImpl;
import com.project.documentretrievalmanagementsystem.service.impl.SchemeServiceImpl;
import com.project.documentretrievalmanagementsystem.utils.CreateFolder;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(org.springframework.test.context.junit4.SpringRunner.class)
class DocumentRetrievalManagementSystemApplicationTests {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    IMaterialService materialService;
    @Autowired
    MaterialMapper materialMapper;
    @Autowired
    SchemeServiceImpl schemeService;
    @Autowired
    ProjectServiceImpl projectService;

    @Test
    void contextLoads() {
        PageInfo<MaterialDto> pagedMaterial = materialService.getPagedMaterial(2, 3, 5, null, null, null);
        System.out.println(pagedMaterial);
    }

    @Test
    void swaggerDoc() throws MalformedURLException {
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                .withMarkupLanguage(MarkupLanguage.MARKDOWN)
                // 输出Markdown格式，可以修改文档类型，例MarkupLanguage.ASCIIDOC
                .build();

        Swagger2MarkupConverter.from(new URL("http://localhost:8070/v2/api-docs"))
                .withConfig(config)
                .build()
                .toFolder(Paths.get("src/docs/markdown/generated"));
        //这是生成的文档位置，可以修改为输出单文件，toFolder改为toFile
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
    void generateSummary() throws Exception {
        String summary = schemeService.generateSummary( 10, 50);
        System.out.println(summary);
    }

   /* @Test
    void downloadDocx() {
        String Name = schemeService.downloadDocx(14);
        System.out.println(Name);
    }*/

   /* @Test
    void similarity() throws Exception {
        double similarity = schemeService.similarity(4, 6);
        System.out.println(similarity);
    }*/

    @Test
    void deleteMaterial() throws Exception {
        materialService.deleteById(52);

    }

    @Test
    void deleteDir() throws Exception {
        schemeService.deleteCategoryFolder("D:\\code\\source\\user\\mhc\\重构测试1\\测试目录1");
    }

    @Test
    void similarity() throws Exception {
        double similarity = projectService.similarity(3, 4);
        System.out.println(similarity);
    }

    @Test
    void projectAnalyze() throws Exception {
        List<ProjectDto> projectDtos = projectService.projectAnalyze(1);
        for (ProjectDto projectDto : projectDtos) {
            System.out.println(projectDto);
        }
    }
}