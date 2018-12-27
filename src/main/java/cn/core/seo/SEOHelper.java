package cn.core.seo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.core.setting.Setting;
import cn.core.setting.model.SiteInfo;
import cn.util.StringHelper;

@Component
public class SEOHelper {
	
	static SEOContext seo;
	static int count;
	static Setting setting;
	
	@Autowired
	public SEOHelper(Setting setting) {
		this.setting=setting;
	}
	
	public static void Seo(SEOContext context){

		if(context==null){
			context=new SEOContext();
		}
		
		SiteInfo siteInfo=setting.getSetting(SiteInfo.class);
		if(StringHelper.IsNullOrEmpty(context.getTitle())){
			context.setTitle(siteInfo.getSiteTitle());
		}
		if(StringHelper.IsNullOrEmpty(context.getMetaKeywords())){
			context.setMetaKeywords(siteInfo.getMetaKeywords());
		}
		if(StringHelper.IsNullOrEmpty(context.getMetaDescription())){
			context.setMetaDescription(siteInfo.getMetaDescription());
		}
		count=0;
		seo=context;
	}
	
	public  SEOContext getSEO() {
		if(count>0||seo==null){
			Seo(null);
		}else{
			count++;
		}
		return seo;
	}
}
