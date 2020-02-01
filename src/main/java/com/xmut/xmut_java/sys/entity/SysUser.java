package com.xmut.xmut_java.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xmut.xmut_java.common.BaseEntity;

public class SysUser extends BaseEntity{
	@TableId(value = "id",type = IdType.AUTO)
	private Long id;
	private String username;
	private String password;
	private Long flag;
	private Long score;
	private Long isSign;
	private Long signDay;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Long getFlag() {
		return flag;
	}
	public void setFlag(Long flag) {
		this.flag = flag;
	}
	public Long getScore() {
		return score;
	}
	public void setScore(Long score) {
		this.score = score;
	}
	public Long getIsSign() {
		return this.isSign;
	}
	public void setIsSign (Long isSign) {
		this.isSign = isSign;
	}
	public Long getSignDay() {
		return this.signDay;
	}
	public void setSignDay(Long signDay) {
		this.signDay = signDay;
	}
}
