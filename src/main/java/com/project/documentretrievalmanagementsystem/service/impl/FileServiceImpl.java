package com.project.documentretrievalmanagementsystem.service.impl;

import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.service.FileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String upload(MultipartFile file,String basePath) {
        // 1.获取当前上传的文件名
        String originalFilename = file.getOriginalFilename();
        // 2.截取当前文件名的格式后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 3.判断要存储文件的路径是否存在，不存在则创建
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        // 4.将上传的文件保存到指定的路径
        try {
            file.transferTo(new File(basePath + originalFilename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 5.返回数据给前端
        return originalFilename;
    }

    @Override
    public ResponseEntity<byte[]> download(HttpSession session, String basePath, String fileName) throws IOException {
        //获取服务器中文件的真实路径
        String realPath = basePath+fileName;
        //创建输入流
        InputStream is = Files.newInputStream(Paths.get(realPath));
        //创建字节数组
        byte[] bytes = new byte[is.available()];
        //将流读到字节数组中
        is.read(bytes);
        //创建HttpHeaders对象设置响应头信息
        MultiValueMap<String, String> headers = new HttpHeaders();
        //设置要下载方式以及下载文件的名字
        headers.add("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName,"UTF-8"));
        //设置响应状态码
        HttpStatus statusCode = HttpStatus.OK;
        //创建ResponseEntity对象
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, headers,
                statusCode);
        //关闭输入流
        is.close();
        return responseEntity;
    }

    @Override
    public ResponseEntity<byte[]> download(HttpSession session, String location) throws IOException {
        //创建输入流
        InputStream is = Files.newInputStream(Paths.get(location));
        //创建字节数组
        byte[] bytes = new byte[is.available()];
        //将流读到字节数组中
        is.read(bytes);
        //创建HttpHeaders对象设置响应头信息
        MultiValueMap<String, String> headers = new HttpHeaders();
        //设置要下载方式以及下载文件的名字
        String fileName = location.substring(location.lastIndexOf(File.separator));
        headers.add("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName,"UTF-8"));
        //设置响应状态码
        HttpStatus statusCode = HttpStatus.OK;
        //创建ResponseEntity对象
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, headers,
                statusCode);
        //关闭输入流
        is.close();
        return responseEntity;
    }
}
