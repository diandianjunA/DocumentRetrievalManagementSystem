package com.project.documentretrievalmanagementsystem.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
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
@Api("资料管理")
public class MaterialController {
    @Autowired
    IMaterialService materialService;

    @PostMapping("/add")
    @ApiOperation("添加资料")
    public R<Material> addMaterial(@ApiParam("资料名称") @RequestParam(value = "name") String name, @ApiParam("资料所属项目的id") @RequestParam(value = "projectId") Integer projectId, @ApiParam("文件") @RequestParam(value = "file")MultipartFile file){
        Material material = materialService.addMaterial(name, projectId, file);
        return R.success(material);
    }

    @GetMapping("/get")
    @ApiOperation("获取全部资料信息")
    public R<List<Material>> getMaterial(){
        List<Material> list = materialService.list();
        return R.success(list);
    }

    @GetMapping("/getPaged")
    @ApiOperation("获取分页资料信息")
    public R<PageInfo<Material>> getPagedMaterial(@ApiParam("第几页")Integer pageNum,@ApiParam("一页多少条数据")int pageSize,@ApiParam("导航栏共展示几页")int navSize){
        try {
            PageHelper.startPage(pageNum,pageSize);
            List<Material> list = materialService.list();
            PageInfo<Material> projectPageInfo = new PageInfo<>(list,navSize);
            return R.success(projectPageInfo);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }

    @GetMapping("/getDownload")
    public void download(String name, HttpServletResponse response){
        LambdaQueryWrapper<Material> materialLambdaQueryWrapper = new LambdaQueryWrapper<>();
        materialLambdaQueryWrapper.eq(Material::getName,name);
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
            e.printStackTrace();
        }
    }
}
