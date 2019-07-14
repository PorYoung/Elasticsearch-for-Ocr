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
            /*margin: 16px auto;*/
        }

        .container {
            max-width: initial;
            width: 100%;
        }

        .left {
            padding-top: 8px;
        }

        .ocrImg {
            width: 100%;
        }

        #ocrCanvas {
            position: fixed;
            top: 5px;
        }

        .accordion .card{
            background: rgba(255,255,255,.7);
        }
        .accordion .list-group-item{
            background: none;
        }

        em {
            font-weight: bold;
            background: rgba(220, 200, 160, .5);
        }

        @media (max-width: 992px) {
            #ocrCanvas {
                position: initial;
            }
        }
    </style>
    <script>
        var ctx = null;
        var csv = null;
        var imgRatio = 0;
        var img = new Image();

        function loadImage(image, loaded, error) {
            // 创建 image 对象加载图片；

            // 当为线上图片时，需要设置 crossOrigin 属性；
            if (image.indexOf('http') == 0) img.crossOrigin = '*';
            img.onload = function () {
                loaded(img);

                // 使用完后清空该对象，释放内存；
                // setTimeout(function () {
                //     img = null;
                // }, 1000);
            };
            img.onerror = function () {
                error('img load error');
            };
            img.src = image;
        }

        function loaded(img) {
            imgRatio = img.naturalHeight / img.naturalWidth;
            console.log(img.width);
            cvs = $("#ocrCanvas")[0];
            console.log(cvs);
            console.log(img.naturalWidth);
            // 获取容器中的画板；
            ctx = cvs.getContext('2d');
            // cvs.height = Math.round($(".right").height() - 10);
            // cvs.width = cvs.height / imgRatio;
            cvs.height = document.body.clientHeight - 10;
            cvs.width = cvs.height / imgRatio;
            ctx.drawImage(img, 0, 0, cvs.width, cvs.height);
        }

        $(function () {
            loadImage($(".ocrImg")[0].src, loaded);

            var ocrText = $(".ocrText");
            var ocrhtml = ocrText.html();
            // handle highlight
            $(".highlightTextResult").each(function (item) {
                text = $(this).html();
                trimText = text.replace(/\<em\>(.*?)\<\/em\>/g, "$1");
                ocrhtml = ocrhtml.replace(new RegExp(trimText, "g"), text);
            });
            ocrText.html(ocrhtml);
        });


        function showPosition(obj, e) {
            if (ctx == null) {
                return alert("图片正在加载中，请稍后再试");
            }
            var par = $(obj).parent().parent(".list-group");
            var lbCoord = par.find(".lbCoord span").html();
            var ltCoord = par.find(".ltCoord span").html();
            var rtCoord = par.find(".rtCoord span").html();
            var rbCoord = par.find(".rbCoord span").html();
            lbCoord = lbCoord.split(",");
            lbCoord = toCanvasCoord(lbCoord);
            ltCoord = ltCoord.split(",");
            ltCoord = toCanvasCoord(ltCoord);
            rtCoord = rtCoord.split(",");
            rtCoord = toCanvasCoord(rtCoord);
            rbCoord = rbCoord.split(",");
            rbCoord = toCanvasCoord(rbCoord);
            // ctx.drawImage(img, 0, 0, cvs.width, cvs.height);
            ctx.beginPath();
            ctx.moveTo(ltCoord[0], ltCoord[1]);
            ctx.lineTo(rtCoord[0], rtCoord[1]);
            ctx.lineTo(rbCoord[0], rbCoord[1]);
            ctx.lineTo(lbCoord[0], lbCoord[1]);
            ctx.closePath();
            ctx.lineWidth = 3;
            ctx.strokeStyle = "red";
            ctx.stroke();
            // ctx.rect(188, 50, 200, 100);
            // ctx.fillStyle = 'yellow';
            // ctx.fill();
        }

        function toCanvasCoord(arr) {
            var numArr = [];
            ratioX = cvs.width / img.naturalWidth;
            ratioY = cvs.height / img.naturalHeight;
            numArr[0] = parseFloat(arr[0]) * ratioX;
            numArr[1] = parseFloat(arr[1]) * ratioY;
            return numArr;
        }
    </script>
</head>
<div class="container">
    <#if ocr??>
        <div class="row wrapper">
            <div class="col-lg-3 left">
                <button type="button" class="btn btn-primary">
                    <span class="badge badge-light">${ocr.id}</span>
                    <span class="sr-only"></span>
                </button>
                <div id="accordion">
                    <div class="card">
                        <div class="card-header" id="headingOne">
                            <h5 class="mb-0">
                                <button class="btn btn-link" data-toggle="collapse" data-target="#collapseOne"
                                        aria-expanded="true" aria-controls="collapseOne">
                                    OCR Text #
                                </button>
                            </h5>
                        </div>

                        <div id="collapseOne" class="collapse show" aria-labelledby="headingOne"
                             data-parent="#accordion">
                            <div class="card-body ocrText">
                                ${ocr.ocrText}
                            </div>
                        </div>
                    </div>
                    <div class="card">
                        <div class="card-header" id="headingTwo">
                            <h5 class="mb-0">
                                <button class="btn btn-link collapsed" data-toggle="collapse" data-target="#collapseTwo"
                                        aria-expanded="true" aria-controls="collapseTwo">
                                    Matched Text #
                                </button>
                            </h5>
                        </div>
                        <div id="collapseTwo" class="collapse.show" aria-labelledby="headingTwo" data-parent="#accordion">
                            <div class="card-body">
                                <ul class="list-group">
                                    <#if ocr.hlTextResult??>
                                        <#list ocr.hlTextResult as textResult>
                                            <li class="list-group-item highlightTextResult"
                                                style="display: none;">${textResult}</li>
                                        </#list>
                                    </#if>
                                    <#list ocr.textResult as textResult>
                                        <li class="list-group-item">
                                            <ul class="list-group">
                                                <li class="list-group-item">
                                                    <button type="button" onclick="showPosition(this,event)"
                                                            class="btn btn-lg btn-primary">
                                                        Click To Show Position
                                                    </button>
                                                </li>
                                                <li class="list-group-item">${textResult.text}</li>
                                                <li class="list-group-item">
                                                    handwritten：<span>${textResult.isHandwritten()?string("是","否")}</span>
                                                </li>
                                                <li class="list-group-item lbCoord">
                                                    leftBottom:<span>${textResult.leftBottom}</span></li>
                                                <li class="list-group-item ltCoord">
                                                    leftTop:<span>${textResult.leftTop}</span></li>
                                                <li class="list-group-item rbCoord">
                                                    rightBottom:<span>${textResult.rightBottom}</span></li>
                                                <li class="list-group-item rtCoord">
                                                    rightTop:<span>${textResult.rightTop}</span></li>
                                            </ul>
                                        </li>
                                    </#list>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-7 right">
                <img src="/ocrs/${ocr.id}/${ocr.id}.jpg" class="ocrImg" style="display: none;"/>
                <canvas id="ocrCanvas">您的浏览器不支持canvas</canvas>
            </div>
        </div>
    </#if>
</div>
</html>