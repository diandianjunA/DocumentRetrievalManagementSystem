package com.project.documentretrievalmanagementsystem;

import com.project.documentretrievalmanagementsystem.dto.EsQueryDto;
import com.project.documentretrievalmanagementsystem.dto.MaterialDto;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Scheme;
import com.project.documentretrievalmanagementsystem.entity.User;
import com.project.documentretrievalmanagementsystem.mapper.UserMapper;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.project.documentretrievalmanagementsystem.service.ISchemeService;
import com.project.documentretrievalmanagementsystem.service.impl.SchemeServiceImpl;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class DocumentRetrievalManagementSystemApplicationTests {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    IMaterialService materialService;
    @Autowired
    ISchemeService schemeService;

    @Test
    void contextLoads() {
        for (User user : userMapper.selectList(null)) {
            System.out.println(user);
        }
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
        esQueryDto.setFrom(0);
        esQueryDto.setSize(5);
        esQueryDto.setWord("系统");
        List<MaterialDto> materialDtos = materialService.fuzzyQuery(esQueryDto);
        for (MaterialDto materialDto : materialDtos) {
            System.out.println(materialDto);
        }
    }

    @Test
    void generateSummary() throws Exception {
        String summary = schemeService.generateSummary( 4);
        System.out.println(summary);
    }
}