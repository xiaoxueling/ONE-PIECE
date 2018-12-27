package cn.main.admin.home.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import cn.core.setting.Setting;
import cn.core.setting.model.SiteInfo;
import cn.main.admin.home.service.AdminHomeService;
import cn.util.DataConvert;
import cn.util.page.PageInfo;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {

	@Autowired
	Setting setting;
	@Autowired
	AdminHomeService adminHomeService;
	
	@RequestMapping("/index")
	public ModelAndView index(@RequestParam Map<String, Object> paramMap) {
		
		Map<String, Object> model=new HashMap<String,Object>();
		
		SiteInfo siteInfo=setting.getSetting(SiteInfo.class);
		
		model.put("siteInfo", siteInfo);
		
		List<Map<String, Object>>userList=adminHomeService.getUserList();
		
		model.put("userList",userList);
		
		PageInfo pageInfo=new PageInfo(100,DataConvert.ToInteger(paramMap.get("page")),DataConvert.ToInteger(paramMap.get("pageSize")));
		
		model.put("pageInfo", pageInfo);
		
		return new ModelAndView("/admin/home/index",model);
	}
	
}
