package com.project.documentretrievalmanagementsystem.mapper;

import com.project.documentretrievalmanagementsystem.entity.Project;
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
 * @since 2023-04-14
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
    @MapKey("id")
    @Select("select id,name,user_id from project")
    Map<Integer,Project> getProjectMap();
}
