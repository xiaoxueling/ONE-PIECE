package cn.util.wechat;

import java.util.List;

import org.apache.commons.collections4.list.TreeList;

public class WxMenuItem {
	
	private String type;
	private String name;
	private String key;
	private String url;
	private String media_id;
	private List<WxMenuItem> sub_button;
	
	public WxMenuItem(){
		sub_button=new TreeList<WxMenuItem>();
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMedia_id() {
		return media_id;
	}
	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}
	public List<WxMenuItem> getSub_button() {
		return sub_button;
	}
	public void setSub_button(List<WxMenuItem> childMenus) {
		this.sub_button = childMenus;
	}
}
