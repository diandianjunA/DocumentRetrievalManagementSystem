package com.project.documentretrievalmanagementsystem.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface FileService {
    String upload(MultipartFile file,String basePath);
    ResponseEntity<byte[]> download(HttpSession session, String basePath, String fileName) throws IOException;
}
