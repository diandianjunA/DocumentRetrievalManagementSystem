package com.project.documentretrievalmanagementsystem.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.common.UserHolder;
import com.project.documentretrievalmanagementsystem.dto.MaterialDto;
import com.project.documentretrievalmanagementsystem.dto.SchemeDto;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.entity.Scheme;
import com.project.documentretrievalmanagementsystem.entity.User;
import com.project.documentretrievalmanagementsystem.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
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

    //方案生成
    @GetMapping("/generate")
    @ApiOperation("生成方案")
    public R<String> generateSummary(@RequestParam("materialId") Integer materialId){
        String result = schemeService.generateSummary(materialId);
        result=result.substring(2,result.length()-2).replace(" ","");
        return R.success(result);
    }

    //方案保存
    @PostMapping("/save")
    @ApiOperation("保存方案")
    public R<Scheme> saveScheme(@RequestBody Scheme scheme){
        Integer currentId = UserHolder.getUser().getId();
        scheme.setUserId(currentId);
        schemeService.save(scheme);
        return R.success(scheme);
    }

    //数据库中scheme导出excel表格中
    @GetMapping("/download")
    @ApiOperation("导出方案")
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
//        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+", "%20"));
        wb.write(output);
        output.close();
    }

    @GetMapping("/getPaged")
    @ApiOperation("获取方案分页数据")
    public R<PageInfo<Scheme>> getPaged(@ApiParam("第几页")Integer pageNum, @ApiParam("一页多少条数据")int pageSize, @ApiParam("导航栏共展示几页")int navSize,@ApiParam("方案名称") String schemeName, @ApiParam("资料名称")String materialName,@ApiParam("资料id") String materialId, @ApiParam("资料对应的项目id") Integer projectId, @ApiParam("项目名称")String projectName){
        PageHelper.startPage(pageNum,pageSize);
        Integer currentId = UserHolder.getUser().getId();
        LambdaQueryWrapper<Scheme> schemeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(schemeName!=null&&!schemeName.equals("")){
            schemeLambdaQueryWrapper.or().like(Scheme::getName,schemeName);
        }
        if(materialId!=null){
            schemeLambdaQueryWrapper.and(i->i.eq(Scheme::getMaterialId,materialId));
        }
        if(materialName!=null&& !materialName.equals("")){
            LambdaQueryWrapper<Material> materialLambdaQueryWrapper = new LambdaQueryWrapper<>();
            materialLambdaQueryWrapper.like(Material::getName,materialName);
            for (Material material : materialService.list(materialLambdaQueryWrapper)) {
                schemeLambdaQueryWrapper.or().eq(Scheme::getMaterialId,material.getId());
            }
        }
        if(projectId!=null){
            schemeLambdaQueryWrapper.and(i->i.eq(Scheme::getProjectId,projectId));
        }
        if(projectName!=null&& !projectName.equals("")){
            LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            projectLambdaQueryWrapper.like(Project::getName,projectName);
            for (Project project : projectService.list(projectLambdaQueryWrapper)) {
                schemeLambdaQueryWrapper.or().eq(Scheme::getProjectId,project.getId());
            }
        }
        schemeLambdaQueryWrapper.and(i->i.eq(Scheme::getUserId,currentId));
        List<Scheme> list = schemeService.list(schemeLambdaQueryWrapper);
        Map<Integer, Project> projectMap = projectService.getProjectMap();
        Map<Integer, Material> materialMap = materialService.getMaterialMap();
        Map<Integer, User> userMap = userService.getUserMap();
        ArrayList<SchemeDto> schemeDtos = new ArrayList<>();
        for(Scheme scheme:list){
            SchemeDto schemeDto = new SchemeDto(scheme);
            schemeDto.setProjectName(projectMap.get(scheme.getProjectId()).getName());
            schemeDto.setMaterialName(materialMap.get(scheme.getMaterialId()).getName());
            schemeDto.setUserName(userMap.get(scheme.getUserId()).getUserName());
            schemeDtos.add(schemeDto);
        }
        return R.success(new PageInfo<>(schemeDtos,navSize));
    }
}
