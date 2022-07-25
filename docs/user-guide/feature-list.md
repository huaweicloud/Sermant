# 当前功能列表

|功能名称|子功能|状态|组件支持列表|支持的配置中心|
|:-:|:-:|:-:|:-|:-:|
|[限流降级](flowcontrol/flowcontrol.md)|接口限流<br>应用降级<br>隔离仓|稳定|SpringBoot 1.2.x - 2.6.x & SpringWebMvc 4.1.3.RELEASE - 5.3.x<br>ApacheDubbo 2.7.3 - 3.0.x<br>AlibabaDubbo 2.1.x - 2.6.x|Kie<br>ZooKeeper|
|[流量录制回放](flowrecord/document.md)||实验|||
|[全链路压测](hercules/document.md)|-|实验|-|-
|[负载均衡](loadbalancer/document.md)|-|实验|-|-
|[影子库压测](online-stresstest/document.md)||实验|||
|[服务注册](registry/document.md)|服务注册|实验|注册中心支持：ServiceComb<br>框架支持：SpringBoot 1.5.x - 2.6.2 & SpringCloud Edgware.x - 2021.0.0或Dubbo 2.6.x-2.7.x|N/A|
|[服务双注册迁移](registry/spring-cloud-registry-migiration.md)|服务双注册迁移|实验|目标注册中心支持：ServiceComb<br>框架支持：SpringBoot 1.5.x - 2.6.2 & SpringCloud Edgware.x - 2021.0.0或Dubbo 2.6.x-2.7.x<br>SpringCloud原注册中心支持：Eureka、Nacos、Zookeeper、Consul<br>Dubbo原注册中心支持：Nacos、Zookeeper|N/A|
|[灰度发布](router/document.md)|-|实验|Dubbo 2.6.x-2.7.x|Kie|
|[服务监控](server-monitor/document.md)|数据库连接池监控|实验|AlibabaDruid 1.0.x, 1.1.x, 1.2.0 - 1.2.8|-|
|[线程变量插件](threadlocal/document.md)||实验|||
