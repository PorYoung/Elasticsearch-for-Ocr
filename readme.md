# Readme

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

## 记录

### ik中文分词

```
PUT /ocr?pretty
{
    "settings": {
        "analysis": {
            "analyzer": {
                "ik": {
                    "tokenizer": "ik_smart"
                }
            }
        }
    },
    "mappings": {
        "doc": {
            "dynamic": true,
            "properties": {
                "textResult.text": {
                    "type": "text",
                    "analyzer": "ik_smart",
                    "search_analyzer": "ik_smart"
                },
                "ocrText": {
                  "type": "text",
                  "analyzer": "ik_smart",
                  "search_analyzer": "ik_smart"
                }
            }
        }
    }
}
```

### 搜索

```$xslt
模糊搜索
GET ocr/doc/_search
{
  "query": {
    "bool": {
      "should": [
        {
          "match": {
            "ocrText": "${queryString}"
          }
        },
        {
          "nested": {
            "path": "textResult",
            "query": {
              "match": {
                "textResult.text": "${queryString}"
              }
            }
          }
        }
      ]
    }
  },
  "_source": ["ocrText","pdfUrl","id"], 
  "highlight": {
    "fields": {
      "textResult.text": {},
      "ocrText": {}
    }
  }
}

详细信息
GET ocr/doc/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "id": "${id}"
          }
        },
        {
          "nested": {
            "path": "textResult",
            "query": {
              "match": {
                "textResult.text": "${wd}"
              }
            },
            "inner_hits":{
              "_source":["textResult"]
            }
          }
        }
      ]
    }
  },
  "highlight": {
    "fields": {
      "textResult.text": {}
    }
  }
}
```