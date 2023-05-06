package com.project.documentretrievalmanagementsystem;

import com.github.pagehelper.PageInfo;
import com.project.documentretrievalmanagementsystem.dto.EsQueryDto;
import com.project.documentretrievalmanagementsystem.dto.MaterialDto;
import com.project.documentretrievalmanagementsystem.dto.ProjectDto;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Scheme;
import com.project.documentretrievalmanagementsystem.entity.User;
import com.project.documentretrievalmanagementsystem.mapper.UserMapper;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.project.documentretrievalmanagementsystem.service.ISchemeService;
import com.project.documentretrievalmanagementsystem.service.impl.ProjectServiceImpl;
import com.project.documentretrievalmanagementsystem.service.impl.SchemeServiceImpl;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

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
    ISchemeService schemeService;
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
        List<MaterialDto> materialDtos = materialService.fuzzyQuery(esQueryDto);
        for (MaterialDto materialDto : materialDtos) {
            System.out.println(materialDto);
        }
    }

    @Test
    void generateSummary() throws Exception {
        String summary = schemeService.generateSummary( 10);
        System.out.println(summary);
    }


    @Test
    void downloadScheme() throws Exception {
        List list = schemeService.getSchemeByMaterialId(4);
        XSSFWorkbook scheme = schemeService.downloadExcel(list);
        System.out.println(scheme.toString());
    }

   /* @Test
    void similarity() throws Exception {
        double similarity = schemeService.similarity(4, 6);
        System.out.println(similarity);
    }*/

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