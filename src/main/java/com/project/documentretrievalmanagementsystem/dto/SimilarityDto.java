package com.project.documentretrievalmanagementsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class SimilarityDto {
    private List<MaterialSimilarityDto> list1;
    private List<MaterialSimilarityDto> list2;
    private double similarity;
}
