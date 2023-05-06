package com.project.documentretrievalmanagementsystem.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.common.UserHolder;
import com.project.documentretrievalmanagementsystem.dto.ProjectDto;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.project.documentretrievalmanagementsystem.service.IProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-14
 */
@RestController
@RequestMapping("/project")
@CrossOrigin
@Api(tags = "项目管理")
public class ProjectController {

    @Autowired
    IProjectService projectService;
    @Autowired
    IMaterialService materialService;
    @Value("${my.basePath}")
    private String basePath;

    @PostMapping("/add")
    @ApiOperation(value = "添加项目")
    public R<Project> addProject(@RequestBody @ApiParam("项目信息") Project project){
        Integer currentId = UserHolder.getUser().getId();
        project.setUserId(currentId);
        if(projectService.save(project)){
            return R.success(project);
        }else{
            return R.error("保存失败");
        }
    }

    @GetMapping("/getAll")
    @ApiOperation("获取所有项目")
    public R<List<Project>> getProjects(){
        LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        Integer currentId = UserHolder.getUser().getId();
        projectLambdaQueryWrapper.eq(Project::getUserId,currentId);
        List<Project> list = projectService.list(projectLambdaQueryWrapper);
        return R.success(list);
    }

    @GetMapping("/getById")
    @ApiOperation("根据项目id获取项目")
    public R<Project> getProjects(@ApiParam("项目id")Integer id){
        Project project = projectService.getById(id);
        return R.success(project);
    }

    @GetMapping("/getPaged")
    @ApiOperation("获取分页数据")
    public R<PageInfo<Project>> getPagedProjects(@ApiParam("第几页")Integer pageNum, @ApiParam("一页多少条数据") Integer pageSize, @ApiParam("导航栏共展示几页")Integer navSize, @ApiParam("模糊查询项目名称") String projectName, @ApiParam("模糊查询项目分类") String category, @ApiParam("模糊查询项目备注") String remark){
        try {
            PageHelper.startPage(pageNum,pageSize);
            LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            Integer currentId = UserHolder.getUser().getId();
            if(projectName!=null&& !projectName.equals("")){
                projectLambdaQueryWrapper.or().like(Project::getName,projectName);
            }
            if(category!=null&& !category.equals("")){
                projectLambdaQueryWrapper.or().like(Project::getCategory,category);
            }
            if(remark!=null&& !remark.equals("")){
                projectLambdaQueryWrapper.or().like(Project::getRemark,remark);
            }
            projectLambdaQueryWrapper.and(i->i.eq(Project::getUserId,currentId));
            List<Project> list = projectService.list(projectLambdaQueryWrapper);
            PageInfo<Project> projectPageInfo = new PageInfo<>(list,navSize);
            return R.success(projectPageInfo);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    @ApiOperation("更新项目信息")
    public R<Project> updateProject(@RequestBody @ApiParam("项目数据") Project project){
        if(projectService.updateById(project)){
            return R.success(project);
        }else{
            return R.error("修改失败");
        }
    }

    @GetMapping("/delete")
    @ApiOperation("删除项目")
    public R<Integer> deleteProject(@ApiParam("项目id") Integer id){
        boolean deleteProject = projectService.removeById(id);
        LambdaQueryWrapper<Material> materialLambdaQueryWrapper = new LambdaQueryWrapper<>();
        materialLambdaQueryWrapper.eq(Material::getProjectId,id);
        List<Material> list = materialService.list(materialLambdaQueryWrapper);
        for (Material material:list) {
            materialService.deleteById(material.getId());
        }
        if(deleteProject){
            return R.success(1);
        }else{
            return R.error("删除失败");
        }
    }

    @GetMapping("/similarity")
    @ApiOperation("项目相似度")
    public R<Double> similarity(@ApiParam("项目Aid") Integer projectIdA, @ApiParam("项目Bid") Integer projectIdB) throws IOException {
            double similarity = projectService.similarity(projectIdA, projectIdB);
        return R.success(similarity);
    }

    @GetMapping("/analyze")
    @ApiOperation("项目分析")
    public R<List<ProjectDto>> analyze(@ApiParam("项目id") Integer projectId) throws IOException {
        List<ProjectDto> analysis = projectService.projectAnalyze(projectId);
        return R.success(analysis);
    }
}
