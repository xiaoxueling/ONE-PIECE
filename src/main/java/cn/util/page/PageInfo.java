package cn.util.page;

public class PageInfo {

	private int totalRecord;//记录总条数
	private int pageSize=20;//每页显示记录数
	private int pageIndex;//当前页码
	
	public PageInfo(){
		
	}
	
	public PageInfo(int totalRecord,int pageIndex,int pageSize){
		this.totalRecord=totalRecord;
		this.pageIndex=pageIndex;
		this.pageSize=pageSize;
	}
	
	public int getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}
	public int getPageSize() {
		if(pageSize<1){
			pageSize=1;
		}
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageIndex() {
		if(pageIndex<1){
			pageIndex=1;
		}
		if(pageIndex>getTotalPages()){
			pageIndex=getTotalPages();
		}
		return pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	
	public int getTotalPages(){
		try {
			return (int) (getTotalRecord() / getPageSize())+((getTotalRecord() % pageSize == 0) ? 0:1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
}
