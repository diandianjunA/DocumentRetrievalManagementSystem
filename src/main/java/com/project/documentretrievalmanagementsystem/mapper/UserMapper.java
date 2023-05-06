package com.project.documentretrievalmanagementsystem.mapper;

import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-11
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @MapKey("id")
    @Select("select id,user_name from user")
    Map<Integer, User> getUserMap();
}
