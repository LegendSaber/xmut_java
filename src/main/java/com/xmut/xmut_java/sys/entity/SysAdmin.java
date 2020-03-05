package com.xmut.xmut_java.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xmut.xmut_java.common.BaseEntity;

public class SysAdmin extends BaseEntity{
	@TableId(value = "id",type = IdType.AUTO)
	private Long id;
	private String name;
	private String password;
	private String roleName;
	private Long isUsed;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public Long getIsUsed() {
		return isUsed;
	}
	public void setIsUsed(Long isUsed) {
		this.isUsed = isUsed;
	}
}
