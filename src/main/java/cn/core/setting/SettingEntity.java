package cn.core.setting;

public class SettingEntity {
	private int id;
	private String key;
	private String setting;
	
	public  SettingEntity() {
	}
	public  SettingEntity(String key) {
		this.key=key;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getSetting() {
		return setting;
	}
	public void setSetting(String setting) {
		this.setting = setting;
	}
}
