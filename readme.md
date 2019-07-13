# Readme

## ik中文分词

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