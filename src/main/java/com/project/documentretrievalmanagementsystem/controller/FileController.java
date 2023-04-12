package com.project.documentretrievalmanagementsystem.controller;

import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${my.basePath}")
    private String basePath;
    @Autowired
    private FileService fileService;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        String filename = fileService.upload(file, basePath);
        return R.success(filename);
    }

    /**
     * 文件下载，以附件形式返回
     * @param session
     * @return
     * @throws IOException
     */
    @RequestMapping("/download")
    public ResponseEntity<byte[]> download(HttpSession session,String fileName) throws IOException {
        return fileService.download(session, basePath, fileName);
    }
}
