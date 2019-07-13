<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/4.0.0/css/bootstrap.min.css"
          integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">

    <!-- Optional JavaScript -->
    <!-- jQuery first, then Popper.js, then Bootstrap JS -->
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.slim.min.js"
            integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
            crossorigin="anonymous"></script>
    <script src="https://cdn.bootcss.com/popper.js/1.12.9/umd/popper.min.js"
            integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
            crossorigin="anonymous"></script>
    <script src="https://cdn.bootcss.com/bootstrap/4.0.0/js/bootstrap.min.js"
            integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
            crossorigin="anonymous"></script>

    <title>OCRPdf-ElasticSearch</title>
    <style>
        .card img {
            width: 300px;
            height: 300px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="row justify-content-md-center info">
        <div class="col-lg-4">
            <div class="alert alert-success" role="alert">
                共找到相关结果${page.total}个，耗时${page.took}ms
            </div>
        </div>
    </div>
    <#list page.list as ocr>
        <#if ocr?index % 2 == 0>
            <div class="row">
            <div class="col">
                <div class="card" style="width: 100%;">
                    <img src="/ocrs/${ocr.id}/${ocr.id}.jpg" class="card-img-top" alt="${ocr.id}">
                    <div class="card-body">
                        <h5 class="card-title">${ocr.id}</h5>
                        <p class="card-text">${ocr.ocrText}</p>
                    </div>
                    <ul class="list-group list-group-flush">
                        <#list ocr.textResult as textResult>
                            <#if textResult?index < 3>
                                <li class="list-group-item">${textResult.text}</li>
                            </#if>
                        </#list>
                    </ul>
                    <div class="card-body">
                        <#if ocr.pdfUrl??>
                            <a href="${ocr.pdfUrl}" class="card-link">PDF link</a>
                        </#if>
                    </div>
                </div>
            </div>
        <#else >
            <div class="col">
                <div class="card" style="width: 100%;">
                    <img src="/ocrs/${ocr.id}/${ocr.id}.jpg" class="card-img-top" alt="${ocr.id}">
                    <div class="card-body">
                        <h5 class="card-title">${ocr.id}</h5>
                        <p class="card-text">${ocr.ocrText}</p>
                    </div>
                    <ul class="list-group list-group-flush">
                        <#list ocr.textResult as textResult>
                            <#if textResult?index < 3>
                                <li class="list-group-item">${textResult.text}</li>
                            </#if>
                        </#list>
                    </ul>
                    <div class="card-body">
                        <#if ocr.pdfUrl??>
                            <a href="${ocr.pdfUrl}" class="card-link">PDF link</a>
                        </#if>
                    </div>
                </div>
            </div>
            </div>
        </#if>
    </#list>

    <nav class="row justify-content-md-center" aria-label="Page navigation">
        <ul class="pagination pagination-lg justify-content-center col-lg-4">
            <li class="page-item<#if !page.hasPrevious()> disabled</#if> col-md-4">
                <a href=<#if page.hasPrevious()>"/listAll?pn=${page.pageNo - 1}" <#else>#</#if>><span
                            aria-hidden="true">&larr;</span>
                    上一页</a>
            </li>
            <li class="page-item<#if !page.hasNext()> disabled</#if> col-md-4">
                <a href=<#if page.hasNext()>"/listAll?pn=${page.pageNo + 1}" <#else>#</#if>>下一页 <span
                            aria-hidden="true">&rarr;</span></a>
            </li>
        </ul>
    </nav>
</div>
</body>
</html>