package cn.Pay.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.Pay.provider.IOnlinePayProvider;
import cn.Pay.provider.OnlinePayNotifyResult;
import cn.Pay.provider.PayParamEntity;
import cn.Pay.provider.onlinepays.alipay.AlipayProvider;
import cn.Pay.provider.onlinepays.wechatpay.WechatProvider;
import cn.Pay.service.PaymethodService;
import cn.Pay.service.payService;
import cn.util.DataConvert;

import com.github.wxpay.sdk.WXPayUtil;

/**
 * 支付通知
 * @author xiaoxueling
 *
 */
@Controller
@RequestMapping("/pay")
public class PayController {
	
	@Autowired
	AlipayProvider alipayProvider;
	@Autowired
	WechatProvider wechatProvider;
	@Autowired
	payService payService;
	@Autowired
	PaymethodService paymethodService;
	
	/**
	 * 支付宝pc支付返回
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/result/alipayPay")
	public ModelAndView alipayPayResult(HttpServletRequest request){
		ModelAndView result=new ModelAndView();
		
		try {
			
			Map<String,String>params=getParam(request);
			 
			String orderNo=params.get("out_trade_no");
			
			Map<String, Object> handleResult=handleInternal(orderNo,params, false,alipayProvider);
			 
			result=(ModelAndView)(handleResult.get("ModelAndView"));
			
		} catch (Exception e) {
			e.printStackTrace();
			
			result.setViewName("/pay/web/payError");
			result.addObject("msg", e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * 支付宝手机支付返回
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/result/alipayWapPay")
	public ModelAndView alipayWebPayResult(HttpServletRequest request){
		
		ModelAndView result=new ModelAndView();
		
		try {
			
			Map<String,String>params=getParam(request);
			 
			String orderNo=params.get("out_trade_no");
			
			Map<String, Object> handleResult=handleInternal(orderNo,params, true,alipayProvider);
			 
			result=(ModelAndView)(handleResult.get("ModelAndView"));
			
		} catch (Exception e) {
			e.printStackTrace();
			
			result.setViewName("/pay/phone/payError");
			result.addObject("msg", e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * 支付宝异步通知
	 */
	@RequestMapping(value={"/notify/alipayPay","/notify/alipayWapPay","/notify/alipayAppPay"})
	public @ResponseBody String alipayPayNotify(HttpServletRequest request){
		String result="";
		
		 try {
			 
			 Map<String,String>params=getParam(request);
			 
			 String orderNo=params.get("out_trade_no");
			
			 Map<String, Object> handleResult=handleInternal(orderNo,params, false,alipayProvider);
			 
			 result=DataConvert.ToString(handleResult.get("msg"));
			 
			 
		} catch (Exception e) {
			e.printStackTrace();
			result= "error";
		}
		return result;
	}
	
	/**
	 * 支付异步通知逻辑处理
	 * @param orderNo 订单号
	 * @param data 接受的数据
	 * @param isMobile 是否手机端
	 * @param provider IOnlinePayProvider
	 * @return
	 */
	 private synchronized Map<String, Object> handleInternal(String orderNo,Map<String, String> data,boolean isMobile,IOnlinePayProvider provider){
		 
		 Map<String, Object> result=new HashMap<String, Object>(){
			 {
				 	put("msg", "error");
			 }
		 };
		 
		 ModelAndView modelAndView=new ModelAndView();
		 String viewName=isMobile? "/pay/phone/orderPayResult":"/pay/web/orderPayResult";
		 modelAndView.setViewName(viewName);
		 modelAndView.addObject("success", false);
		 modelAndView.addObject("msg", "查询支付状态异常,请到我的订单中查看！");
		 
		 int payLogStatus=0;
		 int payLogId=0;
		 String tradeNo="";
		 try {
			 
			 Map<String, Object> payLogMap=new HashMap<String, Object>();
			 
			 //根据 orderNo 从 payLog 表中取出对应的记录
			 payLogMap = payService.selPaylog(orderNo);
				 
			 if(payLogMap==null||payLogMap.isEmpty()){
				 throw new Exception("没有对应的支付记录！");
			 }
			 
			 payLogStatus=DataConvert.ToInteger(payLogMap.get("status"));
			 payLogId=DataConvert.ToInteger(payLogMap.get("id"));
			 
			 int source=DataConvert.ToInteger(payLogMap.get("source"));
			 int sourceId=DataConvert.ToInteger(payLogMap.get("sourceId"));
			 int payMethodId=DataConvert.ToInteger(payLogMap.get("payMethodId"));
			
			 if(payMethodId<=0) {
				 throw new Exception("没有对应的支付方式！");
			 }
				
			 Map<String,Object> paramMap = new HashMap<String,Object>();
			 paramMap.put("id", payMethodId);
			 Map<String,Object> payMethodInfo = paymethodService.findByIdPayment(paramMap);
			 if(payMethodInfo==null){
				 throw new Exception("没有对应的支付方式！");
			 }
			 
			 //支付验证
			 PayParamEntity entity=new PayParamEntity();
			 entity.setOrderId(sourceId);
			 entity.setOrderNo(orderNo);
			 entity.setPayMehtodId(payMethodId);
			 entity.setPayMoney(DataConvert.ToDouble(payLogMap.get("price")));
			 entity.setPayMethodInfo(payMethodInfo);
			 
			 OnlinePayNotifyResult notifyResult=provider.VerifyResult(entity,data);
			 result.put("msg", notifyResult.getMessage());
			 if(!notifyResult.isSuccess()){
				 throw new Exception(notifyResult.getMessage());
			 }
			 payLogStatus=1;
			 tradeNo=notifyResult.getTradeNo();
			 
			 Map<String, Object> sourceInfoMap=new HashMap<String, Object>();
			 sourceInfoMap.put("type", source);
			 
			 Map<String,Object> handelResult =payService.handelPaySuccess(payLogMap);
			 
			 if(DataConvert.ToBoolean(handelResult.get("result"))) {
				 
				 modelAndView.addObject("success", true);
				 modelAndView.addObject("model",sourceInfoMap.get("model"));
				 modelAndView.addObject("msg", "支付成功！");
			 }
			 
		} catch (Exception e) {
			e.printStackTrace();
			modelAndView.setViewName(isMobile? "/pay/phone/payError":"/pay/web/payError");
			modelAndView.addObject("msg", "支付失败！("+e.getMessage()+")");
			if(payLogStatus!=1){
				payLogStatus=2;
			}
		}
		finally{
			 
			 try {
				 //更新 payLog 支付记录 status tradeNo
				 Map<String, Object>paramMap=new HashMap<String,Object>();
				 paramMap.put("id",payLogId);
				 paramMap.put("status",payLogStatus);
				 paramMap.put("tradeNo", tradeNo);
				 
				} catch (Exception e) {
					e.printStackTrace();
				}
		 }
		 
		result.put("ModelAndView",modelAndView); 
		return result;
	 }
	
	
	/**
	 * 微信支付结果展示
	 * @return
	 */
	@RequestMapping("/result/wechatPay")
	public ModelAndView wechatPayResult(String orderNo){
		
		ModelAndView modelAndView= new  ModelAndView();
		String viewName="/pay/phone/orderPayResult";
		
		try {
			
			Map<String, Object> result=payService.getPayState(orderNo);
		
			if(DataConvert.ToBoolean(result.get("result"))){
				modelAndView.addObject("success", true);
				modelAndView.addAllObjects(result);
			}else {
				throw new Exception(result.get("msg")+"");
			}
			
		} catch (Exception e) {
			 viewName="/pay/phone/payError";
			 modelAndView.addObject("msg","支付失败！"+e.getMessage());
		}
		modelAndView.setViewName(viewName);
		return modelAndView;
	}
	
	/**
	 * 获取支付状态
	 * @param orderNo
	 * @return
	 */
	@RequestMapping("/payState")
	public @ResponseBody Map<String,Object> getOrderPayState(String orderNo){
		
		Map<String,Object> resultMap=new HashMap<String, Object>(){
			{
				put("result", false);
			}
		};
		
		try {
			
			Map<String, Object> result=payService.getPayState(orderNo);
			resultMap.put("result", result.get("result"));
			
		} catch (Exception e) {
			resultMap.put("result", false);
		}
		
		return resultMap;
	}
	
	/**
	 * 微信支付异步通知
	 */
	@RequestMapping("/notify/wechatPay")
	public @ResponseBody String wechatPayNotify(HttpServletRequest request){
		
		String result="";
		try {

			request.setCharacterEncoding("UTF-8");

			String line = "";
			String xmlString = null;
			StringBuffer inputString = new StringBuffer();

			while ((line = request.getReader().readLine()) != null) {
				inputString.append(line);
			}
			xmlString = inputString.toString();
			request.getReader().close();
			
			Map<String, String> dataMap = WXPayUtil.xmlToMap(xmlString);

			String orderNo = dataMap.get("out_trade_no");
			Map<String, Object> handleResult=handleInternal(orderNo,dataMap, false,wechatProvider);
			result=DataConvert.ToString(handleResult.get("msg"));
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}
	
	/**
	 * 获取请求的参数
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private Map<String,String>getParam(HttpServletRequest request) throws Exception{
		request.setCharacterEncoding("UTF-8"); 
		
		Map<String,String> params = new HashMap<String,String>();
		
		Map<String,String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		
		return params;
	}
}
