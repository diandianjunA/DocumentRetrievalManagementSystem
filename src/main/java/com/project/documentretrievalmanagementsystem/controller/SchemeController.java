package com.project.documentretrievalmanagementsystem.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.common.UserHolder;
import com.project.documentretrievalmanagementsystem.dto.GenerateDto;
import com.project.documentretrievalmanagementsystem.dto.MaterialDto;
import com.project.documentretrievalmanagementsystem.dto.SchemeDto;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.entity.Record;
import com.project.documentretrievalmanagementsystem.entity.Scheme;
import com.project.documentretrievalmanagementsystem.entity.User;
import com.project.documentretrievalmanagementsystem.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    @Autowired
    IUserService userService;
    @Autowired
    IRecordService recordService;

    //方案生成
    @GetMapping("/generate")
    @ApiOperation("生成方案")
    public R<String> generateSummary(@RequestParam("materialId") Integer materialId, @RequestParam("length") Integer length){
        String result = schemeService.generateSummary(materialId,length);
        result=result.substring(2,result.length()-2).replace(" ","");
        return R.success(result);
    }

    //多文本方案生成
    @PostMapping("/generateMulti")
    @ApiOperation("生成多文本方案")
    public R<String> generateMultiSummary(@RequestBody GenerateDto generateDto) throws IOException {
//        String result = schemeService.generateMultiSummary(generateDto.getMaterialIds(), generateDto.getProjectIds(), generateDto.getLength());
//        result=result.substring(2,result.length()-2).replace(" ","");
//        return R.success(result);
        String result = schemeService.generateSummary(generateDto.getMaterialIds().get(0),generateDto.getLength());
        result=result.substring(2,result.length()-2).replace(" ","");
        return R.success(result);
    }

    //方案保存
    @PostMapping("/save")
    @ApiOperation("保存方案")
    public R<Scheme> saveScheme(@RequestBody Scheme scheme){
        LambdaQueryWrapper<Scheme> schemeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        schemeLambdaQueryWrapper.eq(Scheme::getProjectId,scheme.getProjectId());
        Scheme scheme1 = schemeService.getOne(schemeLambdaQueryWrapper);
        if(scheme1==null) {
            Integer currentId = UserHolder.getUser().getId();
            scheme.setUserId(currentId);
            schemeService.save(scheme);
            Record record = new Record();
            record.setUserId(currentId);
            record.setTime(LocalDateTime.now());
            record.setInformation("保存"+scheme.getName()+"方案");
            recordService.save(record);
        }else{
            scheme.setId(scheme1.getId());
            schemeService.updateById(scheme);
            Record record = new Record();
            record.setUserId(UserHolder.getUser().getId());
            record.setTime(LocalDateTime.now());
            record.setInformation("更新"+scheme.getName()+"方案");
            recordService.save(record);
        }
        return R.success(scheme);
    }

    //数据库中scheme导出excel表格中
    @GetMapping("/download")
    @ApiOperation("导出excel方案")
    public void downloadExcel(Integer projectId,HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<Scheme> schemeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        schemeLambdaQueryWrapper.eq(Scheme::getProjectId,projectId);
        //从数据库中获取表数据
        List<Scheme> list = schemeService.list(schemeLambdaQueryWrapper);
        //生成Excel表格
        XSSFWorkbook wb = schemeService.downloadExcel(list);
        OutputStream output = response.getOutputStream();
        // 文件名中文形式
        String fileName = "方案-" + new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date()) + ".xlsx";
        //fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+", "%20"));
        wb.write(output);
        output.close();
    }

    //数据库中scheme导出Docx文档中
    @GetMapping("/downloadDocx")
    @ApiOperation("导出docx方案")
    public void downloadDocx(Integer projectId,HttpServletResponse response) throws IOException {
        XWPFDocument docx = schemeService.downloadDocx(projectId);
        OutputStream output = response.getOutputStream();
        // 文件名中文形式
        String fileName = "方案-" + new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date()) + ".docx";
        //fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+", "%20"));
        docx.write(output);
        output.close();
    }

    @GetMapping("/getByProjectId")
    @ApiOperation("获取方案数据")
    public R<Scheme> getByProjectId(@ApiParam("项目id") Integer projectId){
        LambdaQueryWrapper<Scheme> schemeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        schemeLambdaQueryWrapper.eq(Scheme::getProjectId,projectId);
        Scheme scheme = schemeService.getOne(schemeLambdaQueryWrapper);
        return R.success(scheme);
    }


    @GetMapping("/delete")
    @ApiOperation("删除方案")
    public R<String> deleteScheme(@ApiParam("方案id") Integer id){
        Scheme scheme = schemeService.getById(id);
        schemeService.removeById(id);
        Integer currentId = UserHolder.getUser().getId();
        Record record = new Record();
        record.setUserId(currentId);
        record.setTime(LocalDateTime.now());
        record.setInformation("删除"+scheme.getName()+"方案");
        recordService.save(record);
        return R.success("删除成功");
    }

    @PostMapping("/update")
    @ApiOperation("更新方案")
    public R<String> updateScheme(@RequestBody Scheme scheme){
        schemeService.updateById(scheme);
        Integer currentId = UserHolder.getUser().getId();
        Record record = new Record();
        record.setUserId(currentId);
        record.setTime(LocalDateTime.now());
        record.setInformation("更新"+scheme.getName()+"方案");
        recordService.save(record);
        return R.success("更新成功");
    }
}
