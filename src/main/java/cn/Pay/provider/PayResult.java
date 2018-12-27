package cn.Pay.provider;

public class PayResult {
	private boolean isSuccess;
	private double payedMoney;
	private String error;
	private String requestData;
	private boolean isOnline;
	
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public double getPayedMoney() {
		return payedMoney;
	}
	public void setPayedMoney(double payedMoney) {
		this.payedMoney = payedMoney;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	public String getRequestData() {
		return requestData;
	}

	public void setRequestData(String requestData) {
		this.requestData = requestData;
	}
	
	public boolean isOnline() {
		return isOnline;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	@Override
	public String toString() {
		return "PayResult [isSuccess=" + isSuccess + ", payedMoney="
				+ payedMoney + ", error=" + error + ", requestData="
				+ requestData + ", isOnline=" + isOnline + "]";
	}
}
