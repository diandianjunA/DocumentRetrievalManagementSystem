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
@TableName("project")
@ApiModel(value="Project对象", description="")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;                //指定序列化版本号

    @ApiModelProperty(value = "项目id")
    @TableId(value = "id", type = IdType.AUTO)
    protected Integer id;

    @ApiModelProperty(value = "项目名称")
    protected String name;

    @ApiModelProperty(value = "项目类型")
    protected String category;

    @ApiModelProperty(value = "项目备注")
    protected String remark;

    @ApiModelProperty(value = "所属用户的id")
    private Integer userId;


}
