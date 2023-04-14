package com.project.documentretrievalmanagementsystem.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.service.IProjectService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/project")
@CrossOrigin
public class ProjectController {

    @Autowired
    IProjectService projectService;
    @Value("${my.basePath}")
    private String basePath;

    @PostMapping("/add")
    @ApiOperation(value = "添加项目")
    public R<Project> addProject(@RequestBody @ApiParam("项目信息") Project project){
        if(projectService.save(project)){
            return R.success(project);
        }else{
            return R.error("保存失败");
        }
    }

    @GetMapping("/getAll")
    @ApiOperation("获取所有项目")
    public R<List<Project>> getProjects(){
        List<Project> list = projectService.list();
        return R.success(list);
    }

    @GetMapping("/getPaged")
    @ApiOperation("获取分页数据")
    public R<PageInfo<Project>> getPagedProjects(@ApiParam("第几页")Integer pageNum,@ApiParam("一页多少条数据")int pageSize,@ApiParam("导航栏共展示几页")int navSize){
        try {
            PageHelper.startPage(pageNum,pageSize);
            List<Project> list = projectService.list();
            PageInfo<Project> projectPageInfo = new PageInfo<>(list,navSize);
            return R.success(projectPageInfo);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    public R<Project> updateProject(@ApiParam("项目数据") Project project){
        if(projectService.updateById(project)){
            return R.success(project);
        }else{
            return R.error("修改失败");
        }
    }
}
