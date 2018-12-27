package cn.core.setting.model;

/**
 * 微信用户增减数据
 * @author xiaoxueling
 *
 */
public class WechatUserSummary {
	private String date;//日期
	private Integer newUser;//新增的用户数量
	private Integer cancelUser;//取消关注的用户数量
	private Integer cumulateUser;//总用户量
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Integer getNewUser() {
		return newUser;
	}
	public void setNewUser(Integer newUser) {
		this.newUser = newUser;
	}
	public Integer getCancelUser() {
		return cancelUser;
	}
	public void setCancelUser(Integer cancelUser) {
		this.cancelUser = cancelUser;
	}
	public Integer getCumulateUser() {
		return cumulateUser;
	}
	public void setCumulateUser(Integer cumulateUser) {
		this.cumulateUser = cumulateUser;
	}
}
