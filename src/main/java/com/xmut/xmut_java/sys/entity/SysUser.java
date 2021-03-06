package com.xmut.xmut_java.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xmut.xmut_java.common.BaseEntity;

public class SysUser extends BaseEntity{
	@TableId(value = "id",type = IdType.AUTO)
	private Long id;
	private String username;
	private String password;
	private String roleName;
	private Long score;
	private Long signDay;
	private Long isUsed;
	private Long picNo;
	
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
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public Long getScore() {
		return score;
	}
	public void setScore(Long score) {
		this.score = score;
	}
	public Long getSignDay() {
		return this.signDay;
	}
	public void setSignDay(Long signDay) {
		this.signDay = signDay;
	}
	public Long getIsUsed() {
		return isUsed;
	}
	public void setIsUsed(Long isUsed) {
		this.isUsed = isUsed;
	}
	public Long getPicNo() {
		return picNo;
	}
	public void setPicNo(Long picNo) {
		this.picNo = picNo;
	}
}
