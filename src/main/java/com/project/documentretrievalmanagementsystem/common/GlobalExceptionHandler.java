package com.project.documentretrievalmanagementsystem.common;

import com.project.documentretrievalmanagementsystem.exception.HaveDisabledException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(FileNotFoundException.class)
    public R<String> fileNotFoundException(FileNotFoundException exception){
        String message=exception.getMessage();
        return R.error(message);
    }

    @ExceptionHandler(HaveDisabledException.class)
    public R<String> haveDisabledException(HaveDisabledException exception){
        String message=exception.getMessage();
        return R.error(message);
    }
}
