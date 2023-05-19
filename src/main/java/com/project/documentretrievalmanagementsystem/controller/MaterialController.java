package com.project.documentretrievalmanagementsystem.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.common.UserHolder;
import com.project.documentretrievalmanagementsystem.dto.EsQueryDto;
import com.project.documentretrievalmanagementsystem.dto.FuzzyQueryDto;
import com.project.documentretrievalmanagementsystem.dto.MaterialDto;
import com.project.documentretrievalmanagementsystem.dto.MaterialFileDto;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Record;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.exception.FileDownloadException;
import com.project.documentretrievalmanagementsystem.exception.SameFileException;
import com.project.documentretrievalmanagementsystem.exception.SameMaterialNameException;
import com.project.documentretrievalmanagementsystem.service.FileService;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.project.documentretrievalmanagementsystem.service.IProjectService;
import com.project.documentretrievalmanagementsystem.service.IRecordService;
import com.project.documentretrievalmanagementsystem.service.impl.SchemeServiceImpl;
import com.project.documentretrievalmanagementsystem.utils.CreateFolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
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
    @Autowired
    SchemeServiceImpl schemeService;
    @Autowired
    IRecordService recordService;
    @Value("${my.basePathT}")
    private String basePathT;
    @Value("${my.UserPath}")
    private String UserPath;

    @PostMapping("/add")
    @ApiOperation("添加资料")
    public R<Material> addMaterial(MaterialFileDto materialFileDto,@ApiParam("相对路径")String upperPath) throws SameMaterialNameException , SameFileException {
        Material material = materialService.addMaterial(materialFileDto.getName(), materialFileDto.getProjectId(), materialFileDto.getFile(), upperPath);
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
    public R<List<Material>> getMaterialByProjectsId(@ApiParam("项目id")Integer projectId){
        LambdaQueryWrapper<Material> materialLambdaQueryWrapper = new LambdaQueryWrapper<>();
        materialLambdaQueryWrapper.eq(Material::getProjectId,projectId);
        List<Material> list = materialService.list(materialLambdaQueryWrapper);
        return R.success(list);
    }

    @GetMapping("/getPaged")
    @ApiOperation("获取分页资料信息")
    public R<PageInfo<MaterialDto>> getPagedMaterial(@ApiParam("第几页")Integer pageNum, @ApiParam("一页多少条数据")int pageSize, @ApiParam("导航栏共展示几页")int navSize,@ApiParam("资料名称")String materialName,@ApiParam("资料对应的项目id") Integer projectId,@ApiParam("项目名称")String projectName){
        try {
            PageInfo<MaterialDto> pagedMaterial = materialService.getPagedMaterial(pageNum, pageSize, navSize, materialName, projectId, projectName);
            return R.success(pagedMaterial);
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
    public R<Material> updateMaterial(@ApiParam("资料数据") @RequestBody Material material){
        if(materialService.updateById(material)){
            Integer currentId = UserHolder.getUser().getId();
            Record record = new Record();
            record.setUserId(currentId);
            record.setTime(LocalDateTime.now());
            record.setInformation("更新"+material.getName()+"资料");
            recordService.save(record);
            return R.success(material);
        }else{
            return R.error("修改失败");
        }
    }

    @GetMapping("/delete")
    @ResponseBody
    @ApiOperation("在删除数据库上资料的同时，删除服务器上的文件")
    public R deleteMaterial(@ApiParam("资料id") Integer id){
        Material material = materialService.getById(id);
        try {
            materialService.deleteById(id);
            Record record = new Record();
            Integer currentId = UserHolder.getUser().getId();
            record.setUserId(currentId);
            record.setTime(LocalDateTime.now());
            record.setInformation("删除"+material.getName()+"项目");
            recordService.save(record);
            return R.success("删除成功");
        } catch (Exception e) {
            return R.success("删除失败");
        }
    }

    @GetMapping("/getFuzzyPaged")
    @ApiOperation("获取模糊查询分页资料信息")
    public R<FuzzyQueryDto> getFuzzyPagedMaterial(@ApiParam("第几页")Integer pageNum, @ApiParam("一页多少条数据")Integer pageSize, @ApiParam("搜索关键字") String keyWord){
        try {
            EsQueryDto esQueryDto = new EsQueryDto();
            esQueryDto.setFrom((pageNum-1)*pageSize);
            esQueryDto.setSize((pageNum-1)*pageSize+pageSize);
            esQueryDto.setWord(keyWord);
            FuzzyQueryDto FuzzyQueryDto = materialService.fuzzyQuery(esQueryDto);
            return R.success(FuzzyQueryDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //创建资料分类文件夹
    @PostMapping("/createCategeory")
    @ApiOperation("创建资料分类文件夹")
    public R createCategeory(@ApiParam("资料分类文件夹名称") String categoryName,@ApiParam("项目Id") Integer projectId, @ApiParam("上一级目录") String upperPath){
        try {
            String userName = UserHolder.getUser().getUserName();
            String userDir = UserPath+userName+"/";
            //根据项目id获取项目名称
            Project project = projectService.getById(projectId);
            if(project == null){
                return R.error("项目不存在");
            }else{
                String projectDir = userDir+project.getName();
                String categoryDir = projectDir+upperPath;
                CreateFolder.createCategoryFolder(categoryDir, categoryName);
                //return R.success(categoryDir);
                return R.success("创建资料分类文件夹成功");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //删除资料分类文件夹，同时删除文件夹下的所有文件，包括子文件夹
    //同时删除数据库中的数据
    @PostMapping("/deleteCategory")
    @ApiOperation("删除资料分类文件夹")
    public R deleteFolder(@ApiParam("项目Id") Integer projectId, @ApiParam("上一级目录") String upperPath, @ApiParam("要删除的文件夹名称") String CategoryName) {

        try {
            String userName = UserHolder.getUser().getUserName();
            String userDir = UserPath+userName+"/";
            //根据项目id获取项目名称
            Project project = projectService.getById(projectId);
            String projectDir = userDir+project.getName();
            String categoryDir = projectDir+upperPath+CategoryName;
            //return R.success(categoryDir);
            schemeService.deleteCategoryFolder(categoryDir);
            return R.success("删除文件夹成功");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
