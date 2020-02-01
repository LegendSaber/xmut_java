package com.xmut.xmut_java.sys.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xmut.xmut_java.common.BaseController;
import com.xmut.xmut_java.common.Result;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.mapper.SysUserMapper;

@RestController
@RequestMapping("/sysSign")
public class SysSign extends BaseController{
	@Autowired
	private SysUserMapper sysUserMapper;
	
	@RequestMapping("/isSign")
	public Result isSign(HttpServletRequest request) {
		Result result = new Result();
		SysUser loginUser = (SysUser)request.getSession().getAttribute("user");
		
		result.setMessage("今日已签到" );
		if (loginUser.getIsSign() == 0) {
			Long id = loginUser.getId();
			SysUser user  = sysUserMapper.selectById(id);
			
			if (user.getIsSign() == 0) {
				result.fail("今日未签到");
			} else {
				request.getSession().setAttribute("user", user);
			}
		}
		
		return result;
	}
	
	@RequestMapping("/sign")
	public Result sign(HttpServletRequest request) {
		Result result = new Result();
		SysUser loginUser = (SysUser)request.getSession().getAttribute("user");
		Long id = loginUser.getId();
		
		loginUser.setSignDay((long)loginUser.getSignDay() + 1);
		loginUser.setIsSign((long)1);
		loginUser.setScore((long)loginUser.getScore() + 1);
		
		sysUserMapper.update(loginUser, new UpdateWrapper<SysUser>().eq("id", id));
		result.setMessage("签到成功,积分+1。累计签到天数: " + loginUser.getSignDay());
		
		return result;
	}
}
