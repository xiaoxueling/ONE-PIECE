package cn.core.interceptor;

import cn.core.jwt.JwtUtil;
import cn.main.api.service.ApiResult;
import cn.util.DataConvert;
import cn.util.StringHelper;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: baoxuechao
 * @Date: 2019/2/12 14:26
 * @Description: 身份认证拦截器
 */
public class JwtInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("utf-8");
        String token = request.getHeader("accessToken");
        //验证token是否存在
        if(!StringHelper.IsNullOrEmpty(token)){
            //验证token是否正确
            boolean flag = JwtUtil.verify(token);
            if(flag){
                return true;
            }
            String uri = request.getRequestURI();
            //验证是否拥有该访问权限
            boolean verification = Verification(token,uri);
            if(verification){
                return true;
            }
        }
        ApiResult apiResult = new ApiResult(false,"对不起,您不具体该访问权限!");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(JSONObject.toJSONString(apiResult));
        return false;
    }

    /**
     * TODO 验证权限
     * @param token
     * @return true/false 存在/不存在
     */
    public boolean Verification(String token,String uri){
        DecodedJWT jwt = JWT.decode(token);
        //TODO 当前角色所拥有的权限
        List<Map> list = new ArrayList<Map>();
        //TODO 当前请求所需要的权限
        List<Map> urilist = new ArrayList<Map>();
        for (Map it:urilist){
            for (Map item:list){
                if(DataConvert.ToString(it.get("AuthorityID")).equals(DataConvert.ToString(item.get("quanxian")))){
                    return true;
                }
            }
        }
        return false;
    }


}
