package com.project.documentretrievalmanagementsystem.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.documentretrievalmanagementsystem.common.UserHolder;
import com.project.documentretrievalmanagementsystem.dto.EsQueryDto;
import com.project.documentretrievalmanagementsystem.dto.FuzzyQueryDto;
import com.project.documentretrievalmanagementsystem.dto.MaterialDto;
import com.project.documentretrievalmanagementsystem.entity.Material;
import com.project.documentretrievalmanagementsystem.entity.Project;
import com.project.documentretrievalmanagementsystem.entity.Record;
import com.project.documentretrievalmanagementsystem.entity.User;
import com.project.documentretrievalmanagementsystem.exception.SameFileException;
import com.project.documentretrievalmanagementsystem.exception.SameMaterialNameException;
import com.project.documentretrievalmanagementsystem.mapper.MaterialMapper;
import com.project.documentretrievalmanagementsystem.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.documentretrievalmanagementsystem.utils.FileRdWt;
import com.project.documentretrievalmanagementsystem.utils.TransTotxtS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

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
    @Value("${my.basePathT}")
    private String basePathT;
    @Value("${my.pythonPath}")
    private String pythonPath;
    @Value("${my.modelPath}")
    private String modelPath;
    @Value("${my.scriptPath}")
    private String scriptPath;
    @Value("${my.UserPath}")
    private String UserPath;
    @Autowired
    ElasticsearchClient elasticsearchClient;

    @Lazy
    @Autowired
    IProjectService projectService;

    @Autowired
    MaterialMapper materialMapper;

    @Autowired
    ISchemeService schemeService;

    @Autowired
    IRecordService recordService;

    @Autowired
    IUserService userService;

    //修改可以上传同名资料和相同文件
    @Override
    public Material addMaterial(String name, Integer projectId, MultipartFile file ,String upperPath) throws SameMaterialNameException, SameFileException{
        //判断该文件是否已经存在，不允许上传同名文件
        LambdaQueryWrapper<Material> materialLambdaQueryWrapper = new LambdaQueryWrapper<>();
        materialLambdaQueryWrapper.eq(Material::getName,name);
        materialLambdaQueryWrapper.eq(Material::getProjectId,projectId);
        Material material1 = getOne(materialLambdaQueryWrapper);
        if (material1 != null){
            throw new SameMaterialNameException("文件名字重复，请重新命名");
        }
        //给ES上传文件
        String originalName = fileService.upload(file, basePath);
        if(originalName.equals("文件已存在")){
            throw new SameFileException("文件已存在");
        }
        Material material = new Material();
        material.setName(name);
        material.setProjectId(projectId);
        Integer currentId = UserHolder.getUser().getId();
        material.setUserId(Math.toIntExact(currentId));
        material.setLocation(basePath + originalName);

        //在用户空间中也创建一个文件
        String userName = UserHolder.getUser().getUserName();
        String userDir = UserPath+userName+"/";
        //根据项目id获取项目名称
        Project project = projectService.getById(projectId);
        String projectDir = userDir+project.getName();
        String materialDir = projectDir+upperPath+originalName;
        //在用户空间中也上传文件
        fileService.upload(file,projectDir+upperPath);
        material.setLocInUser(materialDir);

        String Location = material.getLocation();
        String LocationT = basePathT + material.getName() + ".txt";
        String vecLocation = basePathT + material.getName() + "vector.txt";
        material.setVectorLocation(vecLocation);
        save(material);     //保存到数据库

        Record record = new Record();
        record.setUserId(currentId);
        record.setTime(LocalDateTime.now());
        record.setInformation("上传"+material.getName()+"资料");
        recordService.save(record);
        //将docx文件转换为txt文件
        TransTotxtS.DocxToTxt(Location,LocationT);

        //创建一个新线程来调用python脚本将用户上传的资料转换为向量
        Thread processThread = new Thread(() -> {
            // 处理上传的文件
            // 执行python脚本的代码
            try {
                //开启了命令执行器，输入指令执行python脚本
                Process process = Runtime.getRuntime()
                        .exec(pythonPath+ " " +
                                scriptPath+"/predict.py " +
                                "--model_path "+modelPath+" " +
                                "--file_path "+LocationT+" " +
                                "--sum_min_len 12 " +
                                "--gen_vec 1");

                //这种方式获取返回值的方式是需要用python打印输出，然后java去获取命令行的输出，在java返回
                InputStreamReader ir = new InputStreamReader(process.getInputStream(), "GB2312");
                LineNumberReader input = new LineNumberReader(ir);
                //读取命令行的输出
                String vec = input.readLine();
                //去掉输出的前后的中括号
                String vector = vec.substring(1, vec.length() - 1);
                //将向量写入到文件中
                FileWriter fw = new FileWriter(vecLocation);
                fw.write(vector);
                fw.close();
                input.close();
                ir.close();
            } catch (IOException e) {
                System.out.println("调用python脚本并读取结果时出错：" + e.getMessage());
            }

        });
        processThread.start();

        return material;
    }

    @Override
    public void deleteMaterial(Integer id) {
        //不使用Mybatis-plus中的方法来通过id来获取资料
        Material material = materialMapper.selcetById(id);
        //删除资料文件
        delete_vec_txt_file(material, basePathT);
        //删除数据库记录
        removeById(id);
    }

    @Override
    public PageInfo<MaterialDto> getPagedMaterial(Integer pageNum, int pageSize, int navSize, String materialName, Integer projectId, String projectName) {
        PageHelper.startPage(pageNum,pageSize);
        Integer currentId = UserHolder.getUser().getId();
        LambdaQueryWrapper<Material> materialLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(materialName!=null&& !materialName.equals("")){
            materialLambdaQueryWrapper.or().like(Material::getName,materialName);
        }
        if(projectId!=null){
            materialLambdaQueryWrapper.and(i->i.eq(Material::getProjectId,projectId));
        }
        if(projectName!=null&& !projectName.equals("")){
            LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            projectLambdaQueryWrapper.like(Project::getName,projectName);
            for (Project project : projectService.list(projectLambdaQueryWrapper)) {
                materialLambdaQueryWrapper.or().eq(Material::getProjectId,project.getId());
            }
        }
        materialLambdaQueryWrapper.and(i->i.eq(Material::getUserId,currentId));
        List<Material> list = list(materialLambdaQueryWrapper);
        ArrayList<MaterialDto> dtoList = new ArrayList<>();
        Map<Integer, Project> projectMap = projectService.getProjectMap();
        for(Material material:list){
            MaterialDto materialDto = new MaterialDto(material);
            materialDto.setProjectName(projectMap.get(material.getProjectId()).getName());
            dtoList.add(materialDto);
        }
        return new PageInfo<>(dtoList,navSize);
    }

    @Override
    public Map<Integer, Material> getMaterialMap() {
        return materialMapper.getMaterialMap();
    }

    @Override
    public void delete_vec_txt_file(Material material, String basePathT) {
        File file = new File(material.getLocation());
        if(file.exists()){
            file.delete();
        }
        String txtLocation = basePathT + material.getName() + ".txt";
        File txtFile = new File(txtLocation);
        if(txtFile.exists()){
            txtFile.delete();
        }
        String vectorLocation = material.getVectorLocation();
        File vectorFile = new File(vectorLocation);
        if(vectorFile.exists()){
            vectorFile.delete();
        }
    }

    @Override
    public void deleteElasticsearchDoc(Material material) throws IOException {
        EsQueryDto esQueryDto = new EsQueryDto();
        esQueryDto.setIndex("resumes");
        esQueryDto.setField("content");
        SearchResponse<HashMap> search= elasticsearchClient.search(s -> s.index(esQueryDto.getIndex()), HashMap.class);
        List<Hit<HashMap>> hits = search.hits().hits();
        for (Hit<HashMap> hit:hits){
            HashMap source = hit.source();
            HashMap file = (HashMap) source.get("file");
            String location = (String) file.get("url");
            location=location.substring(7);
            if(Objects.equals(material.getLocation(), location)){
                elasticsearchClient.delete(d->d.index(esQueryDto.getIndex()).id(hit.id()));
                break;
            }
        }
    }

    //此处删除操作为物理删除，删除数据库记录，删除文件，删除es中的文档
    @Override
    public void deleteById(Integer id) {
        Material material = getById(id);
        String locInUser = material.getLocInUser();
        if(!(locInUser==null)){
            java.io.File file = new java.io.File(locInUser);
            if(file.exists()){
                file.delete();
            }
        }
        delete_vec_txt_file(material,basePathT);
        try {
            deleteElasticsearchDoc(material);
        } catch (IOException e) {
            e.printStackTrace();
        }
        removeById(id);
    }

    @Override
    public double similarity(Integer materialIdA, Integer materialIdB) throws IOException {
        Material material1 = getById(materialIdA);
        Material material2 = getById(materialIdB);
        String vec1Loc = material1.getVectorLocation();
        StringBuffer vectorA = FileRdWt.readTxt(vec1Loc);
        String[] temp1 = vectorA.toString().split(",");
        double[] vector1 = new double[temp1.length];
        for (int i = 0; i < temp1.length; i++) {
            vector1[i] = Double.parseDouble(temp1[i]);
        }
        String vec2Loc = material2.getVectorLocation();
        StringBuffer vectorB = FileRdWt.readTxt(vec2Loc);
        String[] temp2 = vectorB.toString().split(",");
        double[] vector2 = new double[temp2.length];
        for (int i = 0; i < temp2.length; i++) {
            vector2[i] = Double.parseDouble(temp2[i]);
        }
        double similarity = TransTotxtS.cosineSimilarity(vector1, vector2);
        //返回相似度
        return similarity < 1 ? similarity : 1;
    }

    @Override
    public FuzzyQueryDto fuzzyQuery(EsQueryDto esQueryDto) throws Exception {
        esQueryDto.setIndex("resumes");
        esQueryDto.setField("content");
        SearchResponse<HashMap> search;
        if(!Objects.equals(esQueryDto.getWord(), "")){
            search = elasticsearchClient.search(s -> s
                    .index(esQueryDto.getIndex()).highlight(h -> h.fields(esQueryDto.getField(), f -> f.preTags("<span style='color:red'>").postTags("</span>").fragmentSize(100)))
                    .query(q -> q.match(t->t.field(esQueryDto.getField()).query(esQueryDto.getWord()))).from(esQueryDto.getFrom()).size(esQueryDto.getSize()), HashMap.class);
            List<Hit<HashMap>> hits = search.hits().hits();
            ArrayList<MaterialDto> res = new ArrayList<>();
            Map<Integer, User> userMap = userService.getUserMap();
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
                materialDto.setCategory(project.getCategory());
                materialDto.setUserName(userMap.get(materialDto.getUserId()).getUserName());
                String content = hit.highlight().get(esQueryDto.getField()).get(0);
                materialDto.setContent(content);
                res.add(materialDto);
            }
            FuzzyQueryDto fuzzyQueryDto = new FuzzyQueryDto();
            fuzzyQueryDto.setTotal((int)search.hits().total().value());
            fuzzyQueryDto.setList(res);
            return fuzzyQueryDto;
        }else{
            search = elasticsearchClient.search(s -> s
                    .index(esQueryDto.getIndex()).highlight(h -> h.fields(esQueryDto.getField(),f -> f.preTags("<span style='color:red'>").postTags("</span>").fragmentSize(100)))
                    .from(esQueryDto.getFrom()).size(esQueryDto.getSize()), HashMap.class);
            List<Hit<HashMap>> hits = search.hits().hits();
            ArrayList<MaterialDto> res = new ArrayList<>();
            Map<Integer, User> userMap = userService.getUserMap();
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
                materialDto.setCategory(project.getCategory());
                materialDto.setUserName(userMap.get(materialDto.getUserId()).getUserName());
                String content = (String) source.get("content");
                content=content.substring(0,Math.min(content.length(),100));
                materialDto.setContent(content);
                res.add(materialDto);
            }
            FuzzyQueryDto fuzzyQueryDto = new FuzzyQueryDto();
            fuzzyQueryDto.setTotal((int)search.hits().total().value());
            fuzzyQueryDto.setList(res);
            return fuzzyQueryDto;
        }
    }

}
