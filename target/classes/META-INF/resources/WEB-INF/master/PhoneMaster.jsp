<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="/WEB-INF/tlds/jsp-masterpage.tld" prefix="m"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<m:MasterPage id="PhoneMaster">
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta name="apple-mobile-web-app-status-bar-style" content="black" />
<meta name="keywords" content="${SEO.metaKeywords}" />
<meta name="description" content="${SEO.metaDescription}" />
<link rel="stylesheet" href="/phone/css/base.css" />
<link rel="stylesheet" href="/phone/css/index.css" />
<link href="/phone/js/layer/mobile/need/layer.css" rel="stylesheet" type="text/css" />
<style type="text/css">
	.layui-m-layerbtn span[yes] {
		margin: 15px auto;
	}
	
	.layui-m-layer-footer .layui-m-layerbtn span {
		background-color: rgba(255, 255, 255, .8);
		height: 50px;
		line-height: 50px;
	}
</style>
<m:ContentPlaceHolder id="css"></m:ContentPlaceHolder>
<title>${SEO.title}</title>
	<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
	<!--[if lt IE 9]>
		<script src="/manage/public/js/html5shiv.js"></script>
		<script src="/manage/public/js/respond.min.js"></script>
	<![endif]-->
</head>
<body>
	<m:ContentPlaceHolder id="content"></m:ContentPlaceHolder>
	
	<c:if test="${empty requestScope.footer || requestScope.footer}">
		<footer>
		  <div  class="nav4">
		    <ul class="box">
		    	<li> <a href="/phone/home/index" class="a1"><span>首页</span></a> </li>
          		<li> <a href="/phone/usercenter/node/nodeListFace" class="a2"><span>学习</span></a> </li>
          		<li> <a href="javascript:void(0)" class="yy_biao">播放</a> </li>
          		<li> <a href="/phone/order/turnShopcart" class="a3"><span>购物车</span></a> </li>
		      	<li> <a href="/phone/usercenter/account/index" class="a4"><span>我的</span></a> </li>
		    </ul>
		  </div>
		</footer>
	</c:if>
	
	<script src="/phone/js/jquery.js"></script>
	<script src="/phone/js/layer/mobile/layer.js"></script>
	<script type="text/javascript">
		$(function() {
			$("img").error(function() {
				$(this).attr("src", "/phone/images/noImage.jpg");
			});

			//播放
			$(".yy_biao").click(function(){
				//先判断是否已经登陆
				$.ajax({
					type:'get',
					url:'/phone/Islogin',
					datatype:'json',
					success:function(data){
						if(data.success){
							var url=localStorage.getItem('url');
							if(url==null||url==''){
								tipinfo("暂无最近播放记录!");
								return false;
							}
							window.location.href=url;
						}else{
							tipinfo(data.msg);
						}
					},
					error:function(){
					}
				})
			})
			
			$(document).ajaxSuccess(function() {
				$("img").error(function() {
					$(this).attr("src", "/phone/images/noImage.jpg");
				});
			});
			;
			(function(win, doc) {
				function change() {
					if (!window["NORESIZE"]) {
						doc.documentElement.style.fontSize = 20* doc.documentElement.clientWidth / 320 + 'px';
					}
				}
				change();
				win.addEventListener('resize', change, false);
			})(window, document);

			if (window["JSSDK"]) {
				wx.config({
					debug : false, // 开启调试模式
					appId : '${appId}', // 必填，公众号的唯一标识
					timestamp : '${timestamp}', // 必填，生成签名的时间戳
					nonceStr : '${nonceStr}', // 必填，生成签名的随机串
					signature : '${signature}',// 必填，签名
					jsApiList : [ 'openLocation', 'getLocation' ]
				// 必填，需要使用的JS接口列表
				});
				wx.error(function() {
					tipinfo("Wechat JS Init Error!");
				});
			}
			
			$("footer a[href='"+window.location.pathname+"']").addClass("on");
			
			$(window).on("scroll",function(){
				var top= $(this).scrollTop();
				if(top>=$(".top").height()*2){
					$(".top").css({"position":"fixed","z-index":"10000","top":"0"});
				}else{
					$(".top").css({"position":""})
				}
			})
		});

		window.alert = function(o) {
			alertinfo(o);
		}
		window.onerror = function() {
			loading(false);
			console.log(arguments);
			//return true;
		}
		
		function alertinfo(msg, action) {
			layer.open({
				content : msg,
				shadeClose : false,
				btn : '确定',
				yes : function(index, layero) {
					if (action != null) {
						action();
					}
					layer.close(index);
				}
			});
		}

		function confirminfo(msg, action,cancel) {
			layer.open({
				content : msg,
				btn : [ '确定', '取消' ],
				skin : 'footer',
				shadeClose : false,
				yes : function(index) {
					if (action != null) {
						action();
					}
					layer.close(index);
				},
				btn2:function(index){
					if (cancel != null) {
						cancel();
					}
					layer.close(index);
				}
			});
		}

		function tipinfo(msg) {
			layer.open({
				content : '<font style="color:#fff">' + msg + '</font>',
				skin : 'msg',
				time : 3
			});
		}
		//底部弹窗，无标题
		function bottomwin(content){
			layer.open({
			    type: 1,
			    content: content,
			    anim: 'up',
			    style: 'position:fixed; bottom:0; left:0; width: 100%; height: 200px; padding:10px 0; border:none;'
			  });
		}
		
		function tipwin(title,content){
			layer.open({
			    title: [
			      title,
			      'background-color: #FF4351; color:#fff;'
			    ]
			    ,content: content
			  });
		}
		
		function loading(flag) {
			if (flag) {
				layer.open({
					type : 2,
					shade : [ 0.5, '#D6D6D6' ]
				});
			} else {
				layer.closeAll('loading');
			}
		}
		
		$.CutStr = function(str, len, dot) {
			if ($.trim(str) == "") {
				return "";
			}
			if (dot == null) {
				dot = "...";
			}
			var length = str.length;
			if (length <= len) {
				return str;
			}
			return str.substring(0, len) + dot;
		}

		//时间格式化
		Date.prototype.format = function(format) {
			var date = {
				"M+" : this.getMonth() + 1,
				"d+" : this.getDate(),
				"h+" : this.getHours(),
				"m+" : this.getMinutes(),
				"s+" : this.getSeconds(),
				"q+" : Math.floor((this.getMonth() + 3) / 3),
				"S+" : this.getMilliseconds()
			};
			if (/(y+)/i.test(format)) {
				format = format.replace(RegExp.$1, (this.getFullYear() + '')
						.substr(4 - RegExp.$1.length));
			}
			for ( var k in date) {
				if (new RegExp("(" + k + ")").test(format)) {
					format = format.replace(RegExp.$1,
							RegExp.$1.length == 1 ? date[k] : ("00" + date[k])
									.substr(("" + date[k]).length));
				}
			}
			return format;
		};

		function parseTime(timestamp, f) {
			if (f == null) {
				f = "yyyy-MM-dd hh:mm:ss";
			}
			var date = new Date(parseInt(timestamp));
			return date.format(f);
		}

		function convertDateFromString(dateString) {
			if (dateString) {
				var arr1 = dateString.split(" ");
				var sdate = arr1[0].split('-');
				if (arr1.length == 2) {
					var smin = arr1[1].split(":");
					var date = new Date(sdate[0], sdate[1] - 1, sdate[2],
							smin[0], smin[1]);
					return date;
				} else {
					var date = new Date(sdate[0], sdate[1] - 1, sdate[2]);
					return date;
				}

			}
		}
	</script>
	<m:ContentPlaceHolder id="js"></m:ContentPlaceHolder>
</body>
</html>
</m:MasterPage>
