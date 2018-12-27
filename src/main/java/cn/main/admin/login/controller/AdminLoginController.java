package cn.main.admin.login.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


import cn.core.setting.Setting;
import cn.core.setting.model.SiteInfo;

@Controller
@RequestMapping("/admin")
public class AdminLoginController {

	@Autowired
	HttpSession session;
	@Autowired
	Setting setting;
	
	@RequestMapping(value= {"","/login"})
	public ModelAndView login(@RequestParam Map<String, Object> map) {
		SiteInfo siteInfo = setting.getSetting(SiteInfo.class);
		map.put("siteInfo", siteInfo);
		if (session.getAttribute("loginUser") != null
				&& !StringUtils.isEmpty(session.getAttribute("loginUser").toString())) {
			return new ModelAndView("redirect:/admin/index");
		}
		return new ModelAndView("/admin/login/index", map);
	}
}
