package com.project.documentretrievalmanagementsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.project.documentretrievalmanagementsystem.dto.EsQueryDto;
import com.project.documentretrievalmanagementsystem.dto.MaterialDto;
import com.project.documentretrievalmanagementsystem.dto.ProjectDto;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.entity.Scheme;
import com.project.documentretrievalmanagementsystem.entity.User;
import com.project.documentretrievalmanagementsystem.mapper.ProjectMapper;
import com.project.documentretrievalmanagementsystem.mapper.UserMapper;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.project.documentretrievalmanagementsystem.service.ISchemeService;
import com.project.documentretrievalmanagementsystem.service.impl.ProjectServiceImpl;
import com.project.documentretrievalmanagementsystem.service.impl.SchemeServiceImpl;
import com.project.documentretrievalmanagementsystem.utils.FileRdWt;
import com.project.documentretrievalmanagementsystem.utils.TransTotxtS;

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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.parsing.Location;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.when;

import java.io.*;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)

public class ProjectServiceTest {
    @Autowired
    ProjectMapper projectMapper;
    @Value("${my.basePathT}")
    private String mybasePath;
    @Autowired
    ProjectServiceImpl projectService;
    @Autowired
    SchemeServiceImpl schemeService;

    // @Autowired
    // ProjectMapper projectMapper;

    @Lazy
    @Autowired
    IMaterialService materialService;
    @Value("${my.basePath}")
    private String basePath;

    @Test
    public void combineTest() throws Exception {
        Integer projectId = 1;
        String filepath = basePath + "test.txt";
        List<Material> materialList = new ArrayList<>();
        Material material1 = new Material();
        material1.setLocation(filepath);
        String content1 = "content1";
        materialList.add(material1);
        // when(FileRdWt.readFile(filepath)).thenReturn(content1);

        StringBuilder sb = new StringBuilder();
        for (Material material : materialList) {
            String content = "content1";
            // String content = FileRdWt.readFile(material.getLocation());
            sb.append(content).append("\n");
        }
        String filePath = basePath + projectId + ".txt";
        FileRdWt.writeFile(filePath, sb.toString());
    }

    @Test
    public void similarityTest() {
        Integer projectIdA = 4;
        Integer projectIdB = 6;
        QueryWrapper<Material> wrapperA = new QueryWrapper<>();
        QueryWrapper<Material> wrapperB = new QueryWrapper<>();
        wrapperA.eq("project_id", projectIdA);
        wrapperB.eq("project_id", projectIdB);
        // 获取项目A的所有资料
        List<Material> materialListA = materialService.list(wrapperA);
        List<Material> materialListB = materialService.list(wrapperB);
        // 将所有资料都分别放入一个txt文件中
        // projectService.combine(projectIdA, materialListA);

        // projectService.combine(projectIdB, materialListB);
        // 路径
        String filePathA = basePath + projectIdA + ".txt";
        String filePathB = basePath + projectIdB + ".txt";

        // 获取第一篇文章的向量
        // try {
        // // 开启了命令执行器，输入指令执行python脚本
        // Process processA = Runtime.getRuntime()
        // .exec("E:\\develop\\Anaconda\\Anaconda3\\envs\\pytorch\\python.exe " +
        // "D:\\MHC\\pycharm\\pythonProject\\predict.py " +
        // "--model_path D:\\MHC\\pycharm\\pythonProject\\cpt-base " +
        // "--file_path " + filePathA + " " +
        // "--sum_min_len 50 " +
        // "--gen_vec 1");

        // // 这种方式获取返回值的方式是需要用python打印输出，然后java去获取命令行的输出，在java返回
        // InputStreamReader ir = new InputStreamReader(processA.getInputStream(),
        // "GB2312");
        // LineNumberReader input = new LineNumberReader(ir);
        // // 读取命令行的输出
        // vecA = input.readLine();
        // System.out.println(vecA);
        // input.close();
        // ir.close();
        // } catch (IOException e) {
        // System.out.println("调用python脚本并读取结果时出错：" + e.getMessage());
        // }

        // // 获取第二篇文章的向量
        // try {
        // // 开启了命令执行器，输入指令执行python脚本
        // Process processB = Runtime.getRuntime()
        // .exec("E:\\develop\\Anaconda\\Anaconda3\\envs\\pytorch\\python.exe " +
        // "D:\\MHC\\pycharm\\pythonProject\\predict.py " +
        // "--model_path D:\\MHC\\pycharm\\pythonProject\\cpt-base " +
        // "--file_path " + filePathB + " " +
        // "--sum_min_len 50 " +
        // "--gen_vec 1");

        // // 这种方式获取返回值的方式是需要用python打印输出，然后java去获取命令行的输出，在java返回
        // InputStreamReader ir = new InputStreamReader(processB.getInputStream(),
        // "GB2312");
        // LineNumberReader input = new LineNumberReader(ir);
        // // 读取命令行的输出
        // vecB = input.readLine();
        // input.close();
        // ir.close();
        // } catch (IOException e) {
        // System.out.println("调用python脚本并读取结果时出错：" + e.getMessage());
        // }
        String vecA = "a,z";

        String vecB = "a,b";

        // 将字符串转换为double数组
        String[] vecAstr = vecA.split(",");
        String[] vecBstr = vecB.split(",");
        double[] vecAdouble = new double[vecAstr.length];
        double[] vecBdouble = new double[vecBstr.length];
        for (int i = 1; i < vecAstr.length - 1; i++) {
            vecAdouble[i] = Double.parseDouble(vecAstr[i]);
        }
        for (int i = 1; i < vecBstr.length - 1; i++) {
            vecBdouble[i] = Double.parseDouble(vecBstr[i]);
        }
        // 计算两个向量的余弦相似度
        double similarity = TransTotxtS.cosineSimilarity(vecAdouble, vecBdouble);
        // 将小数转换为百分数
        // String similaritystr = TransTotxtS.doubleToPercent(similarity);
        // System.out.println(similaritystr);
        // 返回相似度

    }

    @Test
    public void projectAnalyzeTest() {
        // 获取项目map
        Integer projectId = 1;
        Map<Integer, Project> projectMap = projectMapper.getProjectMap();
        // 获取项目id为projectId的项目
        Project project = projectMap.get(projectId);
        // 遍历项目map，计算相似度，将相似度最高的五个项目放入list中
        // List<ProjectDto> projectDtoList = new ArrayList<>();
        // for (Map.Entry<Integer, Project> entry : projectMap.entrySet()) {
        // if (entry.getKey() != projectId) {
        // // 获取项目id为entry.getKey()的项目
        // Project projectT = projectMap.get(entry.getKey());
        // ProjectDto projectDto = new ProjectDto();
        // // 获取信息和相似度
        // projectDto.setId(projectT.getId());
        // projectDto.setName(projectT.getName());
        // projectDto.setCategory(projectT.getCategory());
        // projectDto.setRemark(projectT.getRemark());
        // projectDto.setSimilarity(similarity(projectId, entry.getKey()));
        // projectDtoList.add(projectDto);
        // }
        // }
        // // 对list进行排序
        // Collections.sort(projectDtoList, new Comparator<ProjectDto>() {
        // @Override
        // public int compare(ProjectDto o1, ProjectDto o2) {
        // // 降序
        // if (o1.getSimilarity() < o2.getSimilarity()) {
        // return 1;
        // }
        // if (o1.getSimilarity() == o2.getSimilarity()) {
        // return 0;
        // }
        // return -1;
        // }
        // });
        // 如果项目的数量不足5个，则返回全部项目
    }
}
