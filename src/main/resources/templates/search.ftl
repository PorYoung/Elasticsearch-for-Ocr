<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://cdn.bootcss.com/bootstrap/4.0.0/css/bootstrap.min.css"
          integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">

    <link rel="stylesheet" type="text/css" href="/css/search-form.css">
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
        body {
            background: url("https://i.loli.net/2019/07/13/5d29ba343e0f813835.jpg") no-repeat;
            background-size: cover;
            background-attachment: fixed;
            background-position: center;
            width: 100%;
            height: 100%;
        }

        .container {
            background: rgba(255, 255, 255, .5);
            min-height: 100%;
            margin: 16px auto;
        }

        .container .row{
            margin: 8px 0;
        }

        .search-wrapper {
            position: initial;
            margin: 16px;
            transform: initial;
            top: initial;
            left: initial;
        }

        .search-wrapper.active .input-holder {
            width: 100%;
        }

        .close {
            position: initial;
            transform: initial;
        }

        .header {
            padding: 16px;
        }

        .card {
            background: rgba(255, 255, 255, .9);
        }

        .card img {
            width: 300px;
            height: 300px;
            margin: 16px;
        }

        .card-body {
            display: flex;
            justify-content: space-around;
        }

        em {
            font-weight: bold;
            background: rgba(220, 200, 160, .5);
        }
    </style>
    <script>
        function searchToggle(obj, evt) {
            var container = $(obj).closest('.search-wrapper');

            if (!container.hasClass('active')) {
                container.addClass('active');
                evt.preventDefault();
            } else if (container.hasClass('active') && $(obj).closest('.input-holder').length == 0) {
                container.removeClass('active');
                // clear input
                container.find('.search-input').val('');
                // clear and hide result container when we press close
                container.find('.result-container').fadeOut(100, function () {
                    $(this).empty();
                });
            }
        }

        function submitFn(obj, evt) {
            value = $(obj).find('.search-input').val().trim();

            _html = "Please Wait for searching...";

            $(obj).find('.result-container').html('<span>' + _html + '</span>');
            $(obj).find('.result-container').fadeIn(100);
        }
    </script>
</head>
<body>
<div class="container">
    <div class="header">
        <div class="row justify-content-md-center">
            <form onsubmit="submitFn(this, event);" action="/s" class="col-lg-6">
                <div class="search-wrapper active">
                    <div class="input-holder">
                        <input type="text" name="wd" value="${wd}" class="search-input" placeholder="Type to search"/>
                        <button class="search-icon" onclick="searchToggle(this, event);"><span></span></button>
                    </div>
                    <#--<span class="close" onclick="searchToggle(this, event);"></span>-->
                    <div class="result-container">

                    </div>
                </div>
            </form>
        </div>
        <div class="row justify-content-md-center info">
            <div class="col-sm-4">
                <div class="alert alert-success" role="alert">
                    共找到相关结果${page.total}个，耗时${page.took}ms
                </div>
            </div>
        </div>
    </div>
    <#list page.list as ocr>
        <#if ocr?index % 2 == 0>
            <div class="row">
            <div class="col">
                <div class="card" style="width: 100%;">
                    <a href="/detail/${ocr.id}?wd=${wd}"><img src="/ocrs/${ocr.id}/${ocr.id}.jpg" class="card-img-top"
                                                     alt="${ocr.id}"/></a>
                    <div class="card-body">
                        <h5 class="card-title">${ocr.id}</h5>
                        <#if ocr.hlOcrText??>
                            <#list ocr.hlOcrText as hlOcrText>
                                <#if hlOcrText?index < 3>
                                    <p class="card-text">${hlOcrText}</p>
                                </#if>
                            </#list>
                        <#else >
                        <#--<p class="card-text">${ocr.ocrText?left_pad(35,"...")?right_pad(50,"...")?substring(0,50)}</p>-->
                            <p class="card-text">${ocr.ocrText}</p>
                        </#if>
                    </div>
                    <ul class="list-group list-group-flush">
                        <#if ocr.hlTextResult??>
                            <#list ocr.hlTextResult as textResult>
                                <#if textResult?index < 3>
                                    <li class="list-group-item">${textResult}</li>
                                </#if>
                            </#list>
                        <#else >
                            <#list ocr.textResult as textResult>
                                <#if textResult?index < 3>
                                    <li class="list-group-item">${textResult.text}</li>
                                </#if>
                            </#list>
                        </#if>
                    </ul>
                    <div class="card-body">
                        <#if ocr.pdfUrl??>
                            <a href="${ocr.pdfUrl}" class="card-link">PDF link</a>
                        </#if>
                        <a href="/detail/${ocr.id}?wd=${wd}">Detail</a>
                    </div>
                </div>
            </div>
        <#else >
            <div class="col">
                <div class="card" style="width: 100%;">
                    <a href="/detail/${ocr.id}?wd=${wd}"><img src="/ocrs/${ocr.id}/${ocr.id}.jpg" class="card-img-top"
                                                     alt="${ocr.id}"></a>
                    <div class="card-body">
                        <h5 class="card-title">${ocr.id}</h5>
                        <#if ocr.hlOcrText??>
                            <#list ocr.hlOcrText as hlOcrText>
                                <#if hlOcrText?index < 3>
                                    <p class="card-text">${hlOcrText}</p>
                                </#if>
                            </#list>
                        <#else >
                            <#--<p class="card-text">${ocr.ocrText?left_pad(35,"...")?right_pad(50,"...")?substring(0,50)}</p>-->
                            <p class="card-text">${ocr.ocrText}</p>
                        </#if>
                    </div>
                    <ul class="list-group list-group-flush">
                        <#if ocr.hlTextResult??>
                            <#list ocr.hlTextResult as textResult>
                                <#if textResult?index < 3>
                                    <li class="list-group-item">${textResult}</li>
                                </#if>
                            </#list>
                        <#else >
                            <#list ocr.textResult as textResult>
                                <#if textResult?index < 3>
                                    <li class="list-group-item">${textResult.text}</li>
                                </#if>
                            </#list>
                        </#if>
                    </ul>
                    <div class="card-body">
                        <#if ocr.pdfUrl??>
                            <a href="${ocr.pdfUrl}" class="card-link">PDF link</a>
                        </#if>
                        <a href="/detail/${ocr.id}?wd=${wd}">Detail</a>
                    </div>
                </div>
            </div>
            </div>
        </#if>
    </#list>

    <nav class="row justify-content-md-center" aria-label="Page navigation">
        <ul class="pagination pagination-lg justify-content-center col-lg-4">
            <li class="page-item<#if !page.hasPrevious()> disabled</#if> col-md-4">
                <a href=<#if page.hasPrevious()>"/s?wd=${wd}&pn=${page.pageNo - 1}" <#else>#</#if>><span
                            aria-hidden="true">&larr;</span>
                    上一页</a>
            </li>
            <li class="page-item<#if !page.hasNext()> disabled</#if> col-md-4">
                <a href=<#if page.hasNext()>"/s?wd=${wd}&pn=${page.pageNo + 1}" <#else>#</#if>>下一页 <span
                            aria-hidden="true">&rarr;</span></a>
            </li>
        </ul>
    </nav>
</div>
</body>
</html>