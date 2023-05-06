package com.project.documentretrievalmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.documentretrievalmanagementsystem.dto.ProjectDto;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.mapper.ProjectMapper;
import com.project.documentretrievalmanagementsystem.service.FileService;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.project.documentretrievalmanagementsystem.service.IProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.documentretrievalmanagementsystem.utils.FileRdWt;
import com.project.documentretrievalmanagementsystem.utils.TransTotxtS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-14
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {
    @Autowired
    ProjectMapper projectMapper;

    @Lazy
    @Autowired
    IMaterialService materialService;

    @Override
    public Map<Integer, Project> getProjectMap() {
        return projectMapper.getProjectMap();
    }


    @Override
    public double similarity(Integer projectIdA, Integer projectIdB) throws IOException {
        //查询资料数据库，获取项目id为projectIdA的资料
        QueryWrapper<Material> wrapperA = new QueryWrapper<>();
        QueryWrapper<Material> wrapperB = new QueryWrapper<>();
        wrapperA.eq("project_id", projectIdA);
        wrapperB.eq("project_id", projectIdB);
        //获取项目A的所有资料
        List<Material> materialListA = materialService.list(wrapperA);
        List<Material> materialListB = materialService.list(wrapperB);
        if(materialListA.isEmpty()||materialListB.isEmpty()){
            return 0;
        }
        //读取项目A的所有资料的vector拼接成一个字符串
        StringBuilder vecA = new StringBuilder(new StringBuffer());
        for (Material material : materialListA) {
            //获取资料的vector
            String vectorLocation = material.getVectorLocation();
            //读取vector文件
            StringBuffer vectorA = FileRdWt.readTxt(vectorLocation);
            //将vector拼接成一个字符串
            vecA.append(vectorA).append(",");
        }

        //读取项目B的所有资料的vector拼接成一个字符串
        StringBuilder vecB = new StringBuilder(new StringBuffer());
        for (Material material : materialListB) {
            //获取资料的vector
            String vectorLocation = material.getVectorLocation();
            //读取vector文件
            StringBuffer vectorB = FileRdWt.readTxt(vectorLocation);
            //将vector拼接成一个字符串
            vecB.append(vectorB).append(",");
        }
        //裁剪使得满足余弦相似度计算的格式
        vecA = new StringBuilder(vecA.substring(0, vecA.length() - 1));
        vecB = new StringBuilder(vecB.substring(0, vecB.length() - 1));

        //将字符串转换为double数组
        String[] vecAstr = vecA.toString().split(",");
        String[] vecBstr = vecB.toString().split(",");
        double[] vecAdouble = new double[vecAstr.length];
        double[] vecBdouble = new double[vecBstr.length];
        for (int i = 0; i < vecAstr.length; i++) {
            vecAdouble[i] = Double.parseDouble(vecAstr[i]);
        }
        for (int i = 0; i < vecBstr.length; i++) {
            vecBdouble[i] = Double.parseDouble(vecBstr[i]);
        }
        //计算两个向量的余弦相似度
        double similarity = TransTotxtS.cosineSimilarity(vecAdouble, vecBdouble);
        //将小数转换为百分数
        //String similaritystr = TransTotxtS.doubleToPercent(similarity);
        //System.out.println(similaritystr);
        //返回相似度
        return similarity < 1 ? similarity : 1;
    }

    @Override
    //选型分析，根据项目id获取与该项目相似度最高的五个项目信息以及相似度
    public List<ProjectDto> projectAnalyze(Integer projectId) throws IOException {
        //获取项目map
        Map<Integer, Project> projectMap = projectMapper.getProjectMap();
        //获取项目id为projectId的项目
        Project project = projectMap.get(projectId);
        //遍历项目map，计算相似度，将相似度最高的五个项目放入list中
        List<ProjectDto> projectDtoList = new ArrayList<>();
        for (Map.Entry<Integer, Project> entry : projectMap.entrySet()) {
            if (!Objects.equals(entry.getKey(), projectId)) {
                //获取项目id为entry.getKey()的项目
                Project projectT = projectMap.get(entry.getKey());
                ProjectDto projectDto = new ProjectDto();
                //获取信息和相似度
                projectDto.setId(projectT.getId());
                projectDto.setName(projectT.getName());
                projectDto.setCategory(projectT.getCategory());
                projectDto.setRemark(projectT.getRemark());
                projectDto.setSimilarity(similarity(projectId, entry.getKey()));
                projectDtoList.add(projectDto);
            }
        }
        //对list进行排序
        Collections.sort(projectDtoList, new Comparator<ProjectDto>() {
            @Override
            public int compare(ProjectDto o1, ProjectDto o2) {
                //降序
                if (o1.getSimilarity() < o2.getSimilarity()) {
                    return 1;
                }
                if (o1.getSimilarity() == o2.getSimilarity()) {
                    return 0;
                }
                return -1;
            }
        });
        //如果项目的数量不足5个，则返回全部项目
        if (projectDtoList.size() < 5) {
            return projectDtoList;
            //如果项目的数量大于5个，则返回前五个项目
        } else {
           /* List<ProjectDto> projectDtoList1 = new ArrayList<>();
            for(int i=0;i<5;i++){
                projectDtoList1.add(projectDtoList.get(i));
            }
            return projectDtoList1;    */
            //切片方法
            return projectDtoList.subList(0, 5);
        }
    }
}
