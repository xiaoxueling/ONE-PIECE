<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="/WEB-INF/tlds/jsp-masterpage.tld" prefix="m"%>
<m:ContentPage materPageId="master">
	<m:Content contentPlaceHolderId="css">
		<style>
			body {
				margin: 0px;
				padding: 0px;
				font-family: "微软雅黑", Arial, "Trebuchet MS", Verdana, Georgia, Baskerville, Palatino, Times;
				font-size: 16px;
			}
			
			div {
				margin-left: auto;
				margin-right: auto;
			}

			h1 {
				font-size: 37px;
				color: #09c;
				padding: 20px 0px 10px 0px;
				margin-bottom: 30px;
			}
			
			#page {
				width: 910px;
				padding: 20px 20px 30px 20px;
				margin-top: 80px;
			}
			span.error_icon{
				color:red;
			}
		</style>
	</m:Content>
	<m:Content contentPlaceHolderId="content">
			<div id="page" style="border-style:dashed;border-color:#e4e4e4;line-height:30px;">
				<h1>
					<span class="fa fa-close error_icon"></span>
					  您不具有访问当前页面的权限，请求已被禁止！
				</h1>
				
				<font color="#666666">请联系管理员开通权限！</font><br /><br />
			</div>
	</m:Content>
</m:ContentPage>