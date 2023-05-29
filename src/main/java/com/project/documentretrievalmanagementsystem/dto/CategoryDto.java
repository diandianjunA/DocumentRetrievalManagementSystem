package com.project.documentretrievalmanagementsystem.dto;

/************************
 * DocumentRetrievalManagementSystem
 * com.project.documentretrievalmanagementsystem.dto
 * MHC
 * author : mhc
 * date:  2023/5/29 12:57
 * description : 用于查询分类文件夹下的资料和文件夹，包含三个属性，第一个属性判断是文件还是文件夹，第二个属性是文件夹或者文件的名字，第三个属性假如是文件则是文件的id
 ************************/
import lombok.Data;

@Data
public class CategoryDto {
    //Dir表示文件夹，File表示文件
    private String type;
    private String name;
    private Integer id;
    private Long lastModified;

    //资料
    public CategoryDto(String type, String name, Integer id, Long lastModified) {
        this.type = type;
        this.name = name;
        this.id = id;
        this.lastModified = lastModified;
    }

    //文件夹
    public CategoryDto(String type, String name,Long lastModified) {
        this.type = type;
        this.name = name;
        this.id = null;
        this.lastModified = lastModified;
    }

}
