package cn.Pay.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import cn.Pay.provider.PayExtension;
import cn.Pay.provider.PayParamEntity;
import cn.Pay.provider.PayResult;
import cn.util.DataConvert;
import cn.util.StringHelper;


@Service
@Transactional
public class payService {

	@Autowired
	SqlSessionFactory sqlSessionFactory;
	@Autowired
	SqlSession SqlSession;
	@Autowired
	HttpSession session;
	@Autowired
	PayExtension payExtension;
	
	public Map<String, Object> selPaylog(String orderNo) {
		return SqlSession.selectOne("payDao.selPaylog", orderNo);
	}
	
	/**
	 * 订单支付--根据payLog 进行支付
	 * @param payLogId 支付记录Id
	 * @param payMethodId  支付方式Id
	 * @return
	 */
	public synchronized Map<String,Object> orderPay(int payLogId,int payMethodId){
		
		Map<String, Object> result=new HashMap<String, Object>(){
			{
				put("success", false);
				put("msg","支付失败");
			}
		};
		
		try {
			
			Map<String, Object>paramMap=new HashMap<String,Object>();
			paramMap.put("paylogId", payLogId);
			
			//根据paylogId查询支付记录信息
			Map<String, Object> paymsg =null; //orderService.selectPaylogMsg(paramMap);
			
			if(paymsg==null||paymsg.isEmpty()){
				throw new Exception("获取支付记录失败！");
			}
			
			//判断支付记录是否已经支付
			boolean isPay=DataConvert.ToBoolean(paymsg.get("status"));
			if(isPay){
				result.put("success", true);
				result.put("msg","支付成功！");
				result.put("payResult",new PayResult(){
					{
						setSuccess(true);
						setOnline(false);
					}
				});
				
				return result;
			}
			
			//根据payMethodId 获取 payMethodInfo
			Map<String,Object> paymethod = SqlSession.selectOne("payDao.selectpayMeth", payMethodId);
			if(!"1".equals(paymethod.get("isfreeze")+"")){
				throw new Exception("支付方式不可用!");
			}
			
			//更新 paylog 的 payMethodId 、name
			paymethod.put("paylogId", payLogId);
			paymethod.put("payMethodId", payMethodId);
			int row = SqlSession.update("payDao.updatePaytype", paymethod);
			if(row<=0){
				throw new Exception("更新paylog失败！");
			}
			
			paramMap.put("orderNo", paymsg.get("orderNo")+"");
			
			PayParamEntity payParam = new PayParamEntity();
			payParam.setOrderId(payLogId);
			payParam.setOrderNo(paymsg.get("orderNo")+"");
			payParam.setPayMoney(Double.valueOf(paymsg.get("price")+""));
			payParam.setPayMehtodId(payMethodId);
			payParam.setPayMethodInfo(paymethod);
			
			//支付逻辑方法
			PayResult payResult=payExtension.Pay(payParam);
			result.put("payResult", payResult);
			//支付成功
			if(payResult.isSuccess()){
				if(payResult.isOnline()) {
					result.put("success",true);
					result.put("msg", "请确认支付");
				}else {//线下支付
					Map<String, Object> handelResult = handelPaySuccess(paymsg);
					if(DataConvert.ToBoolean(handelResult.get("result"))){
						result.put("success", true);
						result.put("msg", "支付成功！");
					}else{
						throw new Exception("");
					}
				}
			}
		} catch (Exception e) {
			result.put("msg", "支付失败！"+e.getMessage());
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚
		}
		
		return result;
	}
	
	
	/**
	 * 支付成功逻辑处理--根据支付记录分类型进行订单处理
	 * @param payLog
	 * @return result--true/false, model--支付的订单信息
	 * @throws Exception 
	 */
	public  Map<String, Object> handelPaySuccess(Map<String,Object> payLog) throws Exception {
		
		Map<String, Object> result=new HashMap<String, Object>(){
			{
				put("result", false);
			}
		};
		
		try {
			//相应记录
			Map<String, Object> sourceInfoMap=new HashMap<String, Object>();
			
			int source=DataConvert.ToInteger(payLog.get("source"));
			int sourceId=DataConvert.ToInteger(payLog.get("sourceId"));
			String orderNo=DataConvert.ToString(payLog.get("orderNo"));
			
			boolean flag=false;
			
			//根据 source 来分别处理
			 switch (source) {
			 	case 1://订单支付
			 		
			 		break;
			 	case 2://充值
			 		
					break;
			 }
			 result.put("result", true);
			 result.put("model", sourceInfoMap);
			 
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚
			throw e;
		}
		return result;
	}


	/**
	 * 获取支付状态
	 */
	public  Map<String, Object> getPayState(String orderNo){
		Map<String, Object> result=new HashMap<String, Object>(){
			{
				put("result", false);
			}
		};
		try {
			
			Map<String,Object> payLogMap =selPaylog(orderNo);
			if(payLogMap==null||payLogMap.isEmpty()) {
				throw new Exception("没有对应的支付记录信息！");
			}
			boolean payStatus=DataConvert.ToBoolean(payLogMap.get("status"));
			if(!payStatus) {
				throw new Exception("");
			}
			
			Map<String, Object> sourceInfoMap=new HashMap<String, Object>();
			
			int source=DataConvert.ToInteger(payLogMap.get("source"));
			int sourceId=DataConvert.ToInteger(payLogMap.get("sourceId"));
			
			boolean flag=false;
			
			//根据 source 来查看
			 switch (source) {
			 	case 1://订单支付
			 		
			 		break;
			 	case 2://充值
			 		
					break;
			 }
			 result.put("result", flag);
			 result.put("model", sourceInfoMap);
		} catch (Exception e) {
			result.put("result", false);
			result.put("msg", e.getMessage());
		}
		
		return result;
	}
		
	
}
