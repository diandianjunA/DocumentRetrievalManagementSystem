package com.project.documentretrievalmanagementsystem.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.project.documentretrievalmanagementsystem.common.UserHolder;
import com.project.documentretrievalmanagementsystem.dto.EsQueryDto;
import com.project.documentretrievalmanagementsystem.dto.MaterialDto;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.mapper.MaterialMapper;
import com.project.documentretrievalmanagementsystem.service.FileService;
import com.project.documentretrievalmanagementsystem.service.IMaterialService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.documentretrievalmanagementsystem.service.IProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author diandianjun
 * @since 2023-04-14
 */
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements IMaterialService {

    @Autowired
    FileService fileService;
    @Value("${my.basePath}")
    private String basePath;
    @Autowired
    ElasticsearchClient elasticsearchClient;
    @Autowired
    IProjectService projectService;

    @Override
    public Material addMaterial(String name, Integer projectId, MultipartFile file) {
        String originalName = fileService.upload(file, basePath + "material/");
        Material material = new Material();
        material.setName(name);
        material.setProjectId(projectId);
        Integer currentId = UserHolder.getUser().getId();
        material.setUserId(Math.toIntExact(currentId));
        material.setLocation(basePath+"material/"+ originalName);
        save(material);
        return material;
    }

    @Override
    public void deleteMaterial(Integer id) {
        Material material = getById(id);
        File file = new File(material.getLocation());
        if(file.exists()){
            file.delete();
        }
        removeById(id);
    }

    @Override
    public List<MaterialDto> fuzzyQuery(EsQueryDto esQueryDto) throws Exception {
        esQueryDto.setIndex("resumes");
        esQueryDto.setField("content");
        SearchResponse<HashMap> search = elasticsearchClient.search(s -> s
                        .index(esQueryDto.getIndex())
                        .query(q -> q.fuzzy(t -> t
                                .field(esQueryDto.getField())
                                .value(esQueryDto.getWord()).fuzziness("1")
                        )).from(esQueryDto.getFrom()).size(esQueryDto.getSize()),
                HashMap.class);
        List<Hit<HashMap>> hits = search.hits().hits();
        System.out.println(hits);
        ArrayList<MaterialDto> res = new ArrayList<>();
        for (Hit<HashMap> hit:hits){
            HashMap source = hit.source();
            HashMap file = (HashMap) source.get("file");
            String location = (String) file.get("url");
            location=location.substring(7);
            LambdaQueryWrapper<Material> materialLambdaQueryWrapper = new LambdaQueryWrapper<>();
            materialLambdaQueryWrapper.eq(Material::getLocation,location);
            List<Material> list = list(materialLambdaQueryWrapper);
            MaterialDto materialDto = new MaterialDto(list.get(0));
            Project project = projectService.getById(materialDto.getProjectId());
            materialDto.setProjectName(project.getName());
            String content = (String) source.get("content");
            materialDto.setContent(content);
            res.add(materialDto);
        }
        return res;
    }
}
