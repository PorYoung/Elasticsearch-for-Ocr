# Readme

[趟坑记录](https://blog.poryoung.cn/?p=1052)

## 启动

- elasticsearch `6.2.2`
- kibana `6.2.2` （管理工具，可以不装）
- 中文分词 [elasticsearch-analysis-ik](https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v5.5.1/elasticsearch-analysis-ik-6.2.2.zip) `6.2.2`
- [清华开放词库](http://thuocl.thunlp.org/)

### 中文分词设置

在`elasticsearch`安装目录`plguins`下新建`ik`文件夹，解压elasticsearch-analysis-ik到`ik`文件夹

进入`config`目录，将自定义词典放在该目录下，命名为`userdict.txt`

修改`IKAnalyzer.cfg.xml`自定义词典的路径

```$xslt
<entry key="ext_dict">userdict.txt</entry>
```

启动`elasticsearch`

### 启动项目

maven依赖安装好，导包完成后修改`application.yml`配置文件

```$xslt
spring:
  elasticsearch:
    jest:
      uris: http://localhost:9200
#      read-timeout: 10000
#      username: user
#      password: secret
elastic-index-config:
#这里修改elasticesearch的索引和类型，类似于数据库名和表名
  INDEX: ocr
  TYPE: doc
#  set "initial" to init data
# 这里设置成initial会初始化全部程序，包括elasticesearch索引，第一次运行请初始化
  INIT: default
# 这里设置ocr文件夹路径
ocr-file:
  OCRPATH: "E:\\workfiles\\WEB\\springboot\\elasticsearch-ocr\\src\\main\\resources\\static\\ocrs"
server:
  port: 8080
```

其中ocr文件夹结构为

```$xslt
- ocr
-- 文件id
--- 文件id.json
--- 文件id.jpg
```

### 读取文件

访问`localhost:8080/reader/1234`读取指定目录下的文件，等待执行结果

读取文件完成后，修改`application.yml`配置文件的`INIT`为`default`，否则每次启动都会初始化

### 操作

首页：`localhost:8080/`

搜索：`localhost:8080/s?wd=关键词&pn=页数(>=1)`

详情页面：`localhost:8080/detail?wd=关键词``

![预览](https://i.loli.net/2019/07/14/5d2a95418c84a43268.gif)
