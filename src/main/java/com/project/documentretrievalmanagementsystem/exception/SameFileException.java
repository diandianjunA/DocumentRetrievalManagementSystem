package com.project.documentretrievalmanagementsystem.exception;

/************************
 * DocumentRetrievalManagementSystem
 * com.project.documentretrievalmanagementsystem.exception
 * MHC
 * author : mhc
 * date:  2023/5/13 14:12
 * description : 
 ************************/
public class SameFileException extends RuntimeException{
    public SameFileException(String message){
        super(message);
    }
}
