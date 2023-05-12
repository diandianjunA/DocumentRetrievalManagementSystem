package com.project.documentretrievalmanagementsystem.service;

import com.project.documentretrievalmanagementsystem.controller.FileController;
import com.project.documentretrievalmanagementsystem.service.impl.FileServiceImpl;
import org.apache.http.entity.ContentType;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static javaslang.control.Option.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/************************
 * DocumentRetrievalManagementSystem
 * com.project.documentretrievalmanagementsystem.service
 * MHC
 * author : mhc
 * date:  2023/5/5 19:20
 * description : 文件操作服务类测试
 ************************/
@RunWith(SpringRunner.class)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
    @Autowired
    private FileServiceImpl fileService;

    @Value("${my.basePathT}")
    private String mybasePath;

    @Mock
    private HttpSession session;

    private String basePathDownload;

    @Test
    public void upload() throws Exception {
        String basePath = mybasePath + "test/";
        String filename = "test.txt";
        byte[] content = new byte[1024];


        File testFile = new File(basePath + filename);
        FileInputStream fileInputStream = new FileInputStream(testFile);
        MultipartFile multipartFile = new MockMultipartFile(testFile.getName(), testFile.getName(),
                ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);

        // 创建一个 MultipartFile 对象
        MockMultipartFile file = new MockMultipartFile(filename, basePath, "text/plain", content);

        // 调用 upload 方法
        String result = fileService.upload(multipartFile, basePath);

        // 检查是否成功保存了文件并返回了正确的文件名
        File savedFile = new File(basePath + filename);
        assertTrue(savedFile.exists());
        assertEquals(filename, result);

        // 删除保存的文件
        savedFile.delete();
    }

    @Test
    public void testDownload() throws Exception {
        // 准备要下载的文件
        String fileName = "test.txt";
        String fileContent = "hello world";
        Path file = Paths.get(mybasePath, fileName);
        Files.write(file, fileContent.getBytes(StandardCharsets.UTF_8));

        // 创建一个模拟的HttpSession对象
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("username", "testuser");

        // 设置mock对象的行为
        session.getAttribute("username");

        // 调用被测试方法
        ResponseEntity<byte[]> responseEntity = fileService.download(mockSession,mybasePath,"test.txt");

        // 验证返回结果是否正确
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(fileContent.getBytes(StandardCharsets.UTF_8).length, responseEntity.getBody().length);

        // 验证响应头信息是否正确
        HttpHeaders headers = responseEntity.getHeaders();
        assertNotNull(headers);
        assertTrue(headers.containsKey(HttpHeaders.CONTENT_DISPOSITION));
        String expectedDisposition = "attachment;filename=" + fileName +"";
        assertEquals(expectedDisposition, headers.getFirst(HttpHeaders.CONTENT_DISPOSITION));

        // 验证响应内容是否正确
        InputStream is = new ByteArrayInputStream(responseEntity.getBody());
        byte[] actualBytes = new byte[is.available()];
        is.read(actualBytes);
        assertArrayEquals(fileContent.getBytes(StandardCharsets.UTF_8), actualBytes);

        // 清理临时文件
        Files.deleteIfExists(file);
    }

    @Test
    public void testDownloadFile() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        //返回一个输出流
        Mockito.when(response.getOutputStream()).thenReturn(outputStream);

        String location = "D:\\code\\source\\TXT\\test2.txt";
        File file = new File(location);
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] fileContent = new byte[(int) file.length()];
        fileInputStream.read(fileContent);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(fileContent);
        String fileName = file.getName();
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8");

        fileService.downloadFile(response, location);

        verify(response).addHeader(eq("Content-Disposition"), eq("attachment;filename=" + encodedFileName));
        verify(response).addHeader(eq("Content-Length"), eq(String.valueOf(byteArrayOutputStream.size())));
        verify(response).setHeader(eq("filename"), eq(fileName));
        verify(response).setContentType(eq("application/octet-stream"));
        verify(outputStream).write(eq(byteArrayOutputStream.toByteArray()));
    }

}