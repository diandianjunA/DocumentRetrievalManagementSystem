package com.project.documentretrievalmanagementsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnalyzeDto {
    private List<ProjectDto> list;
    private int total; // 总记录数
    private int pageSize; // 每页显示记录数
    private int pages; // 总页数
    private int pageNum; // 当前页

    private boolean isFirstPage;        //是否为第一页
    private boolean isLastPage;         //是否为最后一页
    private boolean hasPreviousPage;   //是否有前一页
    private boolean hasNextPage;       //是否有下一页
}
