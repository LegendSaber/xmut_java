package com.xmut.xmut_java.sys.controller;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmut.xmut_java.common.BaseController;
import com.xmut.xmut_java.common.Result;
import com.xmut.xmut_java.sys.entity.SysExperience;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.entity.SysUserAndExperience;
import com.xmut.xmut_java.sys.mapper.SysExperienceMapper;
import com.xmut.xmut_java.sys.mapper.SysUserAndExperienceMapper;

@RestController
@RequestMapping("/sysExperience")
public class SysExperienceComtroller extends BaseController{
	@Autowired
	private SysExperienceMapper sysExperienceMapper;
	
	@Autowired
	private SysUserAndExperienceMapper sysUserAndExperienceMpaaer;
	
	@RequestMapping("/getAll")
	public Result getAll(int currentPage, int pageSize) {
		Result result = new Result();
		try {
			Page<SysExperience> page = new Page<SysExperience>(currentPage, pageSize);
			SysExperience parms = new SysExperience();
			QueryWrapper<SysExperience> warpper = new QueryWrapper<SysExperience>(parms);
			
			warpper.orderByDesc("modify_time");

			result.setData(sysExperienceMapper.selectPage(page, warpper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getMyExperience")
	public Result getMyExperience(int currentPage, int pageSize, HttpServletRequest request) {
		Result result = new Result();
		try {
			Page<SysExperience> page = new Page<SysExperience>(currentPage, pageSize);
			SysExperience parms = new SysExperience();
			QueryWrapper<SysExperience> warpper = new QueryWrapper<SysExperience>(parms);
			
			Set<Long> experienceidSet = new HashSet<Long>();
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			SysUserAndExperience queryExperience = new SysUserAndExperience();
			queryExperience.setUserId(currentUser.getId());
			List<SysUserAndExperience> experienceList = sysUserAndExperienceMpaaer.selectList(new QueryWrapper<SysUserAndExperience>(queryExperience));
			
			if (experienceList != null && !experienceList.isEmpty()) {
				for (SysUserAndExperience userandexperience : experienceList) {
					experienceidSet.add(userandexperience.getExperienceId());
				}
			}
			warpper.in("id", experienceidSet);
			warpper.orderByDesc("modify_time");
			result.setData(sysExperienceMapper.selectPage(page, warpper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
