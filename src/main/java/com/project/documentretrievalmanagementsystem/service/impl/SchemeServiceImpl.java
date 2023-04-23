package com.project.documentretrievalmanagementsystem.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Scheme;
import com.project.documentretrievalmanagementsystem.mapper.SchemeMapper;
import com.project.documentretrievalmanagementsystem.service.FileService;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.project.documentretrievalmanagementsystem.service.ISchemeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-14
 */
@Service
public class SchemeServiceImpl extends ServiceImpl<SchemeMapper, Scheme> implements ISchemeService {
    @Autowired
    FileService fileService;
    @Autowired
    IMaterialService materialService;

    @Override
    //调用python脚本生成资料摘要
    public String generateSummary(Integer materialId) {
        Material material = materialService.getById(materialId);
        //获取资料地址
        String location = material.getLocation();
        System.out.println(location);
        return location;
    }
}
