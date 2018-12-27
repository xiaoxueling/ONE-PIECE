package cn.main.api.service;

import java.util.HashMap;
import java.util.Map;
import cn.util.StringHelper;

/**
 * 接口返回数据
 * @author xiaoxueling
 *
 */
public class ApiResult {
	
	private final static String KEY_RESULT="result";
	private final static String KEY_MESSAGE="msg";
	private final static String KEY_DATA="data";
	private final static String KEY_TOTAL_PAGE="totalPage";
	private final static String KEY_CURRENT_PAGE="currentPage";
	
	private Map<String, Object> resultMap=null;
	
	public ApiResult() {
		 this(false,"");
	}
	
	public ApiResult(boolean isSuccess,String message) {
		 resultMap=new HashMap<String,Object>();
		 
		 this.setSuccess(isSuccess);
		 this.setData("");
		 if(!StringHelper.IsNullOrEmpty(message)) {
			 this.setMessage(message);
		 }
	}

	/**
	 * 设置是否成功
	 * @param isSuccess
	 */
	public void setSuccess(boolean isSuccess) {
		resultMap.put(KEY_RESULT, isSuccess?1:0);
		this.setMessage(isSuccess?"成功！":"失败！");
	}
	
	/**
	 * 设置返回的提示信息
	 * @param message
	 */
	public void setMessage(String message) {
		resultMap.put(KEY_MESSAGE, message);
	}
	
	/**
	 * 设置返回的数据
	 * @param data
	 */
	public void setData(Object data) {
		resultMap.put(KEY_DATA, data);
	}
	
	/**
	 * 设置总页数
	 * @param totalPage
	 */
	public void setTotalPage(int totalPage) {
		resultMap.put(KEY_TOTAL_PAGE, totalPage);
	}
	
	/**
	 * 设置当前页码
	 * @param currentPage
	 */
	public void setCurrentPage(int currentPage) {
		resultMap.put(KEY_CURRENT_PAGE, currentPage);
	}
	
	/**
	 * 设置返回结果
	 * @param isSuccess 是否成功
	 * @param message 提示信息
	 */
	public void setResult(boolean isSuccess,String message) {
		this.setResult(isSuccess, message,null);
	}
	
	/**
	 * 设置返回结果
	 * @param isSuccess 是否成功
	 * @param message 提示信息
	 * @param data 数据
	 */
	public void setResult(boolean isSuccess,String message,Object data) {
		this.setSuccess(isSuccess);
		this.setMessage(message);
		this.setData(data);
	}
	
	/**
	 * 设置页数
	 * @param totalCount 总数量
	 * @param pageSize 页容
	 */
	public void setTotalPage(long totalCount,Integer pageSize) {
		this.setTotalPage(totalCount,pageSize,1);
	}
	
	/**
	 * 设置页数
	 * @param totalCount 总数量
	 * @param pageSize 页容
	 * @param currentPage 页码
	 */
	public void setTotalPage(long totalCount,Integer pageSize,Integer currentPage) {
		if(pageSize!=null&&pageSize.intValue()>0) {
			int totalPage=(int)(totalCount / pageSize.intValue() )+(totalCount % pageSize.intValue() > 0 ? 1: 0);
			this.setTotalPage(totalPage);
		}
		if(currentPage!=null&&currentPage.intValue()>0) {
			this.setCurrentPage(currentPage.intValue());
		}
	}
	
	/**
	 * 获取结果Map
	 * @return
	 */
	public  Map<String, Object>getResult(){
		return resultMap;
	}
}
