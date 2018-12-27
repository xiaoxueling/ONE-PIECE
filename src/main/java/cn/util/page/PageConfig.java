package cn.util.page;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.servlet.ModelAndView;

public class PageConfig extends PageInfo {
	
	/**
	 * 分页信息元素位置  
	 */
    public enum PagerElementPosition
    {
       /**
        * 居左
        */
        Left("LEFT"),

        /**
         * 居右
         */
        Right("RIGHT");
        
        private String _position;
        
        PagerElementPosition(String position){
        	this._position=position;
        }
        
    	@Override
    	public String toString(){
    		return String.valueOf(this._position);
    	}
    }
	
	private int currentIndex;//当前page索引（应用于同一页面多个pager时）
	private String url;//url 地址
	private String pageParameter;//当前页码的参数名(默认 page)
	private String pageInfoPosition;//当前页和总页数信息显示位置 Left/Right
	private String className;//class样式名称
	private boolean isShowFirstLastLink;//是否显示首页、尾页链接 
	private boolean isShowPrevNextLink;//是否显示上一页、下一页链接
	private boolean isShowNumberLink;//是否显示数字按钮
	private int numberLinkCount;//数字按钮数量
	private boolean isShowOmitNumberLink;//是否显示省略的数字按钮
	private boolean isShowOnlyPage;// 当总页数小于2页时是否显示
	private boolean isShowNoDataInfo;// 是否显示无数据提示信息
	private boolean isShowTotalRecord;// 是否显示总记录数
	private boolean isShowPageInfo;// 是否显示当前页数和总页数信息
	private boolean isShowGoInput;// 是否显示GoTo输入区域  
	private boolean isAjaxPager;// 是否创建为ajax分页控件
	private String ajaxUpdateTargetId;//  Ajax提交后更新的html元素id
	private String ajaxSuccessFunctionName;//  Ajax提交后调用的js function名称
	private boolean enableSelectPageSize;// 是否可以更改每页数量
	private int[] pageSizeArray;// 每页数量是否可以显示
	private int numberStartPageIndex;//数字按钮开始页
	private int numberEndPageIndex;//数字按钮结束页
	private List<Integer> numberPageArray;
	
	public PageConfig(){
		currentIndex=100;
		setPageSize(20);
		pageParameter="page";
		className="pager";
		isShowFirstLastLink=true;
		isShowPrevNextLink=true;
		isShowNumberLink=true;
		numberLinkCount=5;
		isShowOmitNumberLink=true;
		isShowGoInput=true;
		pageSizeArray=new int[]{10, 20, 50, 100};
		pageInfoPosition = PagerElementPosition.Left.toString();
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPageParameter() {
		return pageParameter;
	}

	public void setPageParameter(String pageParameter) {
		this.pageParameter = pageParameter;
	}

	public String getPageInfoPosition() {
		return pageInfoPosition;
	}

	public void setPageInfoPosition(String pageInfoPosition) {
		if(pageInfoPosition!=null){
			this.pageInfoPosition = pageInfoPosition.toUpperCase();
		}
	}

	public int getNumberLinkCount() {
		return numberLinkCount;
	}

	public void setNumberLinkCount(int numberLinkCount) {
		this.numberLinkCount = numberLinkCount;
	}

	public int[] getPageSizeArray() {
		
		int[] result = new int[] { 10, 20, 50, 100 };
        if (pageSizeArray==null||pageSizeArray.length==0)
        {
        	pageSizeArray=result;
        }
		
		return pageSizeArray;
	}

	public void setPageSizeArray(int[] pageSizeArray) {
		this.pageSizeArray = pageSizeArray;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getAjaxUpdateTargetId() {
		return ajaxUpdateTargetId;
	}

	public void setAjaxUpdateTargetId(String ajaxUpdateTargetId) {
		this.ajaxUpdateTargetId = ajaxUpdateTargetId;
	}

	public String getAjaxSuccessFunctionName() {
		return ajaxSuccessFunctionName;
	}

	public void setAjaxSuccessFunctionName(String ajaxSuccessFunctionName) {
		this.ajaxSuccessFunctionName = ajaxSuccessFunctionName;
	}

	public boolean isShowFirstLastLink() {
		return isShowFirstLastLink;
	}

	public void setShowFirstLastLink(boolean isShowFirstLastLink) {
		this.isShowFirstLastLink = isShowFirstLastLink;
	}

	public boolean isShowPrevNextLink() {
		return isShowPrevNextLink;
	}

	public void setShowPrevNextLink(boolean isShowPrevNextLink) {
		this.isShowPrevNextLink = isShowPrevNextLink;
	}

	public boolean isShowNumberLink() {
		return isShowNumberLink;
	}

	public void setShowNumberLink(boolean isShowNumberLink) {
		this.isShowNumberLink = isShowNumberLink;
	}

	public boolean isShowOmitNumberLink() {
		return isShowOmitNumberLink;
	}

	public void setShowOmitNumberLink(boolean isShowOmitNumberLink) {
		this.isShowOmitNumberLink = isShowOmitNumberLink;
	}

	public boolean isShowOnlyPage() {
		return isShowOnlyPage;
	}

	public void setShowOnlyPage(boolean isShowOnlyPage) {
		this.isShowOnlyPage = isShowOnlyPage;
	}

	public boolean isShowNoDataInfo() {
		return isShowNoDataInfo;
	}

	public void setShowNoDataInfo(boolean isShowNoDataInfo) {
		this.isShowNoDataInfo = isShowNoDataInfo;
	}

	public boolean isShowTotalRecord() {
		return isShowTotalRecord;
	}

	public void setShowTotalRecord(boolean isShowTotalRecord) {
		this.isShowTotalRecord = isShowTotalRecord;
	}

	public boolean isShowPageInfo() {
		return isShowPageInfo;
	}

	public void setShowPageInfo(boolean isShowPageInfo) {
		this.isShowPageInfo = isShowPageInfo;
	}

	public boolean isShowGoInput() {
		return isShowGoInput;
	}

	public void setShowGoInput(boolean isShowGoInput) {
		this.isShowGoInput = isShowGoInput;
	}

	public boolean isAjaxPager() {
		return isAjaxPager;
	}

	public void setAjaxPager(boolean isAjaxPager) {
		this.isAjaxPager = isAjaxPager;
	}

	public boolean isEnableSelectPageSize() {
		return enableSelectPageSize;
	}

	public void setEnableSelectPageSize(boolean enableSelectPageSize) {
		this.enableSelectPageSize = enableSelectPageSize;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public int getNumberStartPageIndex() {
		return numberStartPageIndex;
	}

	public void setNumberStartPageIndex(int numberStartPageIndex) {
		this.numberStartPageIndex = numberStartPageIndex;
	}

	public int getNumberEndPageIndex() {
		return numberEndPageIndex;
	}

	public void setNumberEndPageIndex(int numberEndPageIndex) {
		this.numberEndPageIndex = numberEndPageIndex;
	}
	
	public List<Integer> getNumberPageArray() {
		if(numberPageArray==null){
			numberPageArray=new ArrayList<Integer>();
		}
		return numberPageArray;
	}

	
	
	public void InitNumberPageIndex(){
		if(isShowNumberLink){
			
			int totalPages=getTotalPages();
			int pageIndex=getPageIndex();
			
			//总页数少于要显示的页数，页码全部显示
			if(numberLinkCount>totalPages){
				numberStartPageIndex=1;
				numberEndPageIndex=totalPages;
			}else{ //显示指定数量的页码
				int forward=(int)(numberLinkCount/2.0);
				if(pageIndex>forward){//起始页码大于1
					numberEndPageIndex=pageIndex+forward;
					if(numberEndPageIndex>totalPages){
						numberStartPageIndex=totalPages-numberLinkCount+1;
						numberEndPageIndex=totalPages;
					}else{
						numberStartPageIndex=pageIndex-forward;
					}
				}else{ //起始页码从1开始
					numberStartPageIndex=1;
					numberEndPageIndex=numberLinkCount;
				}
			}
			List<Integer> list=new ArrayList<Integer>();
			for (int i =numberStartPageIndex; i <=numberEndPageIndex; i++) {
				list.add(i);
			}
			numberPageArray=list;
		}
	}
}
