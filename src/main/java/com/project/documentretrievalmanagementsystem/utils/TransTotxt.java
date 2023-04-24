package com.project.documentretrievalmanagementsystem.utils;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import org.springframework.beans.factory.annotation.Value;


/************************
 * DocumentRetrievalManagementSystem
 * com.project.documentretrievalmanagementsystem.utils
 * MHC
 * author : mhc
 * date:  2023/4/24 19:30
 * description : 将各种格式的文档转换为txt格式，便于调用算法接口来产生概要
 ************************/


public class TransTotxt {

    //docx格式文件转为txt格式
    public static void DocxToTxt(String location) {
        try {
            // 读取Word文档
            File file = new File(location);
            FileInputStream fis = new FileInputStream(file);
            XWPFWordExtractor extractor = new XWPFWordExtractor(OPCPackage.open(fis));
            String text = extractor.getText();
            fis.close();

            // 将内容写入txt文件
            FileOutputStream fos = new FileOutputStream("D:\\code\\scheme\\scheme.txt");
            fos.write(text.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OpenXML4JException e) {
            throw new RuntimeException(e);
        }
    }
}
