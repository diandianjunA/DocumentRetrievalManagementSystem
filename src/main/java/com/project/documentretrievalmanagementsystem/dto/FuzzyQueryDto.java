package com.project.documentretrievalmanagementsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class FuzzyQueryDto {
    Integer total;
    List<MaterialDto> list;
}
