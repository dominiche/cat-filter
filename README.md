# cat-filter
dianping-cat的filter集成，集成filter有：springmvc、mybatis、dubbo
   
使用方法：
引入依赖：
```xml
<dependency>
    <groupId>dominic</groupId>
    <artifactId>cat-filter</artifactId>
    <version>${cat-filter_version}</version>
</dependency>
```
对于SpringBoot项目来说，因为有自动配置，所以只需引入依赖，不需其他额外配置；
普通spring项目的话，还需要在注解扫描或在xml中扫描对应配置路径："com.dominic.cat.filter.config"
