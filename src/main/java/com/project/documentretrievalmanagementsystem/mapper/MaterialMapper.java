package com.project.documentretrievalmanagementsystem.mapper;

import com.project.documentretrievalmanagementsystem.entity.Material;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.documentretrievalmanagementsystem.entity.Project;
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
public interface MaterialMapper extends BaseMapper<Material> {
    @MapKey("id")
    @Select("select id,name from material")
    Map<Integer, Material> getMaterialMap();
}
