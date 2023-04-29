package com.project.documentretrievalmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.mapper.ProjectMapper;
import com.project.documentretrievalmanagementsystem.service.FileService;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.project.documentretrievalmanagementsystem.service.IProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.documentretrievalmanagementsystem.utils.FileRdWt;
import com.project.documentretrievalmanagementsystem.utils.TransTotxtS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-14
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {
    @Autowired
    ProjectMapper projectMapper;

    @Lazy
    @Autowired
    IMaterialService materialService;
    @Value("${my.basePath}")
    private String basePath;


    @Override
    public Map<Integer, Project> getProjectMap() {
        return projectMapper.getProjectMap();
    }


    @Override
    public double similarity(Integer projectIdA, Integer projectIdB) throws IOException {
        //查询资料数据库，获取项目id为projectIdA的资料
        QueryWrapper<Material> wrapperA = new QueryWrapper<>();
        QueryWrapper<Material> wrapperB = new QueryWrapper<>();
        wrapperA.eq("project_id",projectIdA);
        wrapperB.eq("project_id",projectIdB);
        //获取项目A的所有资料
        List<Material> materialListA = materialService.list(wrapperA);
        List<Material> materialListB = materialService.list(wrapperB);
        //将所有资料都分别放入一个txt文件中
        combine(projectIdA, materialListA);

        combine(projectIdB, materialListB);
        //路径
        String filePathA = basePath + projectIdA + ".txt";
        String filePathB = basePath + projectIdB + ".txt";
        String vecA = "";
        String vecB = "";
        //获取第一篇文章的向量
        try {
            //开启了命令执行器，输入指令执行python脚本
            Process processA = Runtime.getRuntime()
                    .exec("E:\\develop\\Anaconda\\Anaconda3\\envs\\pytorch\\python.exe " +
                            "D:\\MHC\\pycharm\\pythonProject\\predict.py " +
                            "--model_path D:\\MHC\\pycharm\\pythonProject\\cpt-base " +
                            "--file_path "+filePathA+" " +
                            "--sum_min_len 50 " +
                            "--gen_vec 1");

            //这种方式获取返回值的方式是需要用python打印输出，然后java去获取命令行的输出，在java返回
            InputStreamReader ir = new InputStreamReader(processA.getInputStream(),"GB2312");
            LineNumberReader input = new LineNumberReader(ir);
            //读取命令行的输出
            vecA= input.readLine();
            System.out.println(vecA);
            input.close();
            ir.close();
        } catch (IOException e) {
            System.out.println("调用python脚本并读取结果时出错：" + e.getMessage());
        }

        //获取第二篇文章的向量
        try {
            //开启了命令执行器，输入指令执行python脚本
            Process processB = Runtime.getRuntime()
                    .exec("E:\\develop\\Anaconda\\Anaconda3\\envs\\pytorch\\python.exe " +
                            "D:\\MHC\\pycharm\\pythonProject\\predict.py " +
                            "--model_path D:\\MHC\\pycharm\\pythonProject\\cpt-base " +
                            "--file_path "+filePathB+" " +
                            "--sum_min_len 50 " +
                            "--gen_vec 1");

            //这种方式获取返回值的方式是需要用python打印输出，然后java去获取命令行的输出，在java返回
            InputStreamReader ir = new InputStreamReader(processB.getInputStream(),"GB2312");
            LineNumberReader input = new LineNumberReader(ir);
            //读取命令行的输出
            vecB= input.readLine();
            input.close();
            ir.close();
        } catch (IOException e) {
            System.out.println("调用python脚本并读取结果时出错：" + e.getMessage());
        }

        //将字符串转换为double数组
        String[] vecAstr = vecA.split(",");
        String[] vecBstr = vecB.split(",");
        double[] vecAdouble = new double[vecAstr.length];
        double[] vecBdouble = new double[vecBstr.length];
        for(int i=1;i<vecAstr.length-1;i++){
            vecAdouble[i] = Double.parseDouble(vecAstr[i]);
        }
        for(int i=1;i<vecBstr.length-1;i++){
            vecBdouble[i] = Double.parseDouble(vecBstr[i]);
        }
        //计算两个向量的余弦相似度
        double similarity = TransTotxtS.cosineSimilarity(vecAdouble,vecBdouble);
        //将小数转换为百分数
        //String similaritystr = TransTotxtS.doubleToPercent(similarity);
        //System.out.println(similaritystr);
        //返回相似度
        return similarity;
    }

    private void combine(Integer projectId, List<Material> materialList) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Material material : materialList) {
            String content = FileRdWt.readFile(material.getLocation());
            sb.append(content).append("\n");
        }
        String filePath = basePath + projectId + ".txt";
        FileRdWt.writeFile(filePath, sb.toString());
    }
}
