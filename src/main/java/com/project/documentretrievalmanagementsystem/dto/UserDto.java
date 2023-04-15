package com.project.documentretrievalmanagementsystem.dto;

import com.project.documentretrievalmanagementsystem.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserDto extends User {
    String token;
}
