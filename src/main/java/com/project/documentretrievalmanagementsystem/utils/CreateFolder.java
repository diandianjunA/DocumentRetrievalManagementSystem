package com.project.documentretrievalmanagementsystem.utils;

import com.project.documentretrievalmanagementsystem.service.impl.MaterialServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/************************
 * DocumentRetrievalManagementSystem
 * com.project.documentretrievalmanagementsystem.utils
 * MHC
 * author : mhc
 * date:  2023/5/16 16:44
 * description : 
 ************************/
public class CreateFolder {


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

    //删除资料分类文件夹
    public static void deleteCategoryFolder(String Path) {
        File file = new File(Path);
        if (file.exists()) {
            //文件存在时，判断是文件还是目录，如果是文件，则直接删除
            if (file.isFile()) {
                file.delete();
                //在数据库中删除该文件
            } else if (file.isDirectory()) {
                //先删除目录下所有的文件以及子目录以及子目录下的文件
                String[] list = file.list();
                for (String s : list) {
                    //递归删除目录下的文件
                    deleteCategoryFolder(Path + "/" + s);
                }
                //删除目录
                file.delete();
            }
        }
    }

}
