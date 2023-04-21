package com.project.documentretrievalmanagementsystem.service;

import com.project.documentretrievalmanagementsystem.entity.Material;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

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

    //删除服务器或者本地上的资料
    void deleteMaterial(Integer id);

}
