package com.project.documentretrievalmanagementsystem.controller;

import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/file")
@CrossOrigin
@Api(tags = "文件操作")
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
    @ApiOperation("上传文件")
    public R<String> upload(@ApiParam("文件") MultipartFile file) {
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
    @ApiOperation("文件以附件形式下载")
    public ResponseEntity<byte[]> download(HttpSession session,@ApiParam("文件名") String fileName) throws IOException {
        return fileService.download(session, basePath, fileName);
    }
}
