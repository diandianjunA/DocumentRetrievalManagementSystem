package com.project.documentretrievalmanagementsystem.service.impl;

import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.mapper.ProjectMapper;
import com.project.documentretrievalmanagementsystem.service.IProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

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


    @Override
    public Map<Integer, Project> getProjectMap() {
        return projectMapper.getProjectMap();
    }
}
