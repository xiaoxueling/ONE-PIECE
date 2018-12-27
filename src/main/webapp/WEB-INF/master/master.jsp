<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib  uri="/WEB-INF/tlds/jsp-masterpage.tld" prefix="m"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<m:MasterPage id="master">
<!DOCTYPE html>
<html>
<head>
<meta name="renderer" content="webkit|ie-comp|ie-stand">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="/manage/public/layui/css/layui.css" />
<link rel="stylesheet" href="/manage/public/js/themes/default/css/ueditor.css" />
	<style type="text/css">
		.leftMain .layui-icon {
			margin-right: 5px;
		}
	</style>
<m:ContentPlaceHolder id="css"></m:ContentPlaceHolder>
<title>${title }</title>
</head>
<body style="overflow-y:auto" class="layui-layout-body">
	<m:ContentPlaceHolder id="content"></m:ContentPlaceHolder>
	<script type="text/javascript" src="/manage/public/js/jquery-1.11.3.js"></script>
	<script type="text/javascript" src="/manage/public/layui/layui.js"></script>
	<script type="text/javascript" src="/manage/public/layui/mui_use.js"></script>
	<script type="text/javascript" charset="utf-8" src="/manage/public/js/ueditor.config.js"></script>
	<script type="text/javascript" charset="utf-8" src="/manage/public/js/ueditor.all.js"> </script>
	<script type="text/javascript" charset="utf-8" src="/manage/public/js/lang/zh-cn/zh-cn.js"></script>
	<script type="text/javascript">
		var layer;
		layui.use(['layer','table'], function(){
			  layer = layui.layer;
			  table = layui.table;
		}); 
		$(function(){
			$("img").error(function() {
				$(this).attr("src", "/manage/images/index/noImage.jpg");
			});

			$(document).ajaxSuccess(function() {
				$("img").error(function() {
					$(this).attr("src", "/manage/images/index/noImage.jpg");
				});
			});
		})
		
		window.alert = function(o) {
			if (o==null){
				return false;
			}
			alertinfo(o,false);
		}
		
		function loading(flag){
			if (!IsTopWindow()) {
				window.top.loading(flag);
			} else {
				if (flag) {
					layer.load(0, {
						shade : [ 0.5, '#D6D6D6' ]
					});
				} else {
					layer.closeAll('loading');
				}
			}
		}
		
		function alertinfo(msg, flag,callback) {
			if (!IsTopWindow()) {
				window.top.alertinfo(msg, flag,callback);
			} else {
				var index=layer.alert(msg,{icon:flag?6:5},function(){
					if(callback!=null){
						callback();
					}
					layer.close(index);
				});
			}
		}
		
		function confirminfo(msg,callback){
			confirm(msg,callback);
		}
		
		function confirm(msg, ok, cancel) {
			if (!IsTopWindow()) {
				window.top.confirm(msg, ok, cancel);
			} else {
				layer.confirm(msg, {
					icon : 3,
					title : '提示'
				}, function(index) {
					if (ok != null) {
						ok();
					}
					layer.close(index);
				}, function(index) {
					if (cancel != null) {
						cancel();
					}
					layer.close(index);
				});
			}
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
					tips : [ 3, '#f44336' ]
				});
			}
		}
		
		function openwindow(url,title,width,height,isfull,callback){
			
			if(isfull){
				width="100%";
				height="100%";
			}else{
				if ($(window).height() <= height) {
					height = $(window).height() - 150;
				}
				if ($(window).width() <= width) {
					width = $(window).width() - 150;
				}
				
				height=height+'px';
				width=width+'px';
			}
			
			//loading(true);
			layer.open({
				type: 2,
				title: [title, 'font-size:18px;'],
				anim:0,
				maxmin:!isfull,
				shade:[0.5, '#393D49'],
				shadeClose: false,
				area: [width, height],
				content: '/${applicationScope.adminprefix }/'+url,
				success: function(layero, index) {
					if(isfull){
						layer.full(index);
					}
					loading();
				},
				end: function() { //销毁后触发
					if (callback != null) {
						callback();
					}
				}
			});
		}
		
		function closewindow(){
			var index = parent.layer.getFrameIndex(window.name);
			parent.layer.close(index);
		}
		
		function getUrl(url){
			
			var pref="/${applicationScope.adminprefix}/";
			if(url.indexOf(pref)==-1){
				url=pref+url;
			}
			return url;
		}
		
		function IsTopWindow(){
			return top.location.href==self.location.href;
		}
		
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
		
	</script>
	<m:ContentPlaceHolder id="js"></m:ContentPlaceHolder>
</body>
</html>
</m:MasterPage>
