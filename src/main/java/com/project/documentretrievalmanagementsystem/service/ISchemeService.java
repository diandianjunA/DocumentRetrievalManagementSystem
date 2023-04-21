package com.project.documentretrievalmanagementsystem.service;

import com.project.documentretrievalmanagementsystem.entity.Scheme;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-14
 */
public interface ISchemeService extends IService<Scheme> {

    //调用python接口生成资料的摘要
    public String getSummary(String filePath);
}

