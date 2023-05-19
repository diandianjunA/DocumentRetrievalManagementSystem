package com.project.documentretrievalmanagementsystem.dto;

import com.project.documentretrievalmanagementsystem.entity.Scheme;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SchemeDto extends Scheme {
    private String projectName;
    private String userName;

    public SchemeDto(Scheme scheme){
        this.setId(scheme.getId());
        this.setName(scheme.getName());
        this.setProjectId(scheme.getProjectId());
        this.setUserId(scheme.getUserId());
        this.setSummary(scheme.getSummary());
    }
}
