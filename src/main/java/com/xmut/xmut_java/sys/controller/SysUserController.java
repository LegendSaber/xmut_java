package com.xmut.xmut_java.sys.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xmut.xmut_java.common.BaseController;
import com.xmut.xmut_java.common.Result;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.mapper.SysUserMapper;

@RestController
@RequestMapping("/sysUser")
public class SysUserController extends BaseController{
	@Autowired
	private SysUserMapper sysUserMapper;
	
	@RequestMapping("/register")
	public Result register(String username, String password, Long flag) {
		Result result = new Result();
		SysUser params = new SysUser();
		
		params.setUsername(username);
		SysUser student = sysUserMapper.selectOne(new QueryWrapper<SysUser>(params));
		
		if (student != null) {
			result.fail("用户名已经存在");
		}else {
			params.setPassword(password);
			params.setFlag(flag);
			params.setScore((long)0);
			params.setIsSign((long)0);
			params.setSignDay((long) 0);
			
			sysUserMapper.insert(params);
			result.success("注册成功,请前往登录");
		}
		
		return result;
	}
	
	@RequestMapping("/login")
	public Result login(String username, String password, Long flag, HttpServletRequest request) {
		Result result = new Result();
		SysUser params = new SysUser();
		params.setUsername(username);
		params.setPassword(password);
		params.setFlag(flag);
		SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(params));
		
		if (user == null){
			result.fail("用户信息输入错误");
		}else {
			result.success("登录成功");
			result.setData(user);
			request.getSession().setAttribute("user", user);
		}
		
		return result;
	}
	
	@RequestMapping("/logout")
	public Result logout(HttpServletRequest request) {
		Result result = new Result();

		if (request.getSession().getAttribute("user") != null) {
			request.getSession().removeAttribute("user");
		}
		result.success("退出登录成功");
		
		return result;
	}
	
	@RequestMapping("/cpassword")
	public Result cpassword(String oldPass, String newPass, HttpServletRequest request) {
		Result result = new Result();
		SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
		String username = currentUser.getUsername();
		SysUser params = new SysUser();
		Long id = currentUser.getId();
		
		params.setUsername(username);
		params.setPassword(oldPass);
		
		SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>(params));
		
		if (user == null) {
			result.fail("旧密码输入错误，请重新输入!");
		}else {
			currentUser.setPassword(newPass);
			sysUserMapper.update(currentUser, new UpdateWrapper<SysUser>().eq("id", id));
			result.success("修改密码成功,请重新登录!");
			request.getSession().removeAttribute("user");
		}
		
		return result;
	}
}
