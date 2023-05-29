package com.project.documentretrievalmanagementsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeleteDto {
    private List<String> deleteList;
    private Integer projectId;
    private String upperPath;
}
