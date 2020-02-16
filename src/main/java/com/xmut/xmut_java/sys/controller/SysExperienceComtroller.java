package com.xmut.xmut_java.sys.controller;

import java.util.List;
import java.util.Date;
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
import com.xmut.xmut_java.sys.entity.SysUserExperience;
import com.xmut.xmut_java.sys.mapper.SysExperienceMapper;
import com.xmut.xmut_java.sys.mapper.SysUserExperienceMapper;

@RestController
@RequestMapping("/sysExperience")
public class SysExperienceComtroller extends BaseController{
	@Autowired
	private SysExperienceMapper sysExperienceMapper;
	
	@Autowired
	private SysUserExperienceMapper sysUserExperienceMapper;
	
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
			SysUserExperience queryExperience = new SysUserExperience();
			queryExperience.setUserId(currentUser.getId());
			List<SysUserExperience> experienceList = sysUserExperienceMapper.selectList(new QueryWrapper<SysUserExperience>(queryExperience));
			
			if (experienceList != null && !experienceList.isEmpty()) {
				for (SysUserExperience userandexperience : experienceList) {
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
	
	@RequestMapping("insert")
	public Result insert(String title, String content, HttpServletRequest request) {
		Result result = new Result();
		Date currentDate = new Date();
		SysUser currentUser = (SysUser) request.getSession().getAttribute("user");
		Long userId = currentUser.getId();
		SysExperience experienceParams = new SysExperience();
		SysUserExperience params = new SysUserExperience();
		
		try {
			experienceParams.setAuthor(currentUser.getUsername());
			experienceParams.setTitle(title);
			experienceParams.setContent(content);
			SysExperience experience = sysExperienceMapper.selectOne(new QueryWrapper<SysExperience>(experienceParams));
			
			if (experience != null) {
				result.fail("已有相同标题和内容的文章被发表");
			}else {
				experienceParams.setFavorNum((long)0);
				experienceParams.setCreateTime(currentDate);
				experienceParams.setModifyTime(currentDate);
				sysExperienceMapper.insert(experienceParams);
				
				SysExperience getParams = new SysExperience();
				getParams.setTitle(title);
				getParams.setContent(content);
				SysExperience hasexperience = sysExperienceMapper.selectOne(new QueryWrapper<SysExperience>(getParams));
				if (hasexperience != null) {	
					params.setUserId(userId);
					params.setExperienceId(hasexperience.getId());
					sysUserExperienceMapper.insert(params);
				}
				result.setMessage("提交成功,请前往查看");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
