package com.project.documentretrievalmanagementsystem.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.mapper.MaterialMapper;
import com.project.documentretrievalmanagementsystem.service.impl.MaterialServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

import static com.baomidou.mybatisplus.extension.toolkit.SimpleQuery.list;

/************************
 * DocumentRetrievalManagementSystem
 * com.project.documentretrievalmanagementsystem.utils
 * MHC
 * author : mhc
 * date:  2023/5/16 16:44
 * description : 
 ************************/
//Spring Boot中的@Component、@Service、@Controller和@Repository都是用来定义Bean的。
// @Component：通用的注解，可标注任意类为Spring组件。如果一个Bean不知道属于哪个层，可以使用@Component注解标注。
// @Service：用于标注业务层组件、服务层组件。
// @Controller：用于标注控制层组件（如struts中的action）。
// @Repository：用于标注数据访问组件，即DAO组件。
// @Autowired：自动装配，可以对类成员变量、方法及构造函数进行标注，完成自动装配的工作。
@Component
public class CreateFolder {
    @Autowired
    public static MaterialServiceImpl materialService;
    @Autowired
    public static MaterialMapper materialMapper;

    //创建资料分类文件夹
    public static void createCategoryFolder(String Path, String categoryName) {
        File file = new File(Path + categoryName);
        if (!file.exists()) {
            file.mkdirs();
        }
        /*else {
            System.out.println("文件夹已存在");
        }*/
    }

}
