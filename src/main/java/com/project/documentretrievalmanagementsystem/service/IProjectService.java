package com.project.documentretrievalmanagementsystem.service;

import com.project.documentretrievalmanagementsystem.dto.ProjectDto;
import com.project.documentretrievalmanagementsystem.dto.SimilarityDto;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-14
 */
public interface IProjectService extends IService<Project> {
    Map<Integer,Project> getProjectMap();

    //计算两个项目之间的相似度
    public double similarity(Integer projectIdA, Integer projectIdB) throws IOException;

    //选型分析，根据项目id获取与该项目相似度最高的五个项目信息以及相似度并返回
    public List<ProjectDto> projectAnalyze(Integer projectId) throws IOException;

    SimilarityDto similarityAnalyze(Integer project1Id, Integer project2Id) throws IOException;
}


