package com.project.documentretrievalmanagementsystem.controller;


import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.entity.Scheme;
import com.project.documentretrievalmanagementsystem.service.FileService;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.project.documentretrievalmanagementsystem.service.IProjectService;
import com.project.documentretrievalmanagementsystem.service.ISchemeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-14
 */
@RestController
@RequestMapping("/scheme")
@CrossOrigin
@Api(tags = "方案管理")
public class SchemeController {
    @Autowired
    IMaterialService materialService;
    @Autowired
    IProjectService projectService;
    @Autowired
    FileService fileService;
    @Autowired
    ISchemeService schemeService;

    //方案生成
    @PostMapping("/generate")
    @ApiOperation("生成方案")
    public R<String> generateSummary(Integer materialId){
        String result = schemeService.generateSummary(materialId);
        return R.success(result);
    }

    //方案保存
    @PostMapping("/save")
    @ApiOperation("保存方案")
    public R<Scheme> saveScheme(String summary, String schemeName, Integer materialId){
        Scheme scheme = schemeService.saveScheme(summary,schemeName,materialId);
        return R.success(scheme);
    }

}
