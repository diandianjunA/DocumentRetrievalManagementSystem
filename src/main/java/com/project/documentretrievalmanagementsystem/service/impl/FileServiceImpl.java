package com.project.documentretrievalmanagementsystem.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.InputStreamResource;
import com.alibaba.fastjson.JSONObject;
import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.service.FileService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;



@Service
public class FileServiceImpl implements FileService {

    @Override
    public String upload(MultipartFile file,String Path) {
        // 1.获取当前上传的文件名
        String originalFilename = file.getOriginalFilename();
        // 2.截取当前文件名的格式后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 3.判断要存储文件的路径是否存在，不存在则创建
        File dir = new File(Path);
        if (!dir.exists()){
            dir.mkdirs();
        }
        // 4.判断该路径下是否已经存在同名字的文件，不允许重复上传同名文件
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.getName().equals(originalFilename)){
                return "文件已存在";
            }
        }
        // 5.将上传的文件保存到指定的路径
        try {
            file.transferTo(new File(Path + originalFilename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 6.返回数据给前端
        return originalFilename;
    }


    @Override
    public ResponseEntity<byte[]> download(HttpSession session, String Path, String fileName) throws IOException {
        //获取服务器中文件的真实路径
        String realPath = Path+fileName;
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
    public ResponseEntity<byte[]> download(HttpServletResponse response, String location) throws IOException {
        InputStream is = Files.newInputStream(Paths.get(location));
        //创建字节数组
        byte[] bytes = new byte[is.available()];
        //将流读到字节数组中
        is.read(bytes);
        //创建HttpHeaders对象设置响应头信息
        MultiValueMap<String, String> headers = new HttpHeaders();
        String fileName=location.substring(location.lastIndexOf(File.separator)+1);
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
    public void downloadFile(HttpServletResponse response, String location) throws IOException {
        ServletOutputStream out =null;
        ByteArrayOutputStream baos = null;
        FileInputStream fileInputStream=null;
        try {
            File file = new File(location);
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            baos = new ByteArrayOutputStream();
            while ((len=fileInputStream.read(buffer))!=-1){
                baos.write(buffer,0,len);
            }
            String fileName=location.substring(location.lastIndexOf(File.separator)+1);
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8"));
            response.addHeader("Content-Length", "" + baos.size());
            response.setHeader("filename", fileName);
            response.setContentType("application/octet-stream");
            out = response.getOutputStream();
            out.write(baos.toByteArray());

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }finally {
            assert baos != null;
            baos.flush();
            baos.close();
            assert out != null;
            out.flush();
            out.close();
            fileInputStream.close();
        }
    }
}
