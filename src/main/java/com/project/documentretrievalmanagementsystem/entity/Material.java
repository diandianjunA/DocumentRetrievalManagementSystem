package com.project.documentretrievalmanagementsystem.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
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
@TableName("material")
@ApiModel(value="Material对象", description="")
public class Material implements Serializable {

    private static final long serialVersionUID = 1L;                //指定序列化版本号

    @ApiModelProperty(value = "资料id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "资料名称")
    private String name;

    @ApiModelProperty(value = "资料所属项目的id")
    private Integer projectId;

    @ApiModelProperty(value = "资料在用户空间中的地址")
    private String locInUser;

    @ApiModelProperty(value = "资料地址")
    private String location;

    @ApiModelProperty(value = "所属用户的id")
    private Integer userId;

    @ApiModelProperty(value = "资料向量所在的地址")
    private String vectorLocation;
}
