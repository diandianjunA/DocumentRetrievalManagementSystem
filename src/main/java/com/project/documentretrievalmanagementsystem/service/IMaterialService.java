package com.project.documentretrievalmanagementsystem.service;

import com.project.documentretrievalmanagementsystem.dto.EsQueryDto;
import com.project.documentretrievalmanagementsystem.dto.MaterialDto;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.awt.geom.QuadCurve2D;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-14
 */
public interface IMaterialService extends IService<Material> {
    Material addMaterial(String name, Integer projectId, MultipartFile file);

    List<MaterialDto> fuzzyQuery(EsQueryDto esQueryDto) throws Exception;

    //删除服务器或者本地上的资料
    void deleteMaterial(Integer id);
}
