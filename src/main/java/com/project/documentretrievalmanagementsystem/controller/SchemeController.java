package com.project.documentretrievalmanagementsystem.controller;


import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.entity.Scheme;
import com.project.documentretrievalmanagementsystem.service.FileService;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.project.documentretrievalmanagementsystem.service.IProjectService;
import com.project.documentretrievalmanagementsystem.service.ISchemeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    //数据库中scheme导出excel表格中
    @GetMapping("/download")
    @ApiOperation("导出方案")
    public void downloadExcel(Integer materialId,HttpServletResponse response) throws IOException {
        //从数据库中获取表数据
        List<Scheme> list = schemeService.getSchemeByMaterialId(materialId);
        //生成Excel表格
        HSSFWorkbook wb = schemeService.downloadExcel(list);
        OutputStream output = response.getOutputStream();
        // 文件名中文形式
        String fileName = "方案-" + new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date()) + ".xls";
        fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        response.setContentType("application/octet-stream;charset=ISO-8859-1");
        response.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\"");

        wb.write(output);
        output.close();
    }

}
