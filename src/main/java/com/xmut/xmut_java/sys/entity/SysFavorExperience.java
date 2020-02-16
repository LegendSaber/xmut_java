package com.xmut.xmut_java.sys.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xmut.xmut_java.common.BaseEntity;

public class SysFavorExperience extends BaseEntity{
	@TableId(value="id", type=IdType.AUTO)
	private Long id;
	private Long userId;
	private Long experienceId;
	private Date modifyTime;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getExperienceId() {
		return experienceId;
	}
	public void setExperienceId(Long experienceId) {
		this.experienceId = experienceId;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
}	
