package com.project.documentretrievalmanagementsystem.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Scheme;
import com.project.documentretrievalmanagementsystem.mapper.SchemeMapper;
import com.project.documentretrievalmanagementsystem.service.FileService;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.project.documentretrievalmanagementsystem.service.ISchemeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.documentretrievalmanagementsystem.utils.TransTotxt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-14
 */
@Service
public class SchemeServiceImpl extends ServiceImpl<SchemeMapper, Scheme> implements ISchemeService {
    @Autowired
    FileService fileService;
    @Autowired
    MaterialServiceImpl materialService;
    @Autowired
    MaterialServiceImpl materialServiceImpl;


    @Override
    //调用python脚本生成资料摘要
    public String generateSummary(Integer materialId) {
        Material material = materialService.getById(materialId);
        //获取资料地址
        System.out.println(material.toString());
        String location = material.getLocation();
        System.out.println(location);
        //将资料转换为txt格式
        TransTotxt.DocxToTxt(location);
        System.out.println(location);
        String result = "";
        try {
            //开启了命令执行器，输入指令执行python脚本
            Process process = Runtime.getRuntime()
                    .exec("E:\\develop\\Anaconda\\Anaconda3\\envs\\pytorch\\python.exe " +
                            "D:\\MHC\\pycharm\\pythonProject\\predict.py " +
                            "--model_path D:\\MHC\\pycharm\\pythonProject./cpt-base " +
                            "--file_path D:\\code\\scheme\\scheme.txt" +
                            "--sum_min_len 40");

            //这种方式获取返回值的方式是需要用python打印输出，然后java去获取命令行的输出，在java返回
            InputStreamReader ir = new InputStreamReader(process.getInputStream(),"GB2312");
            LineNumberReader input = new LineNumberReader(ir);
            //读取命令行的输出
            result = input.readLine();
            input.close();
            ir.close();

        } catch (IOException e) {
            System.out.println("调用python脚本并读取结果时出错：" + e.getMessage());
        }
        return result;


    }
}
