package cn.Pay.provider.offlinepays.cardpay;

import org.springframework.stereotype.Service;

import cn.Pay.provider.IPayProvider;
import cn.Pay.provider.Pay;
import cn.Pay.provider.PayParamEntity;
import cn.Pay.provider.PayResult;
import cn.Pay.provider.Pay.PayType;

@Pay(name="充值卡支付",type=PayType.Offline)
@Service
public class CardProvider implements IPayProvider {

	@Override
	public PayResult Pay(PayParamEntity param) {
		return new PayResult(){
			{
				setError("充值卡支付");
			}
		};
	}

}
