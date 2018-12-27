package cn.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;

import cn.core.master.CustomResponse;
import cn.util.DataConvert;
import cn.util.page.PageConfig;
import cn.util.page.PageConfig.PagerElementPosition;
import cn.util.page.PageInfo;


public class PageTag extends BodyTagSupport implements DynamicAttributes{
	private static final long serialVersionUID = 1L;
	private final String pageFolderPath = "/WEB-INF/component/";	
	private final String pageSuffix = ".jsp";
	private Map<String, String> map = new HashMap<String, String>();
	private String tempName="page";
	
	@Override
    public void doInitBody() throws JspException {
        try {
            this.pageContext.getRequest().setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        super.doInitBody();
    }
 
    @Override
    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }
 
    @Override
    public int doStartTag() throws JspException {
        //执行子标签
        return EVAL_BODY_BUFFERED;
    }
 
    @Override
    public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		CustomResponse bufferedResponse = new CustomResponse((HttpServletResponse) this.pageContext.getResponse());
		
		ServletRequest request= this.pageContext.getRequest();
		
		request.setAttribute("Model",getPageConfig());
		try {
			// 渲染页面
			this.pageContext.getServletContext().getRequestDispatcher(this.getPageUrl()).include(this.pageContext.getRequest(), bufferedResponse);
			// out.clearBuffer();
			out.write(bufferedResponse.getContent());
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return EVAL_BODY_INCLUDE;
	}

	private String getPageUrl() {
		return this.pageFolderPath + this.tempName + this.pageSuffix;
	}
	
	public String getTempName() {
		return tempName;
	}
	
	public void setTempName(String tempName) {
		this.tempName = tempName;
	}

	private PageConfig getPageConfig(){
		PageConfig config=new PageConfig(){
			{
				setCurrentIndex(currentIndex);
				setUrl(url);
				setPageParameter(pageParameter);
				setPageInfoPosition(pageInfoPosition);
				setClassName(className);
				setShowFirstLastLink(isShowFirstLastLink);
				setShowPrevNextLink(isShowPrevNextLink);
				setShowNumberLink(isShowNumberLink);
				setNumberLinkCount(numberLinkCount);
				setShowOmitNumberLink(isShowOmitNumberLink);
				setShowOnlyPage(isShowOnlyPage);
				setShowNoDataInfo(isShowNoDataInfo);
				setShowTotalRecord(isShowTotalRecord);
				setShowPageInfo(isShowPageInfo);
				setShowGoInput(isShowGoInput);
				setAjaxPager(isAjaxPager);
				setAjaxUpdateTargetId(ajaxUpdateTargetId);
				setAjaxSuccessFunctionName(ajaxSuccessFunctionName);
				setEnableSelectPageSize(enableSelectPageSize);
				setPageSizeArray(pageSizeArray);
				setPageSize(getPage().getPageSize());
				setPageIndex(getPage().getPageIndex());
				setTotalRecord(getPage().getTotalRecord());
			}
		};
		config.InitNumberPageIndex();
		return config;
	}
	private PageInfo page;
	private int currentIndex=100;//当前page索引（应用于同一页面多个pager时）
	private String url;//url 地址
	private String pageParameter="page";//当前页码的参数名(默认 page)
	private String pageInfoPosition=PagerElementPosition.Left.toString();//当前页和总页数信息显示位置 Left/Right
	private String className="pager";//class样式名称
	private boolean isShowFirstLastLink=true;//是否显示首页、尾页链接 
	private boolean isShowPrevNextLink=true;//是否显示上一页、下一页链接
	private boolean isShowNumberLink=true;//是否显示数字按钮
	private int numberLinkCount=5;//数字按钮数量
	private boolean isShowOmitNumberLink=true;//是否显示省略的数字按钮
	private boolean isShowOnlyPage=true;// 当总页数小于2页时是否显示
	private boolean isShowNoDataInfo=true;// 是否显示无数据提示信息
	private boolean isShowTotalRecord;// 是否显示总记录数
	private boolean isShowPageInfo;// 是否显示当前页数和总页数信息
	private boolean isShowGoInput=true;// 是否显示GoTo输入区域  
	private boolean isAjaxPager;// 是否创建为ajax分页控件
	private String ajaxUpdateTargetId;//  Ajax提交后更新的html元素id
	private String ajaxSuccessFunctionName;//  Ajax提交后调用的js function名称
	private boolean enableSelectPageSize;// 是否可以更改每页数量
	private int[] pageSizeArray=new int[]{10, 20, 50, 100};// 每页数量数据
	
	public PageInfo getPage() {
		if(page==null){
			page=new PageInfo();
		}
		return page;
	}

	public void setPage(PageInfo page) {
		if(page!=null){
			this.page = page;
		}
	}

	public int getCurrentIndex() {
		return currentIndex;
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
		this.pageInfoPosition = pageInfoPosition;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
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

	public int getNumberLinkCount() {
		return numberLinkCount;
	}

	public void setNumberLinkCount(int numberLinkCount) {
		this.numberLinkCount = numberLinkCount;
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

	public boolean isEnableSelectPageSize() {
		return enableSelectPageSize;
	}

	public void setEnableSelectPageSize(boolean enableSelectPageSize) {
		this.enableSelectPageSize = enableSelectPageSize;
	}

	public int[] getPageSizeArray() {
		return pageSizeArray;
	}

	public void setPageSizeArray(int[] pageSizeArray) {
		this.pageSizeArray = pageSizeArray;
	}

	public String getPageFolderPath() {
		return pageFolderPath;
	}

	@Override
	public void setDynamicAttribute(String uri, String key, Object value){
		try {
			map.put(key,DataConvert.ToString(value));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
