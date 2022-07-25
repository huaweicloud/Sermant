# Sermant 开发和使用介绍

Sermant 基于Java的字节码增强技术，通过 JavaAgent 对宿主应用进行非侵入式增强，以解决Java应用的微服务治理问题。Sermant的初衷是建立一个面向微服务治理的对开发态无侵入的解决方案生态，降低服务治理开发和使用的难度，通过抽象接口、功能整合、插件隔离等手段，达到简化开发、功能即插即用的效果。

本文档对如何开发和使用 Sermant 做详细介绍。

## 运行环境

- [HuaweiJDK 1.8](https://gitee.com/openeuler/bishengjdk-8) / [OpenJDK 1.8](https://github.com/openjdk/jdk) / [OracleJDK 1.8](https://www.oracle.com/java/technologies/downloads/)
- [Apache Maven 3](https://maven.apache.org/download.cgi)

## 模块说明

**Sermant**包含以下模块：

- [sermant-agentcore](../sermant-agentcore): *JavaAgent*相关内容
  - [sermant-agentcore-core](../sermant-agentcore/sermant-agentcore-core): 核心功能模块
  - [sermant-agentcore-premain](../sermant-agentcore/sermant-agentcore-premain): *JavaAgent*入口模块
  - [sermant-agentcore-config](../sermant-agentcore/sermant-agentcore-config): 配置模块
- [sermant-backend](../sermant-backend): 消息发送模块服务端
- [sermant-package](../sermant-package): 打包模块
- [sermant-plugins](../sermant-plugins): 插件根模块，内含各种功能的插件及相关附加件

## 打包流程

**Sermant**的打包流程大致分为以下步骤：

- *agent*: 编译、打包核心功能和插件
- *example*: 编译、打包核心功能和示例模块(默认不开启)
- *backend*: 编译、打包**Sermant**后端模块
- *ext*: 编译、打包插件附带的后端、前端和其他附加件
- *package*: 将以上的打包结果归档为产品包
- *release*: 发布构建产物到中央仓库
- *all*: 执行以上全部步骤(默认不开启)

执行以下*maven*命令，对**Sermant**工程进行默认打包：

```shell
mvn clean package -Dmaven.test.skip
```

命令执行完毕后，工程目录下将生成一个形如`sermant-agent-x.x.x`的文件夹和形如`sermant-agent-x.x.x.tar`的压缩文件，后者为**Sermant**的产品包，前者则为产品包解压后的内容。

## 产品目录说明

- *agent*: JavaAgent相关内容
  - *config*: 配置文件目录
    - *bootstrap.properties*: 启动配置
    - *config.properties*: 核心功能配置
    - *plugins.yaml*: 插件配置，配置着需要被加载的插件功能
  - *core/sermant-agentcore-core-x.x.x.jar*: **Sermant**的核心功能包
  - *pluginPackage*: 插件包目录，插件按功能名称分类
    - *xxx*: 任意插件功能
      - *config/config.yaml*: 插件配置文件
      - *plugin*: 插件包目录
      - *service*: 插件服务包目录
  - *sermant-agent.jar*: JavaAgent入口包
- *server*: 服务器目录，含**Sermant**的服务端，插件的服务端和客户端

## 插件开发

如何新增一个插件可以参考[插件模块开发手册](dev-guide/dev_plugin_module.md)，其中涉及添加插件、插件服务及附加件的详细流程。

如何编写一个插件的内容可以参考[插件代码开发手册](dev-guide/dev_plugin_code.md)，其中涉及大部分开发插件过程中可能遇到场景。

## 相关文档

|文档名称|文档类型|
|---|---|
|[第三方版权说明手册](dev-guide/third_party_copyright.md)|开发手册|
|[版本管理手册](dev-guide/version_manage.md)|开发手册|
|[插件模块开发手册](dev-guide/dev_plugin_module.md)|开发手册|
|[插件代码开发手册](dev-guide/dev_plugin_code.md)|开发手册|
|[动态配置服务介绍](dev-guide/service_dynamicconfig.md)|开发手册|
|[心跳服务介绍](dev-guide/service_heartbeat.md)|开发手册|
|[网关服务介绍](dev-guide/service_send.md)|开发手册|
|[核心模块介绍](user-guide/agentcore.md)|使用手册|
|[入口模块介绍](user-guide/entrance.md)|使用手册|
|[后端模块介绍](user-guide/backend.md)|使用手册|
|[限流降级功能介绍](user-guide/flowcontrol/flowcontrol.md)|使用手册|
|[动态配置功能介绍](user-guide/dynamic-config/document.md)|使用手册|
|[流量录制回放功能介绍](user-guide/flowrecord/document.md)|使用手册|
|[全链路压测功能介绍](user-guide/hercules/document.md)|使用手册|
|[影子库压测功能介绍](user-guide/online-stresstest/document.md)|使用手册|
|[服务注册功能介绍](user-guide/registry/document.md)|使用手册|
|[灰度发布功能介绍](user-guide/router/document.md)|使用手册|
|[服务监控功能介绍](user-guide/server-monitor/document.md)|使用手册|
|[线程变量插件功能介绍](user-guide/threadlocal/document.md)|使用手册|
|[当前插件功能汇总列表](user-guide/feature-list.md)|使用手册|
|[常见问题说明指导](./FAQ.md)|使用手册|