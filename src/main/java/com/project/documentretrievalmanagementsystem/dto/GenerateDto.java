package com.project.documentretrievalmanagementsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class GenerateDto {
    List<Integer> materialIds;
    List<Integer> projectIds;
    Integer length;
}
