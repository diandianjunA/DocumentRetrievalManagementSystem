package com.project.documentretrievalmanagementsystem.exception;

/************************
 * DocumentRetrievalManagementSystem
 * com.project.documentretrievalmanagementsystem.exception
 * MHC
 * author : mhc
 * date:  2023/5/13 14:13
 * description : 
 ************************/
public class SameMaterialNameException extends Exception{
    public SameMaterialNameException(String message){
        super(message);
    }
}
