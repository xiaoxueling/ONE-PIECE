package cn.main.api.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.core.interceptor.Sign;
import cn.core.setting.Setting;
import cn.core.setting.model.SiteInfo;
import cn.main.api.service.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

//@Sign
@RestController
@RequestMapping("/api/setting")
@Api(value="配置相关操作",tags={"配置相关操作"})
public class SettingController {

	@Autowired
	Setting setting;
	
	@ApiOperation(value="获取系统参数配置",httpMethod="GET")
	@ApiImplicitParams(value={
			@ApiImplicitParam(name="userId",value="用户Id",dataType="int",paramType="query",defaultValue="006"),
	})
	@ApiResponse(code=200,message="{'result': 1,'msg': '获取数据成功!', 'data': siteInfo")
	@RequestMapping("/getSiteInfo")
	public Map<String, Object>getSiteInfo(Integer userId){
		ApiResult result=new ApiResult();
		
		SiteInfo siteInfo=setting.getSetting(SiteInfo.class);
		
		result.setResult(true, "获取数据成功！", siteInfo);
		
		return result.getResult();
	}
	
}
