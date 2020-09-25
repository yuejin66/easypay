<html>
<head>
    <meta charset="utf-8">
    <title>WxPay</title>
</head>

<body>

<div id="myQrcode"></div>
<div id="orderId" hidden>${orderId}</div>
<#--要跳转的地址-->
<div id="returnUrl" hidden>${returnUrl}</div>

<script src="https://cdn.bootcdn.net/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="https://cdn.bootcdn.net/ajax/libs/jquery.qrcode/1.0/jquery.qrcode.min.js"></script>
<script>
    jQuery('#myQrcode').qrcode({
        text	: "${codeUrl}"
    });

    $(function () {
        //轮询定时器
        setInterval(function () {
            console.log("开始查询支付状态...")
            //ajax请求
            $.ajax({
                url: '/pay/queryByOrderId',
                data: {
                    'orderId': $('#orderId').text()
                },
                success: function (result) {
                    console.log(result)
                    if (result.platformStatus != null &&
                            result.platformStatus === 'SUCCESS'){
                        //可以在后面拼接订单号等业务
                        location.href = $('#returnUrl').text()
                    }
                },
                error: function (result) {
                    alert(result)
                }
            })
        },2000)
    })
</script>

</body>
</html>