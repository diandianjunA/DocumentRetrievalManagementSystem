package com.project.documentretrievalmanagementsystem.controller;


import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-14
 */
@RestController
@RequestMapping("/material")
@CrossOrigin
@Api("资料管理")
public class MaterialController {
    @Autowired
    IMaterialService materialService;

    @PostMapping("/add")
    public R<Material> addMaterial(@ApiParam("资料名称") @RequestParam(value = "name") String name, @ApiParam("资料所属项目的id") @RequestParam(value = "projectId") Integer projectId, @ApiParam("文件") @RequestParam(value = "file")MultipartFile file){
        Material material = materialService.addMaterial(name, projectId, file);
        return R.success(material);
    }
}
