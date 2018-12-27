package cn.Pay.provider.paycallback;

import org.springframework.stereotype.Service;

import cn.Pay.provider.PayParamEntity;
import cn.Pay.provider.PayResult;
import cn.util.CalendarUntil;


@Service
public class OrderPayCallBack implements IPayCallBack  {


	@Override
	public synchronized void OrderPay(PayParamEntity paramEntity, PayResult payResult) {
		try {
			// TODO  支付完成，后续操作

			System.out.println("支付回调:"+payResult.toString()+",Time:"+CalendarUntil.ToDateString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void OrderRefund(PayParamEntity paramEntity, PayResult payResult) {
		try {
			// TODO 退款完成，后续操作
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
