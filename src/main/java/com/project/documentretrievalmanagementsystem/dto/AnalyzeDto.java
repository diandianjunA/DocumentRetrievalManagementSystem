package com.project.documentretrievalmanagementsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class AnalyzeDto {
    private List<ProjectDto> list;
    private int total = 0; // 总记录数
    private int pageSize = 20; // 每页显示记录数
    private int pages = 1; // 总页数
    private int pageNum = 1; // 当前页

    private boolean isFirstPage=false;        //是否为第一页
    private boolean isLastPage=false;         //是否为最后一页
    private boolean hasPreviousPage=false;   //是否有前一页
    private boolean hasNextPage=false;       //是否有下一页
}
