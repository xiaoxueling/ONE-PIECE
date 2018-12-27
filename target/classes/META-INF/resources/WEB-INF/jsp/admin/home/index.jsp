<%@ page language="java" isELIgnored="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="/WEB-INF/tlds/authorizetag.tld" prefix="u"%>
<%@ taglib uri="/WEB-INF/tlds/jsp-masterpage.tld" prefix="m"%>
<%@ taglib uri="/WEB-INF/tlds/pagetag.tld" prefix="p"%>
<m:ContentPage materPageId="master">
	<m:Content contentPlaceHolderId="css">
	 <style type="text/css">
			
	 </style>
	</m:Content>
	<m:Content contentPlaceHolderId="content">
		admin/home/index
		<div>
			 ${siteInfo.siteTitle}
			 ${siteInfo.siteName}
			 ${siteInfo.siteUrl}
		</div>
		<table>
			<c:forEach items="${userList}" var="item">
				<tr>
					<td>${item.realName}</td>
					<td>${item.userName}</td>
					<td>${item.nickName }</td>
				</tr>
			</c:forEach>
		</table>
		
		
		<p:Page url="/admin/index" page="${pageInfo}"></p:Page>
		
	</m:Content>
	<m:Content contentPlaceHolderId="js">
		<script type="text/javascript">
			$(function() {
				
				
				 
			});
		</script>
	</m:Content>
</m:ContentPage>
