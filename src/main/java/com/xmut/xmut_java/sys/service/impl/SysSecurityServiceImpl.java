package com.xmut.xmut_java.sys.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import com.xmut.xmut_java.sys.service.SysSecurityService;

@Service
public class SysSecurityServiceImpl implements SysSecurityService{
	public String generateToken() {
		String salt = "_XmuT";
		String token = "";
		Date date = new Date();
		
		token = DigestUtils.md5DigestAsHex((salt + date.toString()).getBytes());
		
		return token;
	}
}
