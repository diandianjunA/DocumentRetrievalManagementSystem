package com.project.documentretrievalmanagementsystem.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.dto.UserDto;
import com.project.documentretrievalmanagementsystem.entity.User;
import com.project.documentretrievalmanagementsystem.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-11
 */
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    @Autowired
    IUserService userService;

    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String,String> map, HttpSession session){
        User user = userService.login(map, session);
        if (user!=null){
            return R.success(user);
        }else{
            return R.error("登录失败");
        }
    }
}
