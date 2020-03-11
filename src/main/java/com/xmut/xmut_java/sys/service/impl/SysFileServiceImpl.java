package com.xmut.xmut_java.sys.service.impl;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xmut.xmut_java.sys.entity.SysAvatar;
import com.xmut.xmut_java.sys.entity.SysFile;
import com.xmut.xmut_java.sys.entity.SysKnowledgePicture;
import com.xmut.xmut_java.sys.entity.SysPicture;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.entity.SysUserFile;
import com.xmut.xmut_java.sys.mapper.SysAvatarMapper;
import com.xmut.xmut_java.sys.mapper.SysFileMapper;
import com.xmut.xmut_java.sys.mapper.SysKnowledgePictureMapper;
import com.xmut.xmut_java.sys.mapper.SysPictureMapper;
import com.xmut.xmut_java.sys.mapper.SysUserFileMapper;
import com.xmut.xmut_java.sys.service.SysFileService;

@Service
public class SysFileServiceImpl implements SysFileService{
	@Autowired
	private SysFileMapper sysFileMapper;
	
	@Autowired
	private SysUserFileMapper sysUserFileMapper;
	
	@Autowired
	private SysPictureMapper sysPictureMapper;
	
	@Autowired
	private SysKnowledgePictureMapper sysKnowledgePictureMapper;
	
	@Autowired
	private SysAvatarMapper sysAvatarMapper;
	
	public void saveFile(String fileName, byte[] fileContent, SysUser currentUser) {
		SysFile file = new SysFile();
		
		file.setAuthor(currentUser.getUsername());
		file.setFileName(fileName);
		file.setFileContent(fileContent);
		file.setCreateTime(new Date());
		file.setFavorNum((long)0);
		sysFileMapper.insert(file);
		
		SysUserFile userFileParams = new SysUserFile();
		userFileParams.setUserId(currentUser.getId());
		userFileParams.setFileId(file.getId());
		sysUserFileMapper.insert(userFileParams);
	}
	
	public SysFile getFile(Long id) {
		SysFile file = null;
		SysFile queryFile = new SysFile();
		
		queryFile.setId(id);
		file = sysFileMapper.selectOne(new QueryWrapper<SysFile>(queryFile));
		
		return file;
	}
	
	public void savePicture(String pictureName, byte[] pictureContent, Long id) {
		SysPicture picture = new SysPicture();
		SysKnowledgePicture knowledgePicture = new SysKnowledgePicture();
		
		picture.setPictureName(pictureName);
		picture.setPictureContent(pictureContent);
		sysPictureMapper.insert(picture);
		
		knowledgePicture.setKnowledgeId(id);
		knowledgePicture.setPictureId(picture.getId());
		sysKnowledgePictureMapper.insert(knowledgePicture);
	}
	
	public SysPicture getPicture(Long id) {
		SysPicture picture = null;
		SysPicture queryPicture = new SysPicture();
		
		queryPicture.setId(id);
		picture = sysPictureMapper.selectOne(new QueryWrapper<SysPicture>(queryPicture));
		
		return picture;
	}
	
	public Long saveAvatar(String fileName, byte[] fileContent, Long picNo) {
		Long result = (long)-1;
		SysAvatar avatar = new SysAvatar();
		
		avatar.setName(fileName);
		avatar.setContent(fileContent);
		if (picNo == -1) {
			sysAvatarMapper.insert(avatar);
			result = avatar.getId();
		} else {
			sysAvatarMapper.update(avatar, new UpdateWrapper<SysAvatar>().eq("id", picNo));
			result = picNo;
		}
		
		return result;
	}
	
	public byte[] getAvatar(Long avatarId) {
		SysAvatar params = new  SysAvatar();
		
		params.setId(avatarId);
		SysAvatar avatar = sysAvatarMapper.selectOne(new QueryWrapper<SysAvatar>(params));
		
		return avatar.getContent();
	}
}
