package cn.util.wechat;

import java.util.List;

import org.apache.commons.collections4.list.TreeList;

public class WxMenu {
	
	private List<WxMenuItem> button;

	public WxMenu(){
		button=new TreeList<WxMenuItem>();
	}
	
	public List<WxMenuItem> getButton() {
		return button;
	}

	public void setButton(List<WxMenuItem> menuItems) {
		this.button = menuItems;
	}
}
