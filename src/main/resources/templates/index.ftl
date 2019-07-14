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
            background: url("https://i.loli.net/2019/07/13/5d29ba343e0f813835.jpg");
            background-size: cover;
            background-attachment: fixed;
            background-position: center;
            background-repeat: no-repeat;
            width: 100%;
            height: 100%;
            overflow: hidden;
        }

        .container {
            background: rgba(255, 255, 255, .5);
            margin: 16px auto;
            position: absolute;
            left: 16px;
            right: 16px;
            top: 8px;
            bottom: 8px;
        }
        .logo{
            position: absolute;
            width: 320px;
            left: calc(50% - 160px);
            top: calc(50% - 200px);
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
    <div class="row">
        <img src="https://i.loli.net/2019/07/04/5d1dd5dcc376268830.png" class="logo" />
    </div>
    <div class="row">
        <form onsubmit="submitFn(this, event);" action="/s">
            <div class="search-wrapper active">
                <div class="input-holder">
                    <input type="text" name="wd" class="search-input" placeholder="Type to search"/>
                    <button class="search-icon" onclick="searchToggle(this, event);"><span></span></button>
                </div>
                <span class="close" onclick="searchToggle(this, event);"></span>
                <div class="result-container">

                </div>
            </div>
        </form>
    </div>
</div>
</body>
</html>