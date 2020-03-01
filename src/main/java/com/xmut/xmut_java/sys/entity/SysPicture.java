package com.xmut.xmut_java.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xmut.xmut_java.common.BaseEntity;

public class SysPicture extends BaseEntity{
	@TableId(value="id", type=IdType.AUTO)
	private Long id;
	private String pictureName;
	private byte[] pictureContent;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPictureName() {
		return pictureName;
	}
	public void setPictureName(String pictureName) {
		this.pictureName = pictureName;
	}
	public byte[] getPictureContent() {
		return pictureContent;
	}
	public void setPictureContent(byte[] pictureContent) {
		this.pictureContent = pictureContent;
	}
}
