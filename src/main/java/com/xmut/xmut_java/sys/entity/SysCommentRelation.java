package com.xmut.xmut_java.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xmut.xmut_java.common.BaseEntity;

public class SysCommentRelation extends BaseEntity{
	@TableId(value="id", type=IdType.AUTO)
	private Long id;
	private Long fatherId;
	private Long sonId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getFatherId() {
		return fatherId;
	}
	public void setFatherId(Long fatherId) {
		this.fatherId = fatherId;
	}
	public Long getSonId() {
		return sonId;
	}
	public void setSonId(Long sonId) {
		this.sonId = sonId;
	}
}
