package com.project.documentretrievalmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.documentretrievalmanagementsystem.common.UserHolder;
import com.project.documentretrievalmanagementsystem.dto.MaterialSimilarityDto;
import com.project.documentretrievalmanagementsystem.dto.ProjectDto;
import com.project.documentretrievalmanagementsystem.dto.SimilarityDto;
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

    //使用MyBatis框架，将获取到的项目列表封装成Map对象并返回
    @Override
    public Map<Integer, Project> getProjectMap() {
        return projectMapper.getProjectMap();
    }


    @Override
    public double similarity(Integer projectIdA, Integer projectIdB) throws IOException {
        //使用MyBatis-Plus提供的条件构造器查询资料数据库，获取项目id为projectIdA的资料
        QueryWrapper<Material> wrapperA = new QueryWrapper<>();
        QueryWrapper<Material> wrapperB = new QueryWrapper<>();
        Integer id = UserHolder.getUser().getId();
        wrapperA.eq("project_id", projectIdA);
        wrapperA.eq("user_id", id);
        wrapperB.eq("project_id", projectIdB);
        wrapperB.eq("user_id", id);
        //获取项目A的所有资料
        List<Material> materialListA = materialService.list(wrapperA);
        List<Material> materialListB = materialService.list(wrapperB);
        if(materialListA.isEmpty()||materialListB.isEmpty()){
            return 0;
        }
        double[] vecA = new double[768];
        for (Material material : materialListA) {
            //获取资料的vector
            String vectorLocation = material.getVectorLocation();
            //读取vector文件
            StringBuffer vectorA = FileRdWt.readTxt(vectorLocation);
            String[] temp = vectorA.toString().split(",");
            for (int i = 0; i < temp.length; i++) {
                vecA[i] += Double.parseDouble(temp[i]);
            }
        }

        //读取项目B的所有资料的vector拼接成一个字符串
        double[] vecB = new double[768];
        for (Material material : materialListB) {
            //获取资料的vector
            String vectorLocation = material.getVectorLocation();
            //读取vector文件
            StringBuffer vectorB = FileRdWt.readTxt(vectorLocation);
            String[] temp = vectorB.toString().split(",");
            for (int i = 0; i < temp.length; i++) {
                vecB[i] += Double.parseDouble(temp[i]);
            }
        }
        //计算两个向量的余弦相似度
        double similarity = TransTotxtS.cosineSimilarity(vecA, vecB);
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
        Integer id = UserHolder.getUser().getId();
        //获取项目id为projectId的项目
        Project project = projectMap.get(projectId);
        //遍历项目map，计算相似度，将相似度最高的五个项目放入list中
        List<ProjectDto> projectDtoList = new ArrayList<>();
        for (Map.Entry<Integer, Project> entry : projectMap.entrySet()) {
            //确保项目是当前用户线程下的用户所拥有
            if(!Objects.equals(entry.getValue().getUserId(), id)){
                continue;
            }
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

    @Override
    public SimilarityDto similarityAnalyze(Integer project1Id, Integer project2Id) throws IOException {
        SimilarityDto similarityDto = new SimilarityDto();
        ArrayList<MaterialSimilarityDto> materialSimilarityDtos1 = new ArrayList<>();
        ArrayList<MaterialSimilarityDto> materialSimilarityDtos2 = new ArrayList<>();
        LambdaQueryWrapper<Material> materialLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        //条件查询，获取项目1的所有资料
        materialLambdaQueryWrapper1.eq(Material::getProjectId, project1Id);
        List<Material> materials1 = materialService.list(materialLambdaQueryWrapper1);

        LambdaQueryWrapper<Material> materialLambdaQueryWrapper2 = new LambdaQueryWrapper<>();
        materialLambdaQueryWrapper2.eq(Material::getProjectId, project2Id);
        List<Material> materials2 = materialService.list(materialLambdaQueryWrapper2);
        double[] vecA = new double[768];
        for (Material material : materials1) {
            //获取资料的vector
            String vectorLocation = material.getVectorLocation();
            //读取vector文件
            StringBuffer vectorA = FileRdWt.readTxt(vectorLocation);
            String[] temp = vectorA.toString().split(",");
            for (int i = 0; i < temp.length; i++) {
                vecA[i] += Double.parseDouble(temp[i]);
            }
        }
        double[] vecB = new double[768];
        for (Material material : materials2) {
            //获取资料的vector
            String vectorLocation = material.getVectorLocation();
            //读取vector文件
            StringBuffer vectorB = FileRdWt.readTxt(vectorLocation);
            String[] temp = vectorB.toString().split(",");
            for (int i = 0; i < temp.length; i++) {
                vecB[i] += Double.parseDouble(temp[i]);
            }
        }
        for (Material material:materials1){
            MaterialSimilarityDto materialSimilarityDto = new MaterialSimilarityDto();
            materialSimilarityDto.setId(material.getId());
            materialSimilarityDto.setName(material.getName());
            materialSimilarityDto.setUserId(material.getUserId());
            materialSimilarityDto.setLocation(material.getLocation());
            materialSimilarityDto.setProjectId(material.getProjectId());
            materialSimilarityDto.setVectorLocation(material.getVectorLocation());
            String vecLoc = material.getVectorLocation();
            StringBuffer vector = FileRdWt.readTxt(vecLoc);
            String[] temp = vector.toString().split(",");
            double[] vectorDouble = new double[temp.length];
            for (int i = 0; i < temp.length; i++) {
                vectorDouble[i] = Double.parseDouble(temp[i]);
            }
            double similarity = TransTotxtS.cosineSimilarity(vectorDouble, vecB);
            materialSimilarityDto.setSimilarity(similarity < 1 ? similarity : 1);
            materialSimilarityDtos1.add(materialSimilarityDto);
        }
        for (Material material:materials2){
            MaterialSimilarityDto materialSimilarityDto = new MaterialSimilarityDto();
            materialSimilarityDto.setId(material.getId());
            materialSimilarityDto.setName(material.getName());
            materialSimilarityDto.setUserId(material.getUserId());
            materialSimilarityDto.setLocation(material.getLocation());
            materialSimilarityDto.setProjectId(material.getProjectId());
            materialSimilarityDto.setVectorLocation(material.getVectorLocation());
            String vecLoc = material.getVectorLocation();
            StringBuffer vector = FileRdWt.readTxt(vecLoc);
            String[] temp = vector.toString().split(",");
            double[] vectorDouble = new double[temp.length];
            for (int i = 0; i < temp.length; i++) {
                vectorDouble[i] = Double.parseDouble(temp[i]);
            }
            double similarity = TransTotxtS.cosineSimilarity(vectorDouble, vecA);
            materialSimilarityDto.setSimilarity(similarity < 1 ? similarity : 1);
            materialSimilarityDtos2.add(materialSimilarityDto);
        }
        double similarity = TransTotxtS.cosineSimilarity(vecA, vecB);
        similarityDto.setSimilarity(similarity < 1 ? similarity : 1);
        similarityDto.setList1(materialSimilarityDtos1);
        similarityDto.setList2(materialSimilarityDtos2);
        return similarityDto;
    }
}
