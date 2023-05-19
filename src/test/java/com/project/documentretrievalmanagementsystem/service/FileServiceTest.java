package com.project.documentretrievalmanagementsystem.service;

import com.project.documentretrievalmanagementsystem.service.impl.FileServiceImpl;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
class FileServiceTest {

    @Autowired
    private FileServiceImpl fileService;

    @Value("${my.basePathT}")
    private String myBasePath;

    @Mock
    private HttpSession session;


    @Test
    void upload() throws IOException {
        String basePath = myBasePath + "test/";
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
    void download() throws IOException {
        // 准备要下载的文件
        String fileName = "test.txt";
        String fileContent = "hello world";
        Path file = Paths.get(myBasePath, fileName);
        Files.write(file, fileContent.getBytes(StandardCharsets.UTF_8));

        // 创建一个模拟的HttpSession对象
        MockHttpSession mockSession = new MockHttpSession();
        mockSession.setAttribute("username", "testuser");


        // 调用被测试方法
        ResponseEntity<byte[]> responseEntity = fileService.download(mockSession, myBasePath,"test.txt");

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
    void testDownload() throws IOException {
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