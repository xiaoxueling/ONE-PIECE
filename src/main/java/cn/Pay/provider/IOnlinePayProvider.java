package cn.Pay.provider;

import java.util.Map;

public interface IOnlinePayProvider extends IPayProvider {

	OnlinePayNotifyResult VerifyResult(PayParamEntity entity,Map<String,String> param);
    OnlinePayNotifyResult VerifyRefundResult(PayParamEntity entity,Map<String,String>param);
    
}
