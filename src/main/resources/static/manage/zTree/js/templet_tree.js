var setting = {
	view: {
		showLine: false,
		addHoverDom: addHoverDom,
		removeHoverDom: removeHoverDom,
		selectedMulti: false
	},
	edit: {
		drag: {
			isMove: true,
			next: true,
			prev: true,
			inner: false
		},
		enable: true,
		editNameSelectAll: false,
		showRemoveBtn: false,
		showRenameBtn: false
	},
	data: {
		simpleData: {
			enable: true
		}
	},
	callback: {
		beforeDrag: beforeDrag,
		beforeDrop: beforeDrop
	}
};
//点击拖拽
var dragId;//节点id
var dragKId;
function beforeDrag(treeId, treeNodes) {
	for(var i = 0, l = treeNodes.length; i < l; i++) {
		//被拖拽节点的父ID
		dragPID = treeNodes[i].pId;
		if(treeNodes[i].id != null && treeNodes[i].id != '' && treeNodes[i].id != 'undefined'){
			dragId = treeNodes[i].id;
		}else{
			dragKId = treeNodes[i].Kid;
		}
		if(treeNodes[i].drag === false) {
			return false;
		}
	}
	return true;
}
//落下
function beforeDrop(treeId, treeNodes, targetNode, moveType) {
	/**
	 * dragPID 被拖拽节点的父ID
	 * dragId 被拖拽节点id
	 * 
	 * targetNode.PId 落下节点的父ID
	 * targetNode.id 章节id
	 * Kid 课时id(标识当前对象是课时)
	 */
	
	if(dragId==null){//如果dragId 说明拖动的是课时
		
		//被拖拽id为dragKId
		if(targetNode.Kid == null){//落下节点的kid为空，说明当前落下节点不是课时
			upParentId(dragKId,targetNode.id,targetNode.Kid,"k");
		}else{
			upOrderIndex(dragKId,targetNode.id,targetNode.Kid);
		}
		
	}else if(dragId!=null){//如果dragId 说明拖动的是章节
		
		if(targetNode.Kid == null){//落下节点的kid为空，说明当前落下节点是章节
			upOrderIndex(dragId,targetNode.id,targetNode.Kid);
		}else{//当前为课时
			//往第三级节点下拖拽(不允许，则重新加载树)
			treeInit();
		}
		
	}
}
var zNodes = [];

function addHoverDom(treeId, treeNode) {
	var sObj = $("#" + treeNode.tId + "_span");
	//if(treeNode.editNameFlag || $("#addBtn_" + treeNode.tId).length > 0) return;
	if(sObj.parent().find('#jg_btn_group'+ treeNode.tId).length == 0) {
		if(treeNode.pId == null) {
			if(treeNode.Kid == null){
				var addStr = "<div id='jg_btn_group"+ treeNode.tId +"' style='position:absolute;right:0px;top:0px;'>" +
				"<i style='font-size:18px;' class='layui-icon' onclick='addDom("+treeNode.id+","+treeNode.pId+")' id='addBtn_" + treeNode.tId + "' title='添加'>&#xe654;</i>" +
				"&nbsp;&nbsp;<i style='font-size:18px;' class='layui-icon' id='edit_node' onclick='EditNode("+treeNode.id+","+treeNode.pId+","+treeNode.Kid+")' id='EditDepart' title='编辑'>&#xe642;</i>" +
				"&nbsp;&nbsp;<i style='font-size:18px;' class='layui-icon' id='delete_node' onclick='DeletNode("+treeNode.id+","+treeNode.Kid+")' id='DelDepart' title='删除'>&#xe640;</i>" +
				"</div>";
			}else{
				var addStr = "<div id='jg_btn_group"+ treeNode.tId +"'  style='position:absolute;right:0px;top:0px;'>" +
				"&nbsp;&nbsp;<i style='font-size:18px;' class='layui-icon' id='edit_node' onclick='EditNode("+treeNode.id+","+treeNode.pId+","+treeNode.Kid+")' id='EditDepart' title='编辑'>&#xe642;</i>" +
				"&nbsp;&nbsp;<i style='font-size:18px;' class='layui-icon' id='delete_node' onclick='DeletNode("+treeNode.id+","+treeNode.Kid+")' id='DelDepart' title='删除'>&#xe640;</i>" +
				"</div>";
			}
			
		} else {
			if(treeNode.Kid == null){
				var addStr = "<div id='jg_btn_group"+ treeNode.tId +"'  style='position:absolute;right:0px;top:0px;'>" +
				"<i style='font-size:18px;' class='layui-icon' onclick='addDom("+treeNode.id+","+treeNode.pId+")' id='addBtn_" + treeNode.tId + "' title='添加'>&#xe654;</i>" +
				"&nbsp;&nbsp;<i style='font-size:18px;' class='layui-icon' id='edit_node' onclick='EditNode("+treeNode.id+","+treeNode.pId+","+treeNode.Kid+")' id='EditDepart' title='编辑'>&#xe642;</i>" +
				"&nbsp;&nbsp;<i style='font-size:18px;' class='layui-icon' id='delete_node' onclick='DeletNode("+treeNode.id+","+treeNode.Kid+")' id='DelDepart' title='删除'>&#xe640;</i>" +
				"</div>";
			}else{
				var addStr = "<div id='jg_btn_group"+ treeNode.tId +"'  style='position:absolute;right:0px;top:0px;'>" +
				"&nbsp;&nbsp;<i style='font-size:18px;' class='layui-icon' id='edit_node' onclick='EditNode("+treeNode.id+","+treeNode.pId+","+treeNode.Kid+")' id='EditDepart' title='编辑'>&#xe642;</i>" +
				"&nbsp;&nbsp;<i style='font-size:18px;' class='layui-icon' id='delete_node' onclick='DeletNode("+treeNode.id+","+treeNode.Kid+")' id='DelDepart' title='删除'>&#xe640;</i>" +
				"</div>";
			}
		}
	}
	sObj.after(addStr);
//	var btn = $("#addBtn_"+treeNode.tId);
//	if (btn) btn.bind("click", function(){
//		console.log(treeNode);
//	});
	
};
//function addDom(e){
//	alert(1);
//}
function removeHoverDom(treeId, treeNode) {
	$('#jg_btn_group'+ treeNode.tId).unbind().remove();
};