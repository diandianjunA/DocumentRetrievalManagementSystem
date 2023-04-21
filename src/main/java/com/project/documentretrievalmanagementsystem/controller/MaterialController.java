package com.project.documentretrievalmanagementsystem.controller;


import cn.hutool.core.io.resource.InputStreamResource;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.common.UserHolder;
import com.project.documentretrievalmanagementsystem.dto.EsQueryDto;
import com.project.documentretrievalmanagementsystem.dto.MaterialDto;
import com.project.documentretrievalmanagementsystem.dto.MaterialFileDto;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
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
    public R<Material> addMaterial(MaterialFileDto materialFileDto){
        Material material = materialService.addMaterial(materialFileDto.getName(), materialFileDto.getProjectId(), materialFileDto.getFile());
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
    public R<PageInfo<MaterialDto>> getPagedMaterial(@ApiParam("第几页")Integer pageNum, @ApiParam("一页多少条数据")int pageSize, @ApiParam("导航栏共展示几页")int navSize,@ApiParam("资料名称")String materialName,@ApiParam("资料对应的项目id") Integer projectId,@ApiParam("项目名称")String projectName){
        try {
            PageHelper.startPage(pageNum,pageSize);
            Integer currentId = UserHolder.getUser().getId();
            LambdaQueryWrapper<Material> materialLambdaQueryWrapper = new LambdaQueryWrapper<>();
            if(materialName!=null&& !materialName.equals("")){
                materialLambdaQueryWrapper.or().like(Material::getName,materialName);
            }
            if(projectId!=null){
                materialLambdaQueryWrapper.and(i->i.eq(Material::getProjectId,projectId));
            }
            if(projectName!=null&& !projectName.equals("")){
                LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
                projectLambdaQueryWrapper.like(Project::getName,projectName);
                for (Project project : projectService.list(projectLambdaQueryWrapper)) {
                    materialLambdaQueryWrapper.or().eq(Material::getProjectId,project.getId());
                }
            }
            materialLambdaQueryWrapper.and(i->i.eq(Material::getUserId,currentId));
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
    public void getContent(@ApiParam("资料信息") String location, HttpServletResponse response){
        try {
            // 1.创建一个文件输入流用于读取图片
            FileInputStream fileInputStream = new FileInputStream(location);
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

    @GetMapping(value = "/getDownload")
    @ApiOperation("下载资料")
    public ResponseEntity<byte[]> getDownload(@ApiParam("资料信息") String location, HttpServletResponse response) throws IOException {
        try {
            return fileService.download(response, location);
        } catch (IOException e) {
            throw new FileDownloadException("文件下载失败");
        }
    }

    @GetMapping(value = "/getDownload2")
    @ApiOperation("下载资料")
    public void getDownload2(@ApiParam("资料信息") String location, HttpServletResponse response) throws IOException {
        try {
            fileService.downloadFile(response, location);
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
    @ResponseBody
    //在删除数据库上资料的同时，删除服务器上的文件
    public R deleteMaterial(@ApiParam("资料id") Integer id){
        Material material = materialService.getById(id);
        if(materialService.removeById(id)){
            File file = new File(material.getLocation());
            if(file.exists()){
                file.delete();
            }
            return R.success("删除成功");
        }else{
            return R.error("删除失败");
        }
    }

    @GetMapping("/getFuzzyPaged")
    @ApiOperation("获取模糊查询分页资料信息")
    public R<List<MaterialDto>> getFuzzyPagedMaterial(@ApiParam("第几页")Integer pageNum, @ApiParam("一页多少条数据")int pageSize,@ApiParam("搜索关键字") String keyWord){
        try {
            EsQueryDto esQueryDto = new EsQueryDto();
            esQueryDto.setFrom((pageNum-1)*pageSize);
            esQueryDto.setSize((pageNum-1)*pageSize+pageSize);
            esQueryDto.setWord(keyWord);
            List<MaterialDto> materialDtos = materialService.fuzzyQuery(esQueryDto);
            return R.success(materialDtos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
