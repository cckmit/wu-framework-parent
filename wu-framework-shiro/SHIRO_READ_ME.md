## 简介
    不可以与oauth2 一起使用
    基于 spring-boot-starter-parent   2.2.1.RELEASE
## 框架集成
1. swagger 2.9.2
2. lombok 
3. spring-jpa
4. jwt
5.fastjson 
## pom 引入
            <dependency>
                <groupId>com.wu</groupId>
                <artifactId>wu-framework-shiro</artifactId>
                <version>1.0.5</version>
            </dependency>
## 功能
    用户登录产生令牌（默认jwt 校验）
    
## 项目结构 
    shiro 
    ├── com
    │   └── wuframework
    │       ├── EnableAuthorizationServer.java      注解（框架使用引用这个注解）
    │       ├── wuframeworkConfig.java        框架配置文件主要用于扫描当前框架注入容器中
    │       ├── pojo                                
    │       │   ├── bo                              常用业务对象（范型）
    │       │   │   ├── ThreeBO.java
    │       │   │   └── TwoBO.java
    │       │   └── qo
    │       │       └── UniversalSearchQO.java        通用页面搜索参数封装
    │       ├── com.wuframework.response                              数据返回及数据校验
    │       │   ├── Result.java                         返回结果
    │       │   ├── ResultCode.java                     返回结果编码接口
    │       │   ├── ResultFactory.java                  返回结果工厂
    │       │   ├── enmus                                   
    │       │   │   └── DefaultResultCode.java          默认返回结果枚举 
    │       │   ├── handler                             
    │       │   │   └── GlobalExceptionHandler.java      全局异常处理
    │       │   └── mark                                
    │       │       ├── JsonViewType.java
    │       │       └── ValidType.java                    参数校验类 
    │       └── shiro                   权限处理
    │           ├── DefaultUserDetailsServiceImpl.java  默认查询用户服务 
    │           ├── LoginService.java    
    │           ├── LoginServiceImpl.java
    │           ├── UserDetailsService.java  
    │           ├── annotation
    │           │   ├── AccessTokenUser.java            令牌解析成用户注解
    │           │   ├── RequestPage.java                分页注解（区分是mybatis or Jpa 下游具体实现）
    │           │   └── RequiredPermission.java         请求权限注解
    │           ├── config
    │           │   ├── CustomCORSConfiguration.java    跨域请求配置
    │           │   └── pro                             读取配置文件的类放置的位置
    │           ├── domain                              领域对象
    │           │   ├── AccessToken.java
    │           │   ├── Authentication.java 
    │           │   ├── DefaultAccessToken.java         
    │           │   ├── DefaultAuthentication.java
    │           │   └── Login.java
    │           ├── endpoint
    │           │   └── TokenKeyEndpoint.java           暴露的接口服务
    │           ├── exceptions                          异常
    │           │   ├── ExtractScopeException.java  
    │           │   ├── ShiroException.java 
    │           │   └── TokenAuthorizationException.java
    │           ├── factory 
    │           │   └── AuthorizationServerFactory.java
    │           ├── model                               实体
    │           │   ├── AccessToken.java  
    │           │   ├── User.java
    │           │   └── UserDetails.java
    │           ├── persistence                         持久层
    │           │   └── jpa
    │           ├── shiro                               权限控制拦截器（待完善）
    │           │   ├── JwtFilter.java
    │           │   ├── JwtRealm.java 
    │           │   ├── JwtToken.java
    │           │   └── ShiroConfig.java
    │           ├── token                                
    │           │   ├── TokenEnhancer.java              
    │           │   ├── TokenStore.java                 令牌处理接口
    │           │   └── store                           令牌处理具体实现（jwt，jdbc）
    │           ├── util
    │           │   ├── JwtUtil.java
    │           │   ├── JwtUtil1.java
    │           │   └── ShiroContextUtil.java           获取上下文对象工具
    │           └── web
    │               ├── ShiroWebMvcConfigurer.java      实现WebMvcConfigurer
    │               ├── configuration                   配置
    │               ├── interceptors                    处理器拦截器（包含用户权限校验拦截器）
    │               └── methodresolver                  方法注解解析器（实现用户信息获取）
    └── resources
        └── access_token.sql
    


### 版本1.0.1 添加异常处理  返回code 集成
### 版本1.0.4 框架添加注解扫描 使用示例
    @Component
    @EnableAuthorizationServer
    public class AuthorizationServerConfig {
    
      @Bean
      private AuthorizationServerEndpointsConfigurer authorizationServerEndpointsConfigurer() {
        AuthorizationServerEndpointsConfigurer authorizationServerEndpointsConfigurer =
            new AuthorizationServerEndpointsConfigurer();
        authorizationServerEndpointsConfigurer
            .setTokenStore(new JwtTokenStore())
            .setUserDetails(SysUser.class);
        return authorizationServerEndpointsConfigurer;
      }
    
      @Bean
      private TokenStore tokenStore() {
        return new JwtTokenStore();
      }
    }
### 版本1.0.4  ShiroProperties 归属spring下配置文件使用spring.shiro.expire_time=3600000 
    @Component
    @EnableAuthorizationServer
    public class AuthorizationServerConfig {
    
      @Bean
      private AuthorizationServerEndpointsConfigurer authorizationServerEndpointsConfigurer() {
        AuthorizationServerEndpointsConfigurer authorizationServerEndpointsConfigurer =
            new AuthorizationServerEndpointsConfigurer();
        authorizationServerEndpointsConfigurer
            .setTokenStore(new JwtTokenStore())
            .setUserDetails(SysUser.class);
        return authorizationServerEndpointsConfigurer;
      }
      @Resource
      private ShiroProperties shiroProperties;
    
      @Bean
      private TokenStore tokenStore() {
        DefaultJwtAccessTokenConverter defaultJwtAccessTokenConverter=new DefaultJwtAccessTokenConverter(shiroProperties);
        return new JwtTokenStore(defaultJwtAccessTokenConverter);
      }
    }
### 版本 1.0.5 TokenStore 添加jdbc 实现(取消ShiroProperties 手动注入问题)
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
### shiro 1.0.6升级1.0.7  结构调整 添加反射异常处理抛出 新增AuthorizationServerEndpointsConfigurer的实现类未注入容器
    @EnableAuthorizationServer
    public class AuthorizationServerConfig {
    
    
        @Resource
        private DataSource dataSource;
        @Resource
        private UserDetailsService userDetailsService;
    
        @Bean
        private AuthorizationServerEndpointsConfigurer authorizationServerEndpointsConfigurer() {
            AuthorizationServerEndpointsConfigurer authorizationServerEndpointsConfigurer =
                    new DefaultAuthorizationServerEndpointsConfigurer();
            authorizationServerEndpointsConfigurer
                    .setTokenStore(new JdbcTokenStore(dataSource))
                    .setUserDetails(SysUser.class)
                    .setUserDetailsService(userDetailsService);
            return authorizationServerEndpointsConfigurer;
        }


### shiro 1.0.7升级1.0.8 新增令牌移出接口 新增令牌移出注解
    1.RemoveAccessToken
    2.新增CustomResponseException 自定义抛出枚举异常
    3.新增wuEnums 枚举接口
### shiro 1.0.8 升级1.0.9 (下个版本)
    1.添加控制器接口 自动实现CRUD
