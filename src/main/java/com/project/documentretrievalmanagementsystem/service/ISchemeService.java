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
    //调用python脚本生成资料摘要(方案生成)
    public String generateSummary(Integer materialId);

}

