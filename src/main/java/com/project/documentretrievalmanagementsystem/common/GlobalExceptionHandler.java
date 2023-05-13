package com.project.documentretrievalmanagementsystem.common;

import com.project.documentretrievalmanagementsystem.exception.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;

        /*这种方式可以避免在Controller层中出现大量的try-catch块，
        使代码更加简洁、清晰。同时也可以实现异常信息的统一处理和格式化，
        便于前端开发人员对错误进行定位和处理。*/

@ControllerAdvice(annotations = {RestController.class, Controller.class})   //全局异常处理
@ResponseBody                                                               //返回json数据
public class GlobalExceptionHandler {
    @ExceptionHandler(FileNotFoundException.class)                          //捕获指定异常
    public R<String> fileNotFoundException(FileNotFoundException exception){
        String message=exception.getMessage();
        return R.error(message);
    }

    @ExceptionHandler(HaveDisabledException.class)                          //捕获指定异常
    public R<String> haveDisabledException(HaveDisabledException exception){
        String message=exception.getMessage();
        return R.error(message);
    }

    @ExceptionHandler(IOException.class)
    public R<String> IOException(IOException exception){
        String message=exception.getMessage();
        return R.error(message);
    }

    @ExceptionHandler(PasswordWrongException.class)
    public R<String> PasswordWrongException(PasswordWrongException exception){
        String message=exception.getMessage();
        return R.error(message);
    }

    @ExceptionHandler(FileDownloadException.class)
    public R<String> FileDownloadException(FileDownloadException exception){
        String message=exception.getMessage();
        return R.error(message);
    }

    @ExceptionHandler(SameMaterialNameException.class)
    public R<String> SameMaterialNameException(SameMaterialNameException exception){
        String message=exception.getMessage();
        return R.error(message);
    }

    @ExceptionHandler(SameFileException.class)
    public R<String> SameFileException(SameFileException exception){
        String message=exception.getMessage();
        return R.error(message);
    }
    @ExceptionHandler(RuntimeException.class)
    public R<String> RuntimeException(RuntimeException exception){
        String message=exception.getMessage();
        return R.error(message);
    }
}
