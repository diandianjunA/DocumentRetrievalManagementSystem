package com.project.documentretrievalmanagementsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.project.documentretrievalmanagementsystem.common.R;
import com.project.documentretrievalmanagementsystem.dto.UserDto;
import com.project.documentretrievalmanagementsystem.entity.User;
import com.project.documentretrievalmanagementsystem.exception.HaveDisabledException;
import com.project.documentretrievalmanagementsystem.exception.PasswordWrongException;
import com.project.documentretrievalmanagementsystem.mapper.UserMapper;
import com.project.documentretrievalmanagementsystem.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-11
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    public User login(@RequestBody Map<String,String> map, HttpSession session){
        String userName = map.get("userName");
        String password = map.get("password");
        //查看该用户是否为新用户
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserName,userName);
        User user = getOne(userLambdaQueryWrapper);
        if(user==null){
            //是新用户,自动注册
            user = new User();
            user.setUserName(userName);
            user.setPassword(password);
            user.setStatus(1);
            save(user);
            //将用户的信息存到session中，这样可以通过过滤器
            //随机生成token作为登录令牌
            String token = UUID.randomUUID().toString();
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setToken(token);
            userDto.setUserName(userName);
            userDto.setPassword(password);
            userDto.setStatus(1);
            session.setAttribute(token,user);
            return userDto;
        }else{
            if(user.getStatus()==0){
                throw new HaveDisabledException("用户已被禁用");
            }else if(!Objects.equals(user.getPassword(), password)){
                throw new PasswordWrongException("密码错误");
            }else{
                //将用户的信息存到session中，这样可以通过过滤器
                //随机生成token作为登录令牌
                String token = UUID.randomUUID().toString();
                session.setAttribute(token,user);
                UserDto userDto = new UserDto();
                userDto.setId(user.getId());
                userDto.setToken(token);
                userDto.setUserName(user.getUserName());
                userDto.setPassword(user.getPassword());
                userDto.setStatus(user.getStatus());
                return userDto;
            }
        }
    }
}
