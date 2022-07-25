# 灰度路由

[插件目录](../../../sermant-plugins/sermant-router)

## 定位:

为保障新版本平稳上线，可以先选择部分用户试用新版本，待新版本没有问题之后，再逐步扩大范围，把所有用户都迁移到新版本上面来。灰度发布可以保证整体系统的稳定，在初始灰度的时候就可以发现、调整问题，以减小其影响度 。

## 功能:

使用灰度发布可以实现版本的零中断升级。例如，用户准备将生产的应用版本由V1升级到V2，用户首先配置一个灰度规则，将30%的流量请求到V2，然后部署灰度版本，即V2，通过插件选择30%流量到V2版本，待V2版本完全测试无问题后，将全部流量切换到V2，并停止V1版本服务。

## 使用方式:

目前插件端的实例列表基于servicecomb，所以宿主应用需要引入如下依赖：

### Spring Cloud

```xml

<dependency>
    <groupId>com.huaweicloud</groupId>
    <artifactId>spring-cloud-starter-huawei-servicecomb-discovery</artifactId>
    <version>${version}</version>
</dependency>
```

- 具体使用方法可参考[Spring Cloud Huawei](https://github.com/huaweicloud/spring-cloud-huawei)

### Dubbo

```xml

<dependency>
    <groupId>com.huaweicloud.dubbo-servicecomb</groupId>
    <artifactId>dubbo-servicecomb-service-center</artifactId>
    <version>${version}</version>
</dependency>
```

- 具体使用方法可参考[Dubbo-Serivcecomb](https://github.com/huaweicloud/dubbo-servicecomb)

然后需要到配置中心配置如下灰度规则：

### 配置中心新增配置方式如下：

**URL**

POST /publishConfig

**请求Body**

|参数|是否必填|参数类型|描述
|---|---|---|---|
|key|是|String|配置的key|
|group|是|String|配置的组|
|content|是|String|配置文本|

其中，group的格式为：**key=value**，key、value为自定义的值

### 灰度发布规则示例及说明如下：

```yaml
servicecomb:
  routeRule:
    dubbo-b: |# 服务名
      - precedence: 1 # 优先级，优先级数量越大优先级越高
        match: # 匹配策略
          source: dubbo-a # 匹配服务名，非必填
          path: com.huawei.dubbotest.service.BTest.testObject # dubbo接口全路径/或者url路径
          isFullMatch: false # 参数是否全匹配，默认false
          args: # dubbo参数匹配
            args0: # dubbo接口的第0个参数
              type: .id # 取值类型，dubbo应用特有字段，第0个参数为实体，获取其id的属性值，如果参数类型为int，String等普通类型，则无需填写该值，所有的取值类型见取值类型列表
              exact: '2' # 配置策略，等于2，所有的匹配策略见配置策略列表
              caseInsensitive: false # 是否区分大小写，默认为false，区分大小写
          headers: # spring header匹配
              id: # header中的id
                exact: 1 # 配置策略，等于1
                caseInsensitive: false # 是否区分大小写，默认为false，区分大小写
              name: # header中的name
                exact: test # test
        route:
          - tags:
              version: 1.0.2 # 匹配的版本，即dubbo.servicecomb.service.version或spring.cloud.servicecomb.discovery.version配置的版本号
            weight: 100 # 权重值，如果低于100，则有可能会转发流量到其它版本
```

按需修改灰度发布插件的[配置文件](../../../sermant-plugins/sermant-router/config/config.yaml)以读取配置中心的配置：

```yaml
gray.plugin:
  dubboKey: gray #dubbo灰度规则配置的key，可自定义
  dubboGroup: public=default #dubbo灰度规则配置的组，public、default可自定义
```

对于以上示例配置，新增配置接口请求的参数中，key为gray，group为public=default，content为灰度规则。

### 配置策略列表

|策略名|策略值|匹配规则|
|---|---|---|
|精确匹配|exact|参数值等于配置值|
|正则|regex|参数值匹配正则表达式|
|不等于|noEqu|参数值不等于配置值|
|大于等于|noLess|参数值大于等于配置值|
|小于等于|noGreater|参数值小于等于配置值|
|大于|greater|参数值大于配置值|
|小于|less|参数值小于配置值|

### 取值类型列表

|类型|取值方式|适用参数类型|
|---|---|---|
|留空|表示直接取当前参数的值|适用普通参数类型，例如String、int、long等|
|.name|表示取参数的name属性，相当于ARG0.getName()|适用于对象类型|
|.isEnabled()|表示取参数的enabled属性，相当于ARG0.isEnabled()|适用于对象类型|
|[0]|取数组的第一个值，相当于ARG0[0]|适用于普通类型的数组，例如String[]、int[]|
|.get(0)|取List的第一个值，相当于ARG0.get(0)|适用于普通类型的列表，例如List\<String>、List\<Integer>|
|.get("key")|获取key对应的值，相当于ARG0.get("key")|适用于普通类型的map，例如Map<String, String>|

## 结果验证

- 前提条件[正确打包Sermant](../../README.md)

- 注册中心使用华为CSE，下载[Local-CSE](https://support.huaweicloud.com/devg-cse/cse_devg_0036.html) ，解压后按照文档说明进行启动

- 编译[demo应用](../../../sermant-plugins/sermant-router/router-demo/dubbo-router-demo)

```shell
mvn clean package
```

- 启动消费者

```shell
java -javaagent:${path}\sermant-agent-x.x.x\agent\sermant-agent.jar=appName=dubbo-a,instanceName=dubboA -jar dubbo-a.jar
```

其中path需要替换为Sermant实际打包路径

- 启动生产者

```shell
java -jar dubbo-b.jar
```

- 启动灰度生产者

```shell
java -jar dubbo-b2.jar
```

- 测试

当启动以上3个应用并正确配置灰度规则后，通过浏览器访问<http://localhost:28020/object?id=2>，即可灰度到1.0.2版本的dubbo-b2应用。

[返回**Sermant**说明文档](../../README.md)
