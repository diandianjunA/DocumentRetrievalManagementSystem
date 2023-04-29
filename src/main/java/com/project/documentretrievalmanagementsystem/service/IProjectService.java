package com.project.documentretrievalmanagementsystem.service;

import com.project.documentretrievalmanagementsystem.entity.Project;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
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
}
