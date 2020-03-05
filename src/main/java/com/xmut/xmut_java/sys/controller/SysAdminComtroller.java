package com.xmut.xmut_java.sys.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
			} else if (admin.getIsUsed() == 0) {
				result.fail("账户已经停用，请联系超级管理员");
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
	
	@RequestMapping("/getForAdmin")
	public Result getForAdmin(int currentPage, int pageSize, String query) {
		Result result = new Result();
		
		try {
			Page<SysAdmin> page = new Page<SysAdmin>(currentPage, pageSize);
			SysAdmin params = new SysAdmin();
			QueryWrapper<SysAdmin> wrapper = new QueryWrapper<SysAdmin>(params);
			
			if (!query.equals("")) wrapper.like("name", query);
			
			result.setData(sysAdminMapper.selectPage(page, wrapper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/register")
	public Result register(String name, String password, String roleName) {
		Result result = new Result();
		
		try {
			SysAdmin params = new SysAdmin();
			params.setName(name);
			
			SysAdmin admin = sysAdminMapper.selectOne(new QueryWrapper<SysAdmin>(params));
			
			if (admin != null) {
				result.fail("账户已经存在!");
			} else {
				params.setPassword(password);
				params.setRoleName(roleName);
				params.setIsUsed((long)1);
				sysAdminMapper.insert(params);
				result.success("添加成功，点击确定前往查看!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/status")
	public Result status(Long id, Long isUsed) {
		Result result = new Result();
		
		
		try {
			SysAdmin params = new SysAdmin();
			params.setId(id);
			
			SysAdmin admin = sysAdminMapper.selectOne(new QueryWrapper<SysAdmin>(params));
			
			if (admin != null) {
				admin.setIsUsed(isUsed);
				sysAdminMapper.update(admin, new UpdateWrapper<SysAdmin>().eq("id", id));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/deleteAdmin")
	public Result deleteAdmin(Long id) {
		Result result = new Result();
		
		try {
			sysAdminMapper.deleteById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
