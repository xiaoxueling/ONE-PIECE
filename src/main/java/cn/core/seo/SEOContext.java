package cn.core.seo;

public class SEOContext {

	private String title="";
	private String metaKeywords="";
	private String metaDescription="";
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMetaKeywords() {
		return metaKeywords;
	}
	public void setMetaKeywords(String metaKeywords) {
		this.metaKeywords = metaKeywords;
	}
	public String getMetaDescription() {
		return metaDescription;
	}
	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}
	
	@Override
	public String toString(){
		return this.metaDescription+this.metaKeywords+this.title;
	}
}
