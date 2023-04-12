package com.project.documentretrievalmanagementsystem.dto;

import com.project.documentretrievalmanagementsystem.entity.User;
import lombok.Data;

@Data
public class UserDto extends User {
    String token;
}
