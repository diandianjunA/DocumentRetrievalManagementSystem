package com.project.documentretrievalmanagementsystem.dto;

import com.project.documentretrievalmanagementsystem.entity.Material;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MaterialDto extends Material {
    public String projectName;
    public String content;

    public MaterialDto(Material material){
        this.setId(material.getId());
        this.setLoc_in_user(material.getLoc_in_user());
        this.setName(material.getName());
        this.setProjectId(material.getProjectId());
        this.setLocation(material.getLocation());
        this.setVectorLocation(material.getVectorLocation());
    }
}
