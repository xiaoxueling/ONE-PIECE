package cn.Pay.provider.onlinepays.wechatpay;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import cn.util.StringHelper;

import com.github.wxpay.sdk.WXPayConfig;


public class WXPayConfigImpl implements WXPayConfig {

	private byte[] certData;
	private String appId,mchId,key;
	
	public WXPayConfigImpl(String appId,String  mchId,String  key) throws Exception{
		 this(appId,mchId,key,null);
	}
	
    public WXPayConfigImpl(String appId,String  mchId,String  key,String certPath) throws Exception{
    	this.appId=appId;
    	this.mchId=mchId;
    	this.key=key;
  
    	if(!StringHelper.IsNullOrEmpty(certPath)){
    		File file = new File(certPath);
            InputStream certStream = new FileInputStream(file);
            this.certData = new byte[(int) file.length()];
            certStream.read(this.certData);
            certStream.close();
    	}else{
    		certData=new byte[]{};
    	}
    }

    public String getAppID() {
        return this.appId;
    }

    public String getMchID() {
        return this.mchId;
    }


    public String getKey() {
        return this.key;
    }
    
    public InputStream getCertStream() {
        ByteArrayInputStream certBis;
        certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    public int getHttpConnectTimeoutMs() {
        return 2000;
    }

    public int getHttpReadTimeoutMs() {
        return 10000;
    }
}
