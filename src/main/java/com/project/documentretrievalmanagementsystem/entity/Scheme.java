package com.project.documentretrievalmanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("scheme")
@ApiModel(value="Scheme对象", description="")
public class Scheme implements Serializable {

    private static final long serialVersionUID = 1L;                    //指定序列化版本号

    @ApiModelProperty(value = "方案id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "方案名称")
    private String name;

    @ApiModelProperty(value = "方案所属项目id")
    private Integer projectId;


    @ApiModelProperty(value = "所属用户的id")
    private Integer userId;

    @ApiModelProperty(value = "方案的摘要")
    private String summary;



}
