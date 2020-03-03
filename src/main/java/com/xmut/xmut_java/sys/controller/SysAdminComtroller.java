package com.xmut.xmut_java.sys.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xmut.xmut_java.common.BaseController;
import com.xmut.xmut_java.common.Result;
import com.xmut.xmut_java.sys.entity.SysAdmin;
import com.xmut.xmut_java.sys.mapper.SysAdminMapper;

@RestController
@RequestMapping("/sysAdmin")
public class SysAdminComtroller extends BaseController{
	@Autowired
	private SysAdminMapper sysAdminMapper;
	
	@RequestMapping("/login")
	public Result login(String name, String password, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysAdmin params = new SysAdmin();
			
			params.setName(name);
			params.setPassword(password);
			
			SysAdmin admin = sysAdminMapper.selectOne(new QueryWrapper<SysAdmin>(params));
			
			if (admin == null) {
				result.fail("用户名或密码错误");
			} else {
				request.getSession().setAttribute("admin", admin);
				result.setData(admin);
				result.success("登录成功!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/logout")
	public Result logout(HttpServletRequest request) {
		Result result = new Result();
		
		try {
			if (request.getSession().getAttribute("admin") != null) {
				request.getSession().removeAttribute("admin");
			}
			result.success("退出登录成功!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/register")
	public Result register(String name, String password) {
		Result result = new Result();
		
		try {
			SysAdmin params = new SysAdmin();
			
			params.setName(name);
			params.setPassword(password);
			
			SysAdmin admin = sysAdminMapper.selectOne(new QueryWrapper<SysAdmin>(params));
			
			if (admin != null) {
				result.fail("账户已经存在!");
			} else {
				result.success("注册成功!");
				sysAdminMapper.insert(admin);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
