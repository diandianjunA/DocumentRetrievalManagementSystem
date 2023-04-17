package com.project.documentretrievalmanagementsystem.service.impl;

import com.project.documentretrievalmanagementsystem.common.UserHolder;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.mapper.MaterialMapper;
import com.project.documentretrievalmanagementsystem.service.FileService;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-14
 */
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements IMaterialService {

    @Autowired
    FileService fileService;
    @Value("${my.basePath}")
    private String basePath;

    @Override
    public Material addMaterial(String name, Integer projectId, MultipartFile file) {
        String originalName = fileService.upload(file, basePath + "material" + File.separator);
        Material material = new Material();
        material.setName(name);
        material.setProjectId(projectId);
        Integer currentId = UserHolder.getUser().getId();
        material.setUserId(Math.toIntExact(currentId));
        material.setLocation(basePath+"material"+File.separator+ originalName);
        save(material);
        return material;
    }
}
