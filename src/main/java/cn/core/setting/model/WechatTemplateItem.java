package cn.core.setting.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 模板设置的详情
 * @author xiaoxueling
 *
 */
public class WechatTemplateItem {

	@Override
	public String toString() {
		return "WechatTemplateItem [templateId=" + templateId + ", title="
				+ title + ", data=" + data + ", remark=" + remark + "]";
	}

	public WechatTemplateItem(){
		data=new HashMap<String, String>();
	}
	/*
	  模板标准格式（三段式模板）
	  { {first.DATA} }
	  关键词1：{ {keyword1.DATA} }           
	  关键词2：{ {keyword2.DATA} }
	  关键词3：{ {keyword3.DATA} }                       
	  { {remark.DATA} }
	*/
	
	/**
	 * 模板Id
	 */
	private String templateId;
	
	/**
	 * 模板标题(first)
	 */
	private String title;
	
	/**
	 * 模板关键词数据: <关键词数据，内容{替换的数据}>（\n 是折行）
	 *  <pre>
	 *  例如：
	      Map.put("keyword1","您的订单编号是{orderNo}");
	      Map.put("keyword2","订单时间{orderTime}");
	     </pre>
	 */
	private Map<String, String> data;
	
	/**
	 * 模板remark
	 */
	private String remark;

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
