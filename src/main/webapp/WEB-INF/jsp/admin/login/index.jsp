<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${siteInfo.siteTitle }</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
<meta name="apple-mobile-web-app-capable" content="yes"/>
<link rel="stylesheet" href="/manage/public/layui/css/layui.css" />
<link rel="stylesheet" href="/manage/public/css/login/login.css" />
<style type="text/css">
	.hint {
		display: inline-block;
		width: 45px;
		position: relative;
		top: 10px;
	}
</style>
</head>
<body>
	<!-- 内容主体区域 -->
	<div class="Login">
		<div class="login_c">
			<form id="login">
				<input type="hidden" name="loginMethod" value="PC">
				<ul>
					<li><h2>${siteInfo.siteName }</h2></li>
					<li><input type="text" class="dl_tu dl_tu1" placeholder="请输入用户名" id="userName" name="userName"></li>
					<li><input type="password" class="dl_tu dl_tu2" placeholder="请输入密码" id="userPwd" name="userPwd"></li>
					<li><input type="text" id="code" name="code" class="dl_tu dl_tu3" placeholder="请输入验证码" style="width: 210px;"/>
						<img id="IMG_Code" src="/code"  alt="点击刷新验证码" onclick="javascript:this.src='/code?r='+Math.random();" class="yanzheng" style="height: 46px;width: 85px;cursor:pointer;"/>
					</li>
					<li><input type="button" class="dl_an" value="登录" onclick="btnClick(this)"></li>
				</ul>
			</form>
		</div>
	</div>
<script src="/manage/public/js/jquery-1.11.3.js"></script>
<script type="text/javascript" src="/manage/public/layui/layui.js"></script>
<script type="text/javascript">
	var layer;
	layui.use('layer', function(){
		  layer = layui.layer;
	}); 
	$(function(){
		var index;
		$("#IMG_Code").hover(function(){
			index=layer.tips("点击刷新验证码", this, {
				time : 60*1000*60,
				tips : [ 2, '#76c57f' ]
			});
		},function(){
			layer.close(index);
		});
		
		//判断是否是在iframe中
		if(top.location.href!=location.href){
			top.location.href=location.href;
		}
		$("#userName").focus();
		
	})
	
	$("body").keydown(function(event){
		var code = event.keyCode;
		if(code==13){//回车
			btnClick($(".dl_an")); //调用btnClick函数
		}      
	});
	
	function btnClick(o){
		
		var userName=$("#userName").val();
		if(userName==""){
			tipinfo("请输入用户名！","#userName");
			$("#userName").focus();
			return false;
		}
		var userPwd=$("#userPwd").val();
		if(userPwd==""){
			tipinfo("请输入密码！","#userPwd");
			$("#userPwd").focus();
			return false;
		}
		var code=$("#code").val();
		if(code==""){
			tipinfo("请输入验证码！","#code");
			$("#code").focus();
			return false;
		}
		
		$(o).val("正在登录...").attr("disabled",true);
		
		$.ajax({
			type:"post",
			data:$("#login").serialize(),
			url:"/${applicationScope.adminprefix }/login/userLogin",
			success:function(data){
				if(data.isLogin){
					var redirect='${redirectUrl}';
					if(redirect!=''){
						location.href=redirect;
						return;
					}
	        		location.href='/${applicationScope.adminprefix }/index';
	    		}else{
	    			var txt= data.message;
	    			tipinfo(txt);
	    			$("#code").val("");
	    			$("#IMG_Code").click();
	    			if(txt.indexOf("验证码")==-1){
	    				$("#userName").focus();
		    			$("#login input").val("");
	    			}else{
	    				$("#code").focus();
	    			}
	    			$(o).val("登录").removeAttr("disabled");
	    		}
			},
			error:function(data){
				tipinfo("网络异常,请稍后再试！");
				$(o).val("登录").removeAttr("disabled");
			}
		})
	}
	function tipinfo(msg, obj) {
		layer.closeAll();
		if (obj == null || typeof obj == "undefined") {
			layer.msg('<font style="color:#fff" width="100%">' + msg+ '</font>', {
				time : 3000,
				offset : 80,
			});
		} else {
			layer.tips(msg, obj, {
				time : 2000,
				tips : [ 2, '#f75c4c' ]
			});
		}
	}
</script>	
</body>
</html>