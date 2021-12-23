<p></p>
<p></p>

<p align="center">
  <img alt="logo" src="https://mochatcloud.oss-cn-beijing.aliyuncs.com/github/logo.png">
</p>
<h2 align="center">MoChat —— 让企业微信开发更简单</h2>

<div align="center">


</div>

<p></p>
<p></p>
<p></p>
<p></p>

![输入图片说明](https://mochatcloud.oss-cn-beijing.aliyuncs.com/github/header.png)

## 项目简介

> MoChat, easy way to WeWork

MoChat 是开源的企业微信应用开发框架&引擎，是一套通用的企业微信管理系统，得益于 `Hyperf` 框架的优秀，MoChat 可提供超高性能的同时，也保持着极其灵活的可扩展性。

### 应用场景

可用于电商、金融、零售、餐饮服装等服务行业的企业微信用户，通过简单的分流、引流转化微信客户为企业客户，结合强大的后台支持，灵活的运营模式，建立企业与客户的强联系，让企业的盈利模式有了多种不同的选择。

### 功能特性

六大模块助力企业营销能力升级：

* 引流获取：通过多渠道活码获取客户，条理有序分类
* 客户转化：素材库、欢迎语互动客户，加强与客户联系
* 客户管理：精准定位客户，一对一标签编辑，自定义跟踪轨迹，流失客户提醒与反馈
* 客户群管理：于客户的基础，进一步获取客户裂变，自动拉群。集中管理，快速群发
* 聊天侧边栏：提高企业员工沟通效率，精准服务
* 企业风控：客户聊天记录存档，并设立敏感词库、敏感词报警，多方位跟进管理员工服务

### 业务架构
严格的分层来保证架构的灵活性

![架构](https://mochatcloud.oss-cn-beijing.aliyuncs.com/github/framework.png "mochat微信.png")

### 核心技术
* 前端技术栈: `Vue`、`Vuex`、`Vant`、`Ant Design of Vue`
* 后端技术栈: `Java`、`MySQL`、`Redis`、`Spring Boot`、`MyBatis-Plus`、`Spring Security`、`Jwt`

### 环境部署

#### 准备工作

```
Java >= 1.8 (推荐1.8版本)
Node.js >= 10
```

#### 运行系统

后端运行

##### 下载项目

假设你的安装目录为 /data/www/

```bash
# 进入项目目录
cd /data/www/

# 克隆项目文件
git clone https://github.com/mochat-cloud/mochat-java.git

# 更新子模块文件
git submodule update --init --recursive
```

#### 修改相关配置文件

cd /data/www/mochat-java/api-server

修改 MySQL 数据库连接

- 编辑 `resources` 目录下的 `application.yml`查看spring配置中的active属性，然后根据属性值找到application-dev.xml或者是application-prod.xml（默认是dev环境）
- `url` : 服务器地址
- `username` : 账号
- `password `: 密码

修改 Redis 相关配置

开发环境配置

- 编辑 `resources` 目录下的 `application.yml`
- `port` : 端口

- 然后会在项目下生成 ` target` 文件夹包含 `war`  或 `jar `
- `jar` 部署方式：使用命令行执行 `java –jar mochat.jar`
- `war` 部署方式：`pom.xml packaging` 修改为 `war`  放入 `tomcat` 服务器 `webapps` 直接启动bin目录下的./startup.sh(linux环境下)

- Nginx 配置：具体参考开发文档

- 初始管理员账号为 18888888888 密码123456

##### 运行方式

###### docker 方式自动化运行

- 直接运行 `api-server/start.sh` 文件自动化部署

###### 手动运行

```
MySQL >= 5.7
Redis >= 3.0
```

- 安装上述软件
- 创建数据库 `mochat` 并导入数据脚本 `api-server/data/mysql/init/` 下的 sql 文件
- 导入项目到 IDEA 中
- 打开运行 `com.mochat.mochat.MoChatApplication.java`

##### 前端运行

```bash
# 进入项目目录
cd /path/to/mochat/dashboard

# 安装依赖
yarn install

# 修改 .env 中的配置 
VUE_APP_API_BASE_URL= 接口地址

# 编译直接查看
yarn run dev


# 编译生成dist
yarn run build
```

##### 前端部署

当项目开发完毕，只需要运行一行命令就可以打包你的应用

```bash
# dashboard 打包正式环境
yarn run build

# sidebar 打包正式环境
yarn run build
```

构建打包成功之后，会在根目录生成 `dist` 文件夹，里面就是构建打包好的文件，通常是 `.js` 、`.css`、`index.html` 等静态文件。

通常情况下 `dist` 文件夹的静态文件发布到你的 `nginx` 或者静态服务器即可，其中的 `index.html` 是后台服务的入口页面。

### 项目介绍

#### 文件结构
```
.
├── api-server------------------------------------------ 后端接口代码
├── dashboard------------------------------------------- 管理后台前端代码
├── sidebar--------------------------------------------- 聊天侧边栏前端代码
└── workbench------------------------------------------- 工作台前端代码
```

##### 后端结构

```
api-server
├── com.mochat.mochat
│  ├── controller------------------------------------------- 控制器
│  ├── config----------------------------------------------- 应用配置
│  ├── dao-------------------------------------------------- 数据层
│  ├── common----------------------------------------------- 公共类
│  ├── interceptor------------------------------------------ 拦截器
│  ├── job-------------------------------------------------- 定时任务
│  ├── model------------------------------------------------ 模型层
│  ├── service---------------------------------------------- 逻辑层
│  ├── weixin.mp-------------------------------------------- 微信相关配置
├── docker-compose.yml
├── Dockerfile
├── pom.xml
```

##### 前端结构

```
dashboard 和 sidebar 项目结构类似
.
├── README.md------------------------------------------- 项目说明
├── babel.config.js------------------------------------- babel配置文件
├── config
│   ├── plugin.config.js-------------------------------- 插件配置文件
│   └── themePluginConfig.js---------------------------- 主题配置文件
├── jest.config.js
├── jsconfig.json
├── package.json
├── postcss.config.js
├── public
│   ├── favicon.ico------------------------------------- 浏览器icon
│   └── index.html-------------------------------------- Vue 入口模板
├── src
│   ├── App.vue----------------------------------------- Vue 模板入口
│   ├── api--------------------------------------------- Api ajax 等
│   ├── assets------------------------------------------ 本地静态资源
│   ├── components-------------------------------------- 业务通用组件
│   ├── core-------------------------------------------- 项目引导, 全局配置初始化，依赖包引入等
│   ├── global.less------------------------------------- 全局样式
│   ├── layouts----------------------------------------- 控制器
│   ├── main.js----------------------------------------- Vue 入口 JS
│   ├── router------------------------------------------ Vue-Router
│   ├── store------------------------------------------- Vuex
│   ├── utils------------------------------------------- 工具库
│   └── views------------------------------------------- 业务页面入口和常用模板
├── vue.config.js--------------------------------------- Vue主配置
└── webstorm.config.js---------------------------------- ide配置文件
```

### 联系作者加入群

![输入图片说明](https://mochatcloud.oss-cn-beijing.aliyuncs.com/github/contact-qr3.png "mochat微信.png")

### 部分演示图，持续更新

![输入图片说明](https://mochatcloud.oss-cn-beijing.aliyuncs.com/github/demo-1.png "demo演示.png")
![输入图片说明](https://mochatcloud.oss-cn-beijing.aliyuncs.com/github/demo-2.png "demo演示.png")
![输入图片说明](https://mochatcloud.oss-cn-beijing.aliyuncs.com/github/demo-3.png "demo演示.png")
![输入图片说明](https://mochatcloud.oss-cn-beijing.aliyuncs.com/github/demo-4.png "demo演示.png")
![输入图片说明](https://mochatcloud.oss-cn-beijing.aliyuncs.com/github/demo-5.png "demo演示.png")
![输入图片说明](https://mochatcloud.oss-cn-beijing.aliyuncs.com/github/demo-6.png "demo演示.png")
![输入图片说明](https://mochatcloud.oss-cn-beijing.aliyuncs.com/github/demo-7.png "demo演示.png")
![输入图片说明](https://mochatcloud.oss-cn-beijing.aliyuncs.com/github/demo-8.png "demo演示.png")
![输入图片说明](https://mochatcloud.oss-cn-beijing.aliyuncs.com/github/demo-9.png "demo演示.png")


### 版权声明

MoChat 开源版遵循 [`GPL-3.0`](https://github.com/mochat-cloud/mochat/blob/main/LICENSE "GPL-3.0") 开源协议发布，并提供免费研究使用，但绝不允许修改后和衍生的代码做为闭源的商业软件发布和销售！
