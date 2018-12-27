package cn.Pay.provider;


public class PayProviderEntity {
	
		private String name;
		private Boolean online;
		private Class<? extends IPayProvider> classInfo;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Boolean getOnline() {
			return online;
		}
		public void setOnline(Boolean online) {
			this.online = online;
		}
		public Class<? extends IPayProvider> getClassInfo() {
			return classInfo;
		}
		public void setClassInfo(Class<? extends IPayProvider> classInfo) {
			this.classInfo = classInfo;
		}
}
