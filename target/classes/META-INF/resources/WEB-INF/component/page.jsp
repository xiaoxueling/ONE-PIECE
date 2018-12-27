<%@ page language="java" isELIgnored="false"
	contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="${Model.className}">
	<c:choose>
		 <c:when test="${Model.showNoDataInfo and Model.totalRecord==0}">
		 	<div style="clear:none;text-align:center;font-size: 16px;margin: 20px;">
		        <span>暂时没有数据</span>
		    </div>
		 </c:when>
		 <c:when test="${(Model.totalPages<2 and Model.showOnlyPage) or Model.totalPages>1}">
		 		<!-- 左侧数据展示 -->
		 		<c:if test="${Model.pageInfoPosition eq 'LEFT'}">
		 			<c:if test="${Model.showTotalRecord}">
		 				<!--左侧记录总数信息 -->
		 				<span>共${Model.totalRecord}条数据</span>
		 			</c:if>
		 			<c:if test="${Model.showPageInfo}">
		 				<!--左侧页码信息 -->
		 				<span>
		 					<c:choose>
		 						<c:when test="${Model.enableSelectPageSize}">
			 						<label>每页</label>
				 					<select id="Sel_${Model.currentIndex}" onchange="JumpPage_${Model.currentIndex}(1);">
					 					<c:forEach items="${Model.pageSizeArray}" var="item">
					 						<option value="${item}" ${item==Model.pageSize?"selected":""}>${item}</option>
					 					</c:forEach>
		                            </select>
		                            <label>项</label>
		 						</c:when>
		 						<c:otherwise>
		 							每页 ${Model.pageSize} 项
		 						</c:otherwise>
		 					</c:choose>
		 					,第 ${Model.pageIndex} 页/共 ${Model.totalPages} 页
		 				</span>
		 			</c:if>
		 		</c:if>
		 		<!-- 首页 -->
		 		<c:if test="${Model.showFirstLastLink}">
			 		<span>
				 		<c:choose>
			 				<c:when test="${Model.pageIndex>1}">
			 					<a href="javascript:;" onclick="JumpPage_${Model.currentIndex}(1);">首页</a>
			 				</c:when>
			 				<c:otherwise>
			 					<a href="javascript:;">首页</a>
			 				</c:otherwise>
			 			</c:choose>
			 		</span>
		 		</c:if>
		 		<!-- 上一页 -->
		 		<c:if test="${Model.showPrevNextLink}">
			 		<span>
				 		<c:choose>
			 				<c:when test="${Model.pageIndex>1}">
			 					<a href="javascript:;" title="上一页" onclick="JumpPage_${Model.currentIndex}(${Model.pageIndex - 1});">◀</a>
			 				</c:when>
			 				<c:otherwise>
			 					 <a href="javascript:;">◀</a>
			 				</c:otherwise>
			 			</c:choose>
			 		</span>
		 		</c:if>
		 		<!--数字导航 -->
		 		<c:if test="${Model.showNumberLink}">
				 	<c:choose>
			 			<c:when test="${Model.totalRecord==0}">
			 				<span class="on">1</span>
			 			</c:when>
			 			<c:otherwise>
			 				<!-- 向上... -->
			 				<c:if test="${Model.showOmitNumberLink and Model.numberStartPageIndex>1}">
			 					<span>
		                            <a href="javascript:;" onclick="JumpPage_${Model.currentIndex}(${ Model.pageIndex - 1});">....</a>
		                        </span>
			 				</c:if>
			 				<!-- 数字 -->
			 				<c:forEach items="${Model.numberPageArray}" var="item">
			 					<c:choose>
			 						<c:when test="${item!=Model.pageIndex}">
			 							<span>
			                                <a href="javascript:;" onclick="JumpPage_${Model.currentIndex}(${item})">${item}</a>
			                            </span>
			 						</c:when>
			 						<c:otherwise>
			 							<span class="on">${item}</span>
			 						</c:otherwise>
			 					</c:choose>
			 				</c:forEach>
			 				<!-- 向下... -->
			 				<c:if test="${Model.showOmitNumberLink and  Model.numberEndPageIndex<Model.totalPages}">
			 					<span>
		                            <a href="javascript:;" onclick="JumpPage_${Model.currentIndex}(${ Model.pageIndex + 1});">....</a>
		                        </span>
			 				</c:if>
			 			</c:otherwise>
			 		</c:choose>
		 		</c:if>
		 		<!-- 下一页 -->
		 		<c:if test="${Model.showPrevNextLink}">
			 		<span>
				 		<c:choose>
			 				<c:when test="${Model.pageIndex<Model.totalPages}">
			 					<a href="javascript:;" title="下一页" onclick="JumpPage_${Model.currentIndex}(${Model.pageIndex +1});">▶</a>
			 				</c:when>
			 				<c:otherwise>
			 					 <a href="javascript:;">▶</a>
			 				</c:otherwise>
			 			</c:choose>
			 		</span>
		 		</c:if>
		 		<!-- 尾页 -->
		 		<c:if test="${Model.showFirstLastLink}">
			 		<span>
				 		<c:choose>
			 				<c:when test="${Model.pageIndex<Model.totalPages}">
			 					<a href="javascript:;" onclick="JumpPage_${Model.currentIndex}(${Model.totalPages});">尾页</a>
			 				</c:when>
			 				<c:otherwise>
			 					<a href="javascript:;">尾页</a>
			 				</c:otherwise>
			 			</c:choose>
			 		</span>
		 		</c:if>
		 		<!-- 右侧数据展示 -->
		 		<c:if test="${Model.pageInfoPosition eq 'RIGHT'}">
		 			<c:if test="${Model.showTotalRecord}">
		 				<!--右侧记录总数信息 -->
		 				<span>共${Model.totalRecord}条数据</span>
		 			</c:if>
		 			<c:if test="${Model.showPageInfo}">
		 				<!--右侧页码信息 -->
		 				<span>
		 					<c:choose>
		 						<c:when test="${Model.enableSelectPageSize}">
			 						<label>每页</label>
				 					<select id="Sel_${Model.currentIndex}" onchange="JumpPage_${Model.currentIndex}(1);">
					 					<c:forEach items="${Model.pageSizeArray}" var="item">
					 						<option value="${item}" ${item==Model.pageSize?"selected":""}>${item}</option>
					 					</c:forEach>
		                            </select>
		                            <label>项</label>
		 						</c:when>
		 						<c:otherwise>
		 							每页 ${Model.pageSize} 项
		 						</c:otherwise>
		 					</c:choose>
		 					,第 ${Model.pageIndex} 页/共 ${Model.totalPages} 页
		 				</span>
		 			</c:if>
		 		</c:if>
		 		<!--页码输入框-->
		 		<c:if test="${Model.showGoInput and Model.totalPages>1}">
		 			<span style="margin:auto 10px">转到</span>
	                <input type="number" value="${Model.pageIndex}" max="${Model.totalPages}" min="1" id="InputPage_${ Model.currentIndex}" onkeyup="value=value.replace(/[^\d]+?/g,'')" class="in1" placeholder="" /><span style="margin:auto 10px">页</span>
	                <input type="button" value="跳转" class="tz_biao" onclick="Go_${ Model.currentIndex}();" />
		 		</c:if>
		 </c:when>
	</c:choose>
</div>
<script type="text/javascript">
    function JumpPage_${Model.currentIndex}(page) {
        var isAjaxPager=${Model.ajaxPager};
        var url = "${Model.url}";
        var pageSize =${Model.pageSize};
        if (${Model.enableSelectPageSize}){
            pageSize = $("#Sel_${Model.currentIndex}").val();
        }
        if (isAjaxPager) {
            $.get(url,
            {
                ${Model.pageParameter}:page,
                pageSize: pageSize,
                r:Math.random()
            },
            function(html){
                $("#${Model.ajaxUpdateTargetId}").html(html);
                var functionName =${Model.ajaxSuccessFunctionName==null?'null':Model.ajaxSuccessFunctionName};
                if (functionName != null &&functionName!=""&& typeof functionName == 'function') {
                    functionName();
                }
            },"html");
        }else{
            window.location.href=url+(url.indexOf("?")!=-1?"&":"?")+"${Model.pageParameter}="+page+"&pageSize="+pageSize+"&r="+Math.random();
        }
    }

    function Go_${Model.currentIndex}(){
    	var $o=$("#InputPage_${Model.currentIndex}");
        var page=$o.val();
        if (page== "")
        {
            alert("请输入页码.",false);
            $o.focus();
            return false;
        }
        var reg = /^[0-9]+$/ig;
        if (!reg.test(page))
        {
            alert("页码只能输入数字.",false);
            $o.focus();
            return false;
        }
        var totalPage=${Model.totalPages};
        if(page>totalPage){
            alert("最多跳转到"+totalPage+"页.",false);
            $o.val(totalPage);
            return false;
        }
        JumpPage_${Model.currentIndex}(page);
    }
</script>

<!-- 默认样式 -->
<style type="text/css">
.pager {
	margin: 30px auto;
	text-align: center;
	font-size: 14px;
}

.pager span {
	margin: 0 5px;
}

.pager span a {
	border: 1px solid #ccc;
	padding: 2px 8px;
}

.pager span a:hover, .pager span.on {
	padding: 2px 8px;
	border: 1px solid #e30000;
	background: #e30000;
	color: #fff;
}

.pager .in1 {
	width: 45px;
	height: 25px;
	text-align: center;
	font-size: 14px;
	line-height: 25px;
	border: 1px solid #dedede;
	background: #f6f6f6;
}

.pager .tz_biao {
	padding: 3px 10px;
	border: 1px solid #dedede;
	background: #f6f6f6;
	font-size: 14px;
	cursor: pointer;
}

.pager input.tz_biao:hover {
	padding: 3px 10px;
	border: 1px solid #e30000;
	background: #e30000;
	color: #fff;
	cursor: pointer;
}
</style>