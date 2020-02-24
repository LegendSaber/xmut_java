package com.xmut.xmut_java.sys.service.impl;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xmut.xmut_java.sys.entity.SysFile;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.entity.SysUserFile;
import com.xmut.xmut_java.sys.mapper.SysFileMapper;
import com.xmut.xmut_java.sys.mapper.SysUserFileMapper;
import com.xmut.xmut_java.sys.service.SysFileService;

@Service
public class SysFileServiceImpl implements SysFileService{
	@Autowired
	private SysFileMapper sysFileMapper;
	
	@Autowired
	private SysUserFileMapper sysUserFileMapper;
	
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
}
