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
import com.project.documentretrievalmanagementsystem.utils.TransTotxtS;
import io.swagger.models.auth.In;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    IMaterialService materialService;
    @Value("${my.basePath}")
    private String basePath;

    @Override
    //调用python脚本生成资料摘要
    //方案生成
    public String generateSummary(Integer materialId) {
        Material material = materialService.getById(materialId);
        //获取资料地址
        String location = material.getLocation();
        //将资料转换为txt格式
        TransTotxt.DocxToTxt(location,basePath);
        String result = "";
        String Path = basePath+"scheme.txt";
        try {
            //开启了命令执行器，输入指令执行python脚本
            Process process = Runtime.getRuntime()
                    .exec("E:\\develop\\Anaconda\\Anaconda3\\envs\\pytorch\\python.exe " +
                            "D:\\MHC\\pycharm\\pythonProject\\predict.py " +
                            "--model_path D:\\MHC\\pycharm\\pythonProject\\cpt-base " +
                            "--file_path "+Path+" " +
                            "--sum_min_len 50");

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

    @Override
    //方案保存
    public Scheme saveScheme(String summary,String schemeName,Integer materialId) {
        String Path = basePath + "scheme.txt";
        Material material = materialService.getById(materialId);
        //获取该material的数据库信息
        String name = material.getName();
        Integer projectId = material.getProjectId();
        Integer userid = material.getUserId();
        //存入scheme数据库
        Scheme scheme = new Scheme();
        scheme.setName(schemeName);
        scheme.setMaterialId(materialId);
        scheme.setUserId(userid);
        scheme.setProjectId(projectId);
        scheme.setLocation(Path + "\\scheme.txt");
        scheme.setSummary(summary);
        save(scheme);
        return scheme;
    }

    @Override
    public XSSFWorkbook downloadExcel(List<Scheme> list) {
        String[] excelHeader = { "Id", "方案名", "用户Id", "资料Id", "项目Id", "资料地址", "摘要"};
        XSSFWorkbook wb = new XSSFWorkbook();
        //创建XSSFSheet对象
        XSSFSheet sheet = wb.createSheet("关系表");
        XSSFRow row = sheet.createRow((int) 0);
        XSSFCellStyle style = wb.createCellStyle();
        //style.setAlignment(HorizontalAlignment.CENTER);//水平居中

        for (int i = 0; i < excelHeader.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(i);
            //设置指定列的列宽，256 * 50这种写法是因为width参数单位是单个字符的256分之一
            sheet.setColumnWidth(cell.getColumnIndex(), 100 * 50);
        }

        for (int i = 0; i < list.size(); i++) {
            row = sheet.createRow(i + 1);
            Scheme scheme = list.get(i);
            row.createCell(0).setCellValue(scheme.getId());
            row.createCell(1).setCellValue(scheme.getName());
            row.createCell(2).setCellValue(scheme.getUserId());
            row.createCell(3).setCellValue(scheme.getMaterialId());
            row.createCell(4).setCellValue(scheme.getProjectId());
            row.createCell(5).setCellValue(scheme.getLocation());
            row.createCell(6).setCellValue(scheme.getSummary());
        }
        return wb;
    }

    @Override
    public List<Scheme> getSchemeByMaterialId(Integer MaterialId) {
        LambdaQueryWrapper<Scheme> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Scheme::getMaterialId,MaterialId);
        return list(wrapper);
    }

}

