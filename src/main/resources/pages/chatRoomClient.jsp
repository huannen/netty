<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>netty聊天室</title>
    <style>
        .webchat {
            position: absolute;
            top: 15%;
            left: 35%;
            text-align: center;
        }

        #content {
            width: 345px;
            height: 345px;
            border-left: 1px solid #AAAAAA;
            border-right: 1px solid #AAAAAA;
            border-top: 2px solid #AAAAAA;
            border-bottom: 2px solid #AAAAAA;
            border-radius: 2px;
            margin: 9px 0;
            overflow: auto;
        }

        .button {
            background: #60F49B;
            color: #ffffff;
            border: none;
            height: 30px;
        }

        #msg {
            height: 25px;
        }
    </style>

    <!-- jquery -->
    <script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
</head>
<body>
<div class="webchat">
    <div class="title">Netty4+Websocket聊天室</div>
    <div id="content"></div>
    <input type="text" id="msg" /> <input class="button" type="button"
                                          value="发送消息" onclick="CHAT.chat()" /> <input class="button"
                                                                                       type="button" value="清空聊天记录" onclick="CHAT.clean()" />
</div>
</body>

<script type="text/javascript">
    window.CHAT = {
        socket : null,
        init : function() {
            if (window.WebSocket) {
                // ws://机器地址:netty绑定的端口/服务端定义socket路径
                CHAT.socket = new WebSocket("ws://192.168.1.126:1234/ws");

                CHAT.socket.onopen = function() {
                    console.log("连接成功");
                }, CHAT.socket.onclose = function() {
                    console.log("连接关闭");
                }, CHAT.socket.onerror = function() {
                    console.log("异常");
                }, CHAT.socket.onmessage = function(e) {
                    var htm = $("#content").html();
                    $("#content").html(htm + "<br>" + e.data)
                }

            } else {
                alert("浏览器不支持websocket协议.....");
            }
        },
        chat : function() {
            if ($("#msg").val() != "")
                CHAT.socket.send($("#msg").val());
        },
        clean : function() {
            $("#content").html("")
        }
    }
    CHAT.init();
</script>
</html>