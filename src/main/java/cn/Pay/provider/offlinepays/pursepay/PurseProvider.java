package cn.Pay.provider.offlinepays.pursepay;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import cn.Pay.provider.IPayProvider;
import cn.Pay.provider.Pay.PayType;
import cn.Pay.provider.paycallback.IPayCallBack;
import cn.Pay.provider.PayParamEntity;
import cn.Pay.provider.Pay;
import cn.Pay.provider.PayResult;
import cn.util.DataConvert;

@Pay(name="账户余额支付",type=PayType.Offline)
@Service
public class PurseProvider implements IPayProvider {

	@Autowired
	SqlSession sqlSession;
	
	@Override
	public PayResult Pay(PayParamEntity param) {

		PayResult result = new PayResult() {
			{
				setError("账户余额支付失败！");
				setSuccess(false);
			}
		};
		
		try {

			int orderId = param.getOrderId();// 订单id
			
			// 查询账户余额和用户id
			Map userInfo = sqlSession.selectOne("productDao.selUserBalance",orderId);
			double balance = 0;
			String userId = "";
			if(userInfo==null){
				result.setSuccess(false);
				result.setError("该账户不存在！");
				return result;
			}else{
				balance = Double.valueOf(userInfo.get("balance") + "");// 余额
				userId = userInfo.get("userId") + "";// 用户id
			}

			// 需要支付价格
			double price = param.getPayMoney();
			if (balance > price) {// 账户余额足够支付
				
				boolean flag=false;
				
				Map debit = new HashMap();
				debit.put("userId", userId);
				debit.put("price", price);
				// 扣除账户余额的金额
				flag=sqlSession.update("productDao.updUserBalance", debit)>0;
				if(!flag){
					throw new Exception();
				}
				//判断
				int source = DataConvert.ToInteger(userInfo.get("source"));
				int type = 0;
				if(source==1){
					type = 3;
				}else if(source==2){
					type = 1;
				}else if(source==3){
					type = 2;
				}else if(source==4){
					type = 4;
				}else if(source==5){
					type = 5;
				}
				debit.put("type", type);
				debit.put("orderNum", param.getOrderNo());

				Calendar cal = Calendar.getInstance();
				int month = cal.get(Calendar.MONTH) + 1;// 当前月份
				debit.put("month", month);
				//账户剩余余额
				debit.put("balance", balance-price);
				// 添加用户消费记录表
				flag=sqlSession.insert("productDao.addUseraccountlog", debit)>0;

				if(!flag){
					throw new Exception();
				}
				// 修改支付状态
				Map<String,Object> ma = new HashMap<String, Object>();
				ma.put("status", 1);
				ma.put("id", orderId);
				flag=sqlSession.update("productDao.updPayLogStatus", ma)>0;
				if(!flag){
					throw new Exception();
				}
				result.setSuccess(true);
				result.setError("");
			} else {
				result.setSuccess(false);
				result.setError("余额不足");
			}
			
		} catch (Exception ex) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();// 手动回滚
		} finally {
			IPayCallBack callBack = param.getCallBack();
			if (callBack != null) {
				callBack.OrderPay(param, result);
			}
		}
		return result;

	}
	
	
}
