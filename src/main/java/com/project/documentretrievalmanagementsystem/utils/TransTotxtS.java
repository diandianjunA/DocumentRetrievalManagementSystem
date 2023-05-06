package com.project.documentretrievalmanagementsystem.utils;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.text.NumberFormat;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/************************
 * DocumentRetrievalManagementSystem
 * com.project.documentretrievalmanagementsystem.utils
 * MHC
 * author : mhc
 * date:  2023/4/24 19:30
 * description : 将各种格式的文档转换为txt格式，便于调用算法接口来产生概要
 ************************/


public class TransTotxtS {

    //docx格式文件转为txt格式
    public static void DocxToTxt(String location,String basePath) {
        try {
            // 读取Word文档
            File file = new File(location);
            FileInputStream fis = new FileInputStream(file);
            XWPFWordExtractor extractor = new XWPFWordExtractor(OPCPackage.open(fis));
            String text = extractor.getText();
            fis.close();

            // 将内容写入txt文件
            //String PATH = basePath+"vector.txt";
            FileOutputStream fos = new FileOutputStream(basePath);
            fos.write(text.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OpenXML4JException e) {
            throw new RuntimeException(e);
        } catch (XmlException e) {
            throw new RuntimeException(e);
        }
    }

    //将小数转换为百分数
    public static String doubleToPercent(double num) {
        NumberFormat percent = NumberFormat.getPercentInstance();
        percent.setMaximumFractionDigits(2);
        return percent.format(num);
    }

    //计算两个向量的余弦相似度
    public static double cosineSimilarity(double[] vec1, double[] vec2) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            normA += vec1[i] * vec1[i];
            normB += vec2[i] * vec2[i];
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
