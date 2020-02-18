package com.xmut.xmut_java.sys.entity;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xmut.xmut_java.common.BaseEntity;

public class SysComment extends BaseEntity{
	@TableId(value="id", type=IdType.AUTO)
	private Long id;
	private String author;
	private String content;
	private Date createTime;
	private Long favorNum;
	private List<SysComment> sonComment;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Long getFavorNum() {
		return favorNum;
	}
	public void setFavorNum(Long favorNum) {
		this.favorNum = favorNum;
	}
	public List<SysComment> getSonComment() {
		return sonComment;
	}
	public void setSonComment(List<SysComment> sonComment) {
		this.sonComment = sonComment;
	}
}
