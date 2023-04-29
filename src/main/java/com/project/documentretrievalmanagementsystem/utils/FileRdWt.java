package com.project.documentretrievalmanagementsystem.utils;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;


/************************
 * DocumentRetrievalManagementSystem
 * com.project.documentretrievalmanagementsystem.utils
 * MHC
 * author : mhc
 * date:  2023/4/29 0:56
 * description : 
 ************************/
public class FileRdWt {

        // 读取文件内容
        public static String readFile(String filePath) throws IOException {
            String text = "";
            try {
                FileInputStream fis = new FileInputStream(filePath);
                XWPFDocument doc = new XWPFDocument(fis);
                XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
                text = extractor.getText();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return text;
        }


    // 写入文件内容
    public static void writeFile(String filePath, String content) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath,true), "UTF-8"));
        bw.write(content);
        bw.close();
    }

}

