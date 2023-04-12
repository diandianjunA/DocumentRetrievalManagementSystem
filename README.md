# 文档检索管理系统

## 系统概述
本系统是一个基于云端的文档资料统一管理平台，实现文档的集中管
理和知识元的统一归档。在该系统中，用户可以方便地上传、下载和共享历史案
例资料，包括各种技术文档、相关规范等。系统支持帮助用户快速查找和检索所
需的信息。此外，系统还支持对于知识元的管理，知识元和技术方案唯一绑定。
该系统还拥有技术方案自动生成的功能。通过算法分析，系统能够在新建工程项
目时，通过和历史案例资料的相似度对比，自动生成技术方案。

## 系统模块
- 用户模块
- 文件控制模块

## 用户模块
用户类定义

```java
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
@ApiModel(value="User对象", description="")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "用户的密码")
    private String password;

    @ApiModelProperty(value = "用户是否禁用 0表示禁用 1表示正常")
    private Integer status;
}
```
### 用户登录模块
接口路径：/user/login

以post方式传入json格式的数据：
```javascript
userName:xxx
password:xxx
```
当数据传入后，首先进入数据库查询是否存在该用户，如果不存在则插入一条用户；如果存在则查看用户是否被禁用，如果未被禁用则登录成功
登录成功后将用户id存入session中便于后续操作的执行
```java
public User login(@RequestBody Map<String,String> map, HttpSession session){
        String userName = map.get("userName");
        String password = map.get("password");
        //查看该用户是否为新用户
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUserName,userName);
        User user = getOne(userLambdaQueryWrapper);
        if(user==null){
            //是新用户,自动注册
            user = new User();
            user.setUserName(userName);
            user.setPassword(password);
            user.setStatus(1);
            save(user);
            return user;
        }else{
            if(user.getStatus()==0){
                throw new HaveDisabledException("用户已被禁用");
            }else{
                //将用户的信息存到session中，这样可以通过过滤器
                session.setAttribute("user",user.getId());
                return user;
            }
        }
    }
```
## 文件控制模块

### 配置MultipartResolver

#### SpringMvc配置文件

```java
@Slf4j
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射");
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    /**
     * 扩展MVC框架的消息转换器
     * @param converters MVC原先默认的转换器
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将java对象转为json
        converter.setObjectMapper(new JacksonObjectMapper());
        //将这个消息转换器追加到默认的转换器中
        converters.add(0,converter);
    }

    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver(){
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        //resolveLazily属性启用是为了推迟文件解析，以在在UploadAction中捕获文件大小异常
        resolver.setResolveLazily(true);
        resolver.setMaxInMemorySize(40960);
        //上传文件大小 50M 50*1024*1024
        resolver.setMaxUploadSize(50*1024*1024);
        return resolver;
    }
}
```

#### Web配置文件

```java
public class WebConfig implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext ctx=new AnnotationConfigWebApplicationContext();
        ctx.register(WebMvcConfig.class);//注册SpringMvc的配置类WebMvcConfig
        ctx.setServletContext(servletContext);//和当前ServletContext关联
        /**
         * 注册SpringMvc的DispatcherServlet
         */
        ServletRegistration.Dynamic servlet=servletContext.addServlet("dispatcher",new DispatcherServlet(ctx));
        servlet.addMapping("/");
        servlet.setLoadOnStartup(1);
    }
}
```

### 文件上传
接口路径：/file/upload

前端表单
```html
<form method="post" action="/file/upload" enctype="multipart/form-data">
    <input name="file" type="file"  />
    <input type="submit" value="提交" /> 
</form>
```

```java
@Override
    public String upload(MultipartFile file,String basePath) {
        // 1.获取当前上传的文件名
        String originalFilename = file.getOriginalFilename();
        // 2.截取当前文件名的格式后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 3.判断要存储文件的路径是否存在，不存在则创建
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }
        // 4.将上传的文件保存到指定的路径
        try {
            file.transferTo(new File(basePath + originalFilename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 5.返回数据给前端
        return originalFilename;
    }
```

### 文件下载
接口路径：/file/download

以get方式传入fileName
```javascript
filename:xxx
```

```java
@Override
    public ResponseEntity<byte[]> download(HttpSession session, String basePath, String fileName) throws IOException {
        //获取服务器中文件的真实路径
        String realPath = basePath+fileName;
        //创建输入流
        InputStream is = Files.newInputStream(Paths.get(realPath));
        //创建字节数组
        byte[] bytes = new byte[is.available()];
        //将流读到字节数组中
        is.read(bytes);
        //创建HttpHeaders对象设置响应头信息
        MultiValueMap<String, String> headers = new HttpHeaders();
        //设置要下载方式以及下载文件的名字
        headers.add("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName,"UTF-8"));
        //设置响应状态码
        HttpStatus statusCode = HttpStatus.OK;
        //创建ResponseEntity对象
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, headers,
                statusCode);
        //关闭输入流
        is.close();
        return responseEntity;
    }
```