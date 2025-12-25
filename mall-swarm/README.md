# mall-swarm
> 5. **分支说明**：`master`分支基于Spring Cloud 2023+Spring Boot 3.2，`dev-v2`分支基于Spring Cloud 2021+Spring Boot 2.7。

## 项目简介

`mall-swarm`是一套微服务商城系统，采用了 Spring Cloud 2023 & Alibaba、Spring Boot 3.2、Sa-Token、MyBatis、Elasticsearch、Docker、Kubernetes等核心技术，同时提供了基于Vue的管理后台方便快速搭建系统。`mall-swarm`在电商业务的基础集成了注册中心、配置中心、监控中心、网关等系统功能。文档齐全，附带全套Spring Cloud教程。

## 项目演示

### 后台管理系统

![后台管理系统功能演示](./document/resource/mall_admin_show.png)

### 前台商城系统


![前台商城系统功能演示](./document/resource/re_mall_app_show.jpg)

## 项目架构

### 系统架构

![系统架构图](./document/resource/mall_micro_service_arch.jpg)

### 业务架构

![业务架构图](./document/resource/re_mall_business_arch.jpg)

### 组织结构

``` lua
mall
├── mall-common -- 工具类及通用代码模块
├── mall-mbg -- MyBatisGenerator生成的数据库操作代码模块
├── mall-auth -- 基于Spring Security Oauth2的统一的认证中心
├── mall-gateway -- 基于Spring Cloud Gateway的微服务API网关服务
├── mall-monitor -- 基于Spring Boot Admin的微服务监控中心
├── mall-admin -- 后台管理系统服务
├── mall-search -- 基于Elasticsearch的商品搜索系统服务
├── mall-portal -- 移动端商城系统服务
├── mall-demo -- 微服务远程调用测试服务
└── config -- 配置中心存储的配置
```

## 技术选型

### 后端技术

| 技术                   | 说明                 | 官网                                                 |
| ---------------------- | -------------------- | ---------------------------------------------------- |
| Spring Cloud           | 微服务框架           | https://spring.io/projects/spring-cloud              |
| Spring Cloud Alibaba   | 微服务框架           | https://github.com/alibaba/spring-cloud-alibaba      |
| Spring Boot            | 容器+MVC框架         | https://spring.io/projects/spring-boot               |
| Sa-Token               | 认证和授权框架       | https://github.com/dromara/Sa-Token                   |
| MyBatis                | ORM框架              | http://www.mybatis.org/mybatis-3/zh/index.html       |
| MyBatisGenerator       | 数据层代码生成       | http://www.mybatis.org/generator/index.html          |
| PageHelper             | MyBatis物理分页插件  | http://git.oschina.net/free/Mybatis_PageHelper       |
| Knife4j                | 文档生产工具         | https://github.com/xiaoymin/swagger-bootstrap-ui     |
| Elasticsearch          | 搜索引擎             | https://github.com/elastic/elasticsearch             |
| RabbitMq               | 消息队列             | https://www.rabbitmq.com/                            |
| Redis                  | 分布式缓存           | https://redis.io/                                    |
| MongoDb                | NoSql数据库          | https://www.mongodb.com/                             |
| Docker                 | 应用容器引擎         | https://www.docker.com/                              |
| Druid                  | 数据库连接池         | https://github.com/alibaba/druid                     |
| OSS                    | 对象存储             | https://github.com/aliyun/aliyun-oss-java-sdk        |
| MinIO                  | 对象存储             | https://github.com/minio/minio                       |
| LogStash               | 日志收集             | https://github.com/logstash/logstash-logback-encoder |
| Lombok                 | 简化对象封装工具     | https://github.com/rzwitserloot/lombok               |
| Seata                  | 全局事务管理框架     | https://github.com/seata/seata                       |
| Portainer              | 可视化Docker容器管理 | https://github.com/portainer/portainer               |
| Jenkins                | 自动化部署工具       | https://github.com/jenkinsci/jenkins                 |
| Kubernetes             | 应用容器管理平台     | https://kubernetes.io/                               |

### 前端技术

| 技术       | 说明                  | 官网                           |
| ---------- | --------------------- | ------------------------------ |
| Vue        | 前端框架              | https://vuejs.org/             |
| Vue-router | 路由框架              | https://router.vuejs.org/      |
| Vuex       | 全局状态管理框架      | https://vuex.vuejs.org/        |
| Element    | 前端UI框架            | https://element.eleme.io/      |
| Axios      | 前端HTTP框架          | https://github.com/axios/axios |
| v-charts   | 基于Echarts的图表框架 | https://v-charts.js.org/       |

### 移动端技术

| 技术         | 说明             | 官网                                    |
| ------------ | ---------------- | --------------------------------------- |
| Vue          | 核心前端框架     | https://vuejs.org                       |
| Vuex         | 全局状态管理框架 | https://vuex.vuejs.org                  |
| uni-app      | 移动端前端框架   | https://uniapp.dcloud.io                |
| mix-mall     | 电商项目模板     | https://ext.dcloud.net.cn/plugin?id=200 |
| luch-request | HTTP请求框架     | https://github.com/lei-mu/luch-request  |

## 环境搭建

### 开发环境

| 工具          | 版本号 | 下载                                                         |
| ------------- | ------ | ------------------------------------------------------------ |
| JDK           | 17     | https://www.oracle.com/cn/java/technologies/downloads/#java17 |
| Mysql         | 5.7    | https://www.mysql.com/                                       |
| Redis         | 7.0    | https://redis.io/download                                    |
| Elasticsearch | 7.17.3 | https://www.elastic.co/cn/downloads/elasticsearch            |
| Kibana        | 7.17.3 | https://www.elastic.co/cn/downloads/kibana                   |
| Logstash      | 7.17.3 | https://www.elastic.co/cn/downloads/logstash                 |
| MongoDb       | 5.0    | https://www.mongodb.com/download-center                      |
| RabbitMq      | 3.10.5 | http://www.rabbitmq.com/download.html                        |
| nginx         | 1.22   | http://nginx.org/en/download.html                            |

### 搭建步骤

- Windows环境搭建请参考：[mall-swarm项目后端开发环境搭建](https://cloud.macrozheng.com/start/mall_swarm_deploy_windows.html);
- `mall-admin-web`项目的安装及部署请参考：[mall-swarm前端开发环境搭建](https://cloud.macrozheng.com/start/mall_swarm_deploy_windows_web.html);

## docker 部署补充

- 修改pom 里面的 docker.host 地址为本地服务器/远程服务器docker 地址
- 本地 运行maven clean 和 package
- docker compose -f docker-compose-env.yml up -d
- docker compose -f docker-compose-app.yml up -d

- docker login --username=${DOCKER_USERNAME} registry.cn-hangzhou.aliyuncs.com
- docker tag ishopdev-mall-portal:1.0-SNAPSHOT registry.cn-hangzhou.aliyuncs.com/yinshi/ishopdev-mall-portal:1.0-SNAPSHOT
- docker tag ishopdev-mall-selfcheck:1.0-SNAPSHOT registry.cn-hangzhou.aliyuncs.com/yinshi/ishopdev-mall-selfcheck:1.0-SNAPSHOT
- docker tag ishopdev-mall-admin:1.0-SNAPSHOT registry.cn-hangzhou.aliyuncs.com/yinshi/ishopdev-mall-admin:1.0-SNAPSHOT
- docker tag ishopdev-mall-gateway:1.0-SNAPSHOT registry.cn-hangzhou.aliyuncs.com/yinshi/ishopdev-mall-gateway:1.0-SNAPSHOT
- docker tag ishopdev-mall-search:1.0-SNAPSHOT registry.cn-hangzhou.aliyuncs.com/yinshi/ishopdev-mall-search:1.0-SNAPSHOT
- docker tag ishopdev-mall-auth:1.0-SNAPSHOT registry.cn-hangzhou.aliyuncs.com/yinshi/ishopdev-mall-auth:1.0-SNAPSHOT
- docker tag ishopdev-mall-monitor:1.0-SNAPSHOT registry.cn-hangzhou.aliyuncs.com/yinshi/ishopdev-mall-monitor:1.0-SNAPSHOT

- 登录Docker Hub 【或者自己的镜像服务地址，推荐使用国内的镜像地址】 docker login
- 推送到远程仓库  docker push fghsg/mall-admin:1.0-SNAPSHOT
- docker push registry.cn-hangzhou.aliyuncs.com/yinshi/ishopdev-mall-portal:1.0-SNAPSHOT
- docker push registry.cn-hangzhou.aliyuncs.com/yinshi/ishopdev-mall-selfcheck:1.0-SNAPSHOT
- docker push registry.cn-hangzhou.aliyuncs.com/yinshi/ishopdev-mall-admin:1.0-SNAPSHOT
- docker push registry.cn-hangzhou.aliyuncs.com/yinshi/ishopdev-mall-gateway:1.0-SNAPSHOT
- docker push registry.cn-hangzhou.aliyuncs.com/yinshi/ishopdev-mall-search:1.0-SNAPSHOT
- docker push registry.cn-hangzhou.aliyuncs.com/yinshi/ishopdev-mall-auth:1.0-SNAPSHOT
- docker push registry.cn-hangzhou.aliyuncs.com/yinshi/ishopdev-mall-monitor:1.0-SNAPSHOT

- nginx 需要到 /mydata/nginx/conf 里面修改nginx.conf 和 mine.type

- 需要进入 mysql 容器创建数据库并执行 sql 文件
- CREATE DATABASE mall CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;   【创建数据库】
- docker cp mall.sql mysql:/mall.sql  【退出容器后，把sql 文件放到当前目录下再执行复制脚本】
- mysql -u root -p mall < /mall.sql 【进入mysql后执行 sql 文件】

- elasticsearch 需要安装对应的分词器，下载地址 https://release.infinilabs.com/analysis-ik/stable/
- 复制到容器里 docker cp elasticsearch-analysis-ik-7.17.3.zip elasticsearch:/usr/share/elasticsearch/plugins/
- 进入容器 docker exec -it elasticsearch /bin/bash
- 解压分词器  cd /usr/share/elasticsearch/plugins/
  unzip elasticsearch-analysis-ik-7.17.3.zip -d analysis-ik
- 重启容器 docker restart elasticsearch


- logstash 需要在服务器配置 logstash.conf 文件
- rabbitmq 如果跑不起来，检查一下日志，如果是目录权限不够则执行 sudo chmod -R 777 /mydata/rabbitmq/log 添加权限
- rabbitmq，需要进入容器 docker exec -it rabbitmq /bin/bash
- 设置启动管理功能 **rabbitmq-plugins enable rabbitmq_management**
- rabbitmq 的默认 guest 用户不能支持用户远程访问/服务间调用也不行，需要自行创建，执行命令创建
- rabbitmqctl add_user mall mall 【创建用户】
- rabbitmqctl set_user_tags mall administrator  【授管理员(administrator)角色】
- rabbitmqctl add_vhost /mall  【创建 vhost】
- rabbitmqctl set_permissions -p /mall mall ".*" ".*" ".*"   【为用户 mall 授权访问 /mall】

- 如果使用 minio 则需要
- docker pull minio/minio
- 在Docker容器中运行MinIO，这里我们将MiniIO的数据和配置文件夹挂在到宿主机上
- docker run -p 9090:9000 --name minio \
  -v /mydata/minio/data:/data \
  -v /mydata/minio/config:/root/.minio \
  -d minio/minio server /data
## 服务器和数据库相关资料
### 服务器
- IP ${SERVER_IP}
- 用户名 ${SERVER_USERNAME}
- 登录密码 ${SERVER_PASSWORD}

### 数据库
IP ${MYSQL_HOST}
用户名 ${MYSQL_USERNAME}
密码 ${MYSQL_PASSWORD}
数据库名：mall

- 查看注册中心注册服务信息，访问地址：http://192.168.3.101:8848/nacos/

![](./document/resource/re_mall_swarm_run_01.png)

- 监控中心应用信息，访问地址：http://192.168.3.101:8101

![](./document/resource/re_mall_swarm_run_02.png)

![](./document/resource/re_mall_swarm_run_03.png)

- API文档信息，访问地址：http://192.168.3.101:8201

![](./document/resource/re_mall_swarm_run_04.png)

- 日志收集系统信息，访问地址：http://192.168.3.101:5601

![](./document/resource/re_mall_swarm_run_05.png)

- 使用Kubernetes部署后项目运行状态，访问地址：http://192.168.3.101:30880

![](document/resource/re_mall_swarm_run_06.png)

![](document/resource/re_mall_swarm_run_07.png)
