package com.xmut.xmut_java.sys.service;

import com.xmut.xmut_java.sys.entity.SysFile;
import com.xmut.xmut_java.sys.entity.SysUser;

public abstract interface SysFileService {
	public void saveFile(String fileName, byte[] fileContent, SysUser currentUser);
	public SysFile getFile(Long id);
}