package com.project.documentretrievalmanagementsystem.dto;

import com.project.documentretrievalmanagementsystem.entity.Project;
import lombok.Data;
import lombok.EqualsAndHashCode;

/************************
 * DocumentRetrievalManagementSystem
 * com.project.documentretrievalmanagementsystem.dto
 * MHC
 * author : mhc
 * date:  2023/4/30 10:15
 * description : 
 ************************/
@EqualsAndHashCode(callSuper = true)
@Data
public class ProjectDto extends Project {
    double similarity;
    Integer id;
    String name;
    String category;
    String remark;
    public Integer getId(){
        return super.getId();
    }
    public String getName(){
        return super.getName();
    }
    public String getCategory(){
        return super.getCategory();
    }
    public String getRemark(){
        return super.getRemark();
    }
    public Project setId(Integer id){
        super.setId(id);
        return null;
    }
    public Project setName(String name){
        super.setName(name);
        return null;
    }
    public Project setCategory(String category){
        super.setCategory(category);
        return null;
    }
    public Project setRemark(String remark){
        super.setRemark(remark);
        return null;
    }
    public ProjectDto(){}

}
