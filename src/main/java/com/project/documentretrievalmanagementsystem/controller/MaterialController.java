package com.project.documentretrievalmanagementsystem.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.common.UserHolder;
import com.project.documentretrievalmanagementsystem.dto.MaterialDto;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.exception.FileDownloadException;
import com.project.documentretrievalmanagementsystem.service.FileService;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.project.documentretrievalmanagementsystem.service.IProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
@RequestMapping("/material")
@CrossOrigin
@Api(tags = "资料管理")
public class MaterialController {
    @Autowired
    IMaterialService materialService;
    @Autowired
    IProjectService projectService;
    @Autowired
    FileService fileService;

    @PostMapping("/add")
    @ApiOperation("添加资料")
    public R<Material> addMaterial(@ApiParam("资料名称") @RequestParam(value = "name") String name, @ApiParam("资料所属项目的id") @RequestParam(value = "projectId") Integer projectId, @ApiParam("文件") @RequestParam(value = "file")MultipartFile file){
        Material material = materialService.addMaterial(name, projectId, file);
        return R.success(material);
    }

    @GetMapping("/get")
    @ApiOperation("获取全部资料信息")
    public R<List<Material>> getMaterial(){
        Integer currentId = UserHolder.getUser().getId();
        LambdaQueryWrapper<Material> materialLambdaQueryWrapper = new LambdaQueryWrapper<>();
        materialLambdaQueryWrapper.eq(Material::getUserId,currentId);
        List<Material> list = materialService.list();
        return R.success(list);
    }

    @GetMapping("/getById")
    @ApiOperation("根据资料id获取资料")
    public R<Material> getMaterialById(@ApiParam("资料id")Integer id){
        Material material = materialService.getById(id);
        return R.success(material);
    }

    @GetMapping("/getByProjectId")
    @ApiOperation("根据项目id获取资料")
    public R<List<Material>> getMaterialByProjectsId(@ApiParam("项目id")Integer id){
        LambdaQueryWrapper<Material> materialLambdaQueryWrapper = new LambdaQueryWrapper<>();
        materialLambdaQueryWrapper.eq(Material::getProjectId,id);
        List<Material> list = materialService.list(materialLambdaQueryWrapper);
        return R.success(list);
    }

    @GetMapping("/getPaged")
    @ApiOperation("获取分页资料信息")
    public R<PageInfo<MaterialDto>> getPagedMaterial(@ApiParam("第几页")Integer pageNum, @ApiParam("一页多少条数据")int pageSize, @ApiParam("导航栏共展示几页")int navSize,@ApiParam("资料名称")String materialName,@ApiParam("资料对应的项目id") Integer projectId){
        try {
            PageHelper.startPage(pageNum,pageSize);
            Integer currentId = UserHolder.getUser().getId();
            LambdaQueryWrapper<Material> materialLambdaQueryWrapper = new LambdaQueryWrapper<>();
            materialLambdaQueryWrapper.eq(Material::getUserId,currentId);
            if(materialName!=null){
                materialLambdaQueryWrapper.like(Material::getName,materialName);
            }
            if(projectId!=null){
                materialLambdaQueryWrapper.eq(Material::getProjectId,projectId);
            }
            List<Material> list = materialService.list(materialLambdaQueryWrapper);
            ArrayList<MaterialDto> dtoList = new ArrayList<>();
            Map<Integer, Project> projectMap = projectService.getProjectMap();
            for(Material material:list){
                MaterialDto materialDto = new MaterialDto(material);
                materialDto.setProjectName(projectMap.get(material.getProjectId()).getName());
                dtoList.add(materialDto);
            }
            PageInfo<MaterialDto> projectPageInfo = new PageInfo<>(dtoList,navSize);
            return R.success(projectPageInfo);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    @GetMapping("/getContent")
    @ApiOperation("获取资料内容")
    public void getContent(@RequestBody @ApiParam("资料信息") Material material, HttpServletResponse response){
        Integer currentId = UserHolder.getUser().getId();
        LambdaQueryWrapper<Material> materialLambdaQueryWrapper = new LambdaQueryWrapper<>();
        materialLambdaQueryWrapper.eq(Material::getUserId,currentId);
        materialLambdaQueryWrapper.eq(Material::getId,material.getId());
        List<Material> list = materialService.list(materialLambdaQueryWrapper);
        if(list.isEmpty()){
            return;
        }
        try {
            // 1.创建一个文件输入流用于读取图片
            FileInputStream fileInputStream = new FileInputStream(list.get(0).getLocation());
            // 2.创建一个输出流，通过输出流将文件写回浏览器，在浏览器中展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            // 3.关闭流
            outputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            throw new FileDownloadException("文件下载失败");
        }
    }

    @GetMapping("/getDownload")
    @ApiOperation("下载资料")
    public ResponseEntity<byte[]> getDownload(@RequestBody @ApiParam("资料信息") Material material, HttpSession session){
        try {
            return fileService.download(session, material.getLocation());
        } catch (IOException e) {
            throw new FileDownloadException("文件下载失败");
        }
    }

    @PostMapping("/update")
    @ApiOperation("更新资料信息")
    public R<Material> updateMaterial(@ApiParam("资料数据") Material material){
        if(materialService.updateById(material)){
            return R.success(material);
        }else{
            return R.error("修改失败");
        }
    }

    @GetMapping("/delete")
    @ApiOperation("删除资料")
    public R<Integer> deleteMaterial(@ApiParam("资料id") Integer id){
        boolean deleteMaterial = materialService.removeById(id);
        if(deleteMaterial){
            return R.success(1);
        }else{
            return R.error("删除失败");
        }
    }
}
