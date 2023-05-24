package com.project.documentretrievalmanagementsystem.service;

import com.project.documentretrievalmanagementsystem.entity.Scheme;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-14
 */
public interface ISchemeService extends IService<Scheme> {
    //调用python脚本生成资料摘要(方案生成)
    public String generateSummary(Integer materialId, Integer length);

    //数据库中scheme导出excel表格中(导出全部方案)
    XSSFWorkbook downloadExcel(List<Scheme> list);

    //由于在工具类中无法注入bean，所以将该方法放在service中
    public void deleteCategoryFolder(String Path);

    //数据库中scheme导出docx文档中(一个项目对应一个方案)
    XWPFDocument downloadDocx(Integer projectId);

    //List<Scheme> getSchemeByMaterialId(Integer MaterialId);

    //void deleteByMaterialId(Integer MaterialId);

}

