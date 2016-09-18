# 畅旅小米秘书v3.0版系统环境搭建操作手册

-------

## 第一章 概要
> #### 编写目的：
> 帮助开发人员快速掌握开发环境与开发流程，理解本版系统架构的思想
> #### 注意事项：
> 仓库内提供的工程为骨架工程，包含可以直接部署运行的简单示例，可以基于项目骨架直接进行业务开发，开发依据现行约定开发规范标准进行，并不要求完全按照示例实现。

## 第二章 系统运行环境
> #### 服务运行环境要求：
> 1. 操作系统支持全平台，建议linux系列centos6.\*版本或ubuntu 14.\*服务器版（可虚拟机）
> 2. 硬件测试运行环境建议单机双核CPU及4G内存以上云环境。
> 3. 生产环境需要安装lvs和nginx作为一、二级web层负载均衡，测试开发环境不要求安装。

> #### 开发环境要求：
> 1. 操作系统全平台，推荐mac os、windows、ubuntu
> 2. 开发工具：
>  - **mac os**：idea，item2，vim，navicat或同类工具
>  - **windows**：idea，git bash， vim， navicat或同类工具
>  - **ubuntu**：idea， bash， vim， navicat或同类工具

## 第三章 开发软件环境安装清单和流程
> #### 相关软件清单
>  - **zookeeper**：分布式架构的注册中心和服务中心，必须
>  - **dubbo-admin**：dubbo服务管理控制台，可扩展，非必须
>  - **dubbo-monitor**：dobbo服务监控中心，可扩展，开发非必须
>  - **redis**：高速缓存容器，依据业务需求安装配置
>  - **mysql**：关系数据库，必须
>  - **atlas**：360开源mysql数据库中间件，开发非必须
>  - **rocketmq**：自建阿里开源消息队列，非必须
>  - **阿里云ons**：阿里云rocketmq服务，与自建rocketmq服务二者选一

> #### 安装配置流程
> - **zookeeper**
  > 1. **下载**：`wget http://mirrors.cnnic.cn/apache/zookeeper/zookeeper-3.4.9/zookeeper-3.4.9.tar.gz`
  > 2. **解压缩**：`tar zxvf zookeeper-3.4.9.tar.gz`
  > 3. **修改配置**：`cd zookeeper-3.4.9 && mv zoo_sample.cfg zoo.cfg && vim conf/zoo.cfg`
  > 4. **启动服务**：`./bin/zkServer.sh start`
  
> - **dubbo-admin** 
  > 1. **下载**：`git clone https://github.com/alibaba/dubbo.git`
  > 2. **打包**：`cd dubbo && mvn clean package -Dmaven.test.skip`
  > 3. **配置部署**：`wget https://mirrors.tuna.tsinghua.edu.cn/apache/tomcat/tomcat-7/v7.0.70/bin/apache-tomcat-7.0.70.tar.gz`
  `tar zxvf apache-tomcat-7.0.70.tar.gz`
  `cd apache-tomcat-7.0.70`
  `rm -rf webapps/ROOT`
  `unzip dubbo/dubbo-admin/target/dubbo-admin-2.5.4-SNAPSHOT.war -d ~/apache-tomcat-7.0.70/webapps/ROOT`
  `vim ~/apache-tomcat-7.0.70/webapps/ROOT/WEB-INF/dubbo.properties`
  `dubbo.registry.address=zookeeper://127.0.0.1:2181`
  > 4. **启动服务**：`cd ~/apache-tomcat-7.0.70/ && bin/startup.sh`
  
## 第四章 基础工程骨架搭建

