package com.project.documentretrievalmanagementsystem.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.entity.Scheme;
import com.project.documentretrievalmanagementsystem.mapper.MaterialMapper;
import com.project.documentretrievalmanagementsystem.mapper.SchemeMapper;
import com.project.documentretrievalmanagementsystem.service.FileService;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.project.documentretrievalmanagementsystem.service.ISchemeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.documentretrievalmanagementsystem.utils.TransTotxt;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;
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
    ProjectServiceImpl projectService;
    @Autowired
    SchemeMapper schemeMapper;
    @Lazy
    @Autowired
    IMaterialService materialService;
    @Autowired
    MaterialMapper materialMapper;

    @Value("${my.basePathT}")
    private String basePathT;
    @Value("${my.modelPath}")
    private String modelPath;
    @Value("${my.pythonPath}")
    private String pythonPath;
    @Value("${my.scriptPath}")
    private String scriptPath;


    @Override
    //调用python脚本生成资料摘要
    //方案生成
    public String generateSummary(Integer materialId, Integer length) {
        Material material = materialService.getById(materialId);
        //获取资料地址
        String location = material.getLocation();
        //将资料转换为txt格式
        TransTotxt.DocxToTxt(location,basePathT);
        String result = "";
        String Path = basePathT+"scheme.txt";
        try {
            //开启了命令执行器，输入指令执行python脚本
            Process process = Runtime.getRuntime()
                    .exec(pythonPath+" " +
                            scriptPath+"/predict.py " +
                            "--model_path "+modelPath+" " +
                            "--file_path "+Path+" " +
                            "--sum_min_len "+length);

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

    //导出格式为Excel
    @Override
    public XSSFWorkbook downloadExcel(List<Scheme> list) {
        //定义Excel表格
        String[] excelHeader = { "Id", "方案名", "用户Id", "资料Id", "项目Id", "摘要"};
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
            row.createCell(4).setCellValue(scheme.getProjectId());
            row.createCell(5).setCellValue(scheme.getSummary());
        }
        return wb;
    }


    //导出格式为Docx
    @Override
    public XWPFDocument downloadDocx(Integer projectId){
        XWPFDocument docx = new XWPFDocument();
        //根据projectId取得该项目
        Project project = projectService.getById(projectId);
        String projectName = project.getName();
        //获得projectId为projectId的方案
        LambdaQueryWrapper<Scheme> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Scheme::getProjectId, projectId);
        List<Scheme> schemeList = schemeMapper.selectList(queryWrapper);
        if (schemeList.isEmpty()) {
            return null;
        }
        Scheme scheme = schemeList.get(0);
        String summary = scheme.getSummary();
        //将项目名作为标题
        XWPFParagraph title = docx.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = title.createRun();
        titleRun.setText(projectName);
        titleRun.setBold(true);
        //将摘要作为正文
        XWPFParagraph body = docx.createParagraph();
        body.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun bodyRun = body.createRun();
        bodyRun.setText(summary);
        bodyRun.setBold(false);
        return docx;
    }

    //删除资料分类文件夹
    public void deleteCategoryFolder(String Path) {
        File file = new File(Path);
        if (file.exists()) {
            //文件存在时，判断是文件还是目录，如果是文件，则直接删除
            if (file.isFile()) {
                //根据Path查找数据库中的Material
                LambdaQueryWrapper<Material> queryWrapper = new LambdaQueryWrapper<>();
                String PathC = Path.replace("\\", "/");
                queryWrapper.eq(Material::getLocInUser, PathC);
                // 执行查询操作，取出符合条件的实体列表
                List<Material> materialList = materialMapper.selectList(queryWrapper);
                Material material = materialList.get(0);
                //删除数据库中的记录
                materialMapper.deleteById(material.getId());
                file.delete();

            } else if (file.isDirectory()) {
                //先删除目录下所有的文件以及子目录以及子目录下的文件
                String[] list = file.list();
                for (String s : list) {
                    //递归删除目录下的文件
                    deleteCategoryFolder(Path + "\\" + s);
                }
                //删除目录
                file.delete();
            }
        }
    }

   /* @Override
    public XWPFDocument downloadDocx(Integer projectId)
    {

    }*/

    /*@Override
    public List<Scheme> getSchemeByMaterialId(Integer MaterialId) {
        LambdaQueryWrapper<Scheme> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Scheme::getMaterialId,MaterialId);
        return list(wrapper);
    }*/

    /*@Override
    public void deleteByMaterialId(Integer MaterialId) {
        LambdaQueryWrapper<Scheme> schemeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        schemeLambdaQueryWrapper.eq(Scheme::getMaterialId,MaterialId);
        remove(schemeLambdaQueryWrapper);
    }*/
}

