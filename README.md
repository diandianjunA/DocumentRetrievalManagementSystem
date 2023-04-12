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
传入json格式的数据：
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
