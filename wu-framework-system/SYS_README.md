## 简介
    不可以与oauth2 一起使用
    基于 spring-boot-starter-parent   2.2.1.RELEASE
    当前版本 1.0.1
    
### 依赖
    1.wu-framework-shiro 1.0.5
    2.wu-common-until  v_2.0.1
    3.mybatisplus-spring-boot-starter
    4.fastdfs-client
    5.spring-boot-starter-web
    6.spring-boot-autoconfigure
    7.spring-boot-starter-data-redis
    8.mysql-connector-java
    

### pom 引入
            <dependency>
                <groupId>com.wu</groupId>
                <artifactId>wu-framework-system</artifactId>
                <version>1.0.1-SNAPSHOT</version>
            </dependency>

### 使用方式      
    同wu-framework-shiro 使用方式相同
    @Component
    @EnableAuthorizationServer
    public class AuthorizationServerConfig {
    
      @Bean
      private AuthorizationServerEndpointsConfigurer authorizationServerEndpointsConfigurer() {
        AuthorizationServerEndpointsConfigurer authorizationServerEndpointsConfigurer =
                new AuthorizationServerEndpointsConfigurer();
        authorizationServerEndpointsConfigurer
                .setTokenStore(tokenStore())
                .setUserDetails(SysUser.class);
        return authorizationServerEndpointsConfigurer;
      }
    
      @Resource
      private ShiroProperties shiroProperties;
    
      @Resource
      private DataSource dataSource;
    
      @Bean
      private TokenStore tokenStore() {
    //    DefaultJwtAccessTokenConverter defaultJwtAccessTokenConverter = new DefaultJwtAccessTokenConverter(shiroProperties);
    //    return new JwtTokenStore(defaultJwtAccessTokenConverter);
        return new JdbcTokenStore(dataSource);
      }
    }

    
### 结构
    .
    ├── SystemBaseApplication.java              单个项目启动类
    ├── common                          
    │   ├── config                              配置文件
    │   │   ├── ApplicationProperties.java      读取application 配置文件
    │   │   ├── AuthorizationServerConfig.java  已经弃用
    │   │   ├── DefaultSwaggerConfig.java        默认swagger配置 下游使用继承此类重写方法即可（主要作用注释注解解析出来的用户信息）
    │   │   ├── JpaConfig.java                  jpa配置 （默认创建数据库表结构字段 或更新字段）
    │   │   ├── MybatisPlusConfig.java          mybatis配置
    │   │   └── UserDetailsServiceImpl.java     下游实现 UserDetailsService 并注入
    │   ├── consts                              
    │   │   ├── CacheConsts.java                 缓存静态变量
    │   │   ├── CharValidConsts.java             字段校验变量
    │   │   └── ConfigConsts.java                   
    │   ├── dto                                 
    │   │   ├── DeptDTO.java
    │   │   ├── MenuNode.java
    │   │   ├── PageParams.java
    │   │   ├── SignatureDTO.java
    │   │   └── UserDTO.java
    │   ├── entity                              数据库表字段对应实体
    │   │   ├── DefaultSysUser.java
    │   │   ├── DefaultSysUserDetails.java
    │   │   ├── SysConfig.java
    │   │   ├── SysDept.java
    │   │   ├── SysDict.java
    │   │   ├── SysDictType.java
    │   │   ├── SysLog.java
    │   │   ├── SysMenu.java
    │   │   ├── SysRole.java
    │   │   ├── SysRoleMenu.java
    │   │   ├── SysUserMessage.java
    │   │   └── SysUserRole.java
    │   └── vo                                 返回页面实体对象
    │       ├── DeptLocationVO.java
    │       ├── DeptNodeVO.java
    │       ├── DeptRankVO.java
    │       ├── DeptSampleCountVO.java
    │       ├── DeptVO.java
    │       ├── SimpleSelectVo.java
    │       ├── SysDictVO.java
    │       └── UserVO.java
    ├── controller                             控制器
    │   ├── SysConfigController.java
    │   ├── SysDeptController.java
    │   ├── SysDictController.java
    │   ├── SysDictTypeController.java
    │   ├── SysMenuController.java
    │   ├── SysRoleController.java
    │   ├── SysRoleMenuController.java
    │   ├── SysUserController.java
    │   └── SysUserRoleController.java
    ├── listen                                
    │   └── ApplicationStartedEventListener.java   监听服务启动（将配置数据，部门数据添加到redis）
    ├── mapper                                      持久层
    │   ├── SysConfigMapper.java
    │   ├── SysDeptMapper.java
    │   ├── SysDictMapper.java
    │   ├── SysDictTypeMapper.java
    │   ├── SysMenuMapper.java
    │   ├── SysRoleMapper.java
    │   ├── SysRoleMenuMapper.java
    │   ├── SysUserMapper.java
    │   └── SysUserRoleMapper.java
    ├── redis                                      redis缓存
    │   ├── RedisConfig.java
    │   └── RedisRepository.java
    ├── serivce                                     业务服务接口
    │   ├── DefaultSysUserService.java
    │   ├── SysConfigService.java
    │   ├── SysDeptService.java
    │   ├── SysDictService.java
    │   ├── SysDictTypeService.java
    │   ├── SysMenuService.java
    │   ├── SysRoleMenuService.java
    │   ├── SysRoleService.java
    │   ├── SysUserRoleService.java
    │   └── impl                                    业务服务实现类
    │       ├── DefaultSysUserServiceImpl.java
    │       ├── SysConfigServiceImpl.java
    │       ├── SysDeptServiceImpl.java
    │       ├── SysDictServiceImpl.java
    │       ├── SysDictTypeServiceImpl.java
    │       ├── SysMenuServiceImpl.java
    │       ├── SysRoleMenuServiceImpl.java
    │       ├── SysRoleServiceImpl.java
    │       └── SysUserRoleServiceImpl.java
    └── utils                                       工具类
        ├── BusinessUtils.java
        ├── FastDFSClientWrapper.java
        ├── JwtUtil.java
        └── RedisUtils.java
### 功能
    初始化数据库 创建表
    sys_user
    sys_config
    sys_dept
    sys_dict
    sys_dict_type
    sys_log
    sys_menu
    sys_role
    sys_role_menu
    sys_user_message
    sys_user_role
    提供基础的业务
### sys 1.0.1升级1.0.4 添加RequiredPermission 权限注解
    1.内部整合依赖拆分 db  file  swagger
    2.业务服务支持二次开发
    3.实现枚举 自动装载入库
    4.新增分配指定角色标识给指定用户
    5.优化AccessTokenUser RequiredRole RequiredPermission 同时使用
### sys 1.0.4升级 1.0.4
    1.wuEnumsProperty 兼容 wuEnumsService
### TODO 下个版本1.0.4
    1.wuEnumsProperty 替换 wuEnumsService 添加枚举是否自动装载按钮 (已经完成)
    2.配置文件统一 (未完成)
    3.异常策略支持扩展(目前支持)
    4.日志系统 (暂时支持动态注解使用)


    