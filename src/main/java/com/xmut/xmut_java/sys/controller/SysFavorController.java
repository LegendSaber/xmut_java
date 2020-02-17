package com.xmut.xmut_java.sys.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xmut.xmut_java.common.BaseController;
import com.xmut.xmut_java.common.Result;
import com.xmut.xmut_java.sys.entity.SysExperience;
import com.xmut.xmut_java.sys.entity.SysFavorExperience;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.mapper.SysExperienceMapper;
import com.xmut.xmut_java.sys.mapper.SysFavorExperienceMapper;

@RestController
@RequestMapping("/collect")
public class SysFavorController extends BaseController{
	@Autowired
	private SysExperienceMapper sysExperienceMapper;
	
	@Autowired
	private SysFavorExperienceMapper sysFavorExperienceMapper;
	
	@RequestMapping("/getExCollect")
	public Result getExCollect(Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			Long userId = currentUser.getId();
			SysFavorExperience params = new SysFavorExperience();
			params.setExperienceId(id);
			params.setUserId(userId);
			SysFavorExperience search = sysFavorExperienceMapper.selectOne(new QueryWrapper<SysFavorExperience>(params));
			if (search == null) {
				result.fail("未收藏");
			}else {
				result.success("已收藏");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/collectEx")
	public Result collectEx(Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			Long userId = currentUser.getId();
			SysFavorExperience params = new SysFavorExperience();
			
			params.setUserId(userId);
			params.setExperienceId(id);
			
			SysFavorExperience isCollect = sysFavorExperienceMapper.selectOne(new QueryWrapper<SysFavorExperience>(params));
			
			if (isCollect == null)
			{
				SysExperience exParams = new SysExperience();
				exParams.setId(id);
				SysExperience experience = sysExperienceMapper.selectOne(new QueryWrapper<SysExperience>(exParams));
				experience.setFavorNum(experience.getFavorNum() + 1);
				sysExperienceMapper.update(experience, new UpdateWrapper<SysExperience>().eq("id", id));
				params.setModifyTime(new Date());
				sysFavorExperienceMapper.insert(params);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/cancelCollectEx")
	public Result cancelCollectEx(Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			Long userId = currentUser.getId();
			SysFavorExperience params = new SysFavorExperience();
			
			params.setUserId(userId);
			params.setExperienceId(id);
			
			SysFavorExperience hasData = sysFavorExperienceMapper.selectOne(new QueryWrapper<SysFavorExperience>(params));
			
			if (hasData != null) 
			{
				SysExperience exParams = new SysExperience();
				exParams.setId(id);
				SysExperience experience = sysExperienceMapper.selectOne(new QueryWrapper<SysExperience>(exParams));
				if (experience.getFavorNum() > 0) experience.setFavorNum(experience.getFavorNum() - 1);
				sysExperienceMapper.update(experience, new UpdateWrapper<SysExperience>().eq("id", id));
				Map<String, Object> columnMap = new HashMap<String, Object>();
				
				columnMap.put("user_id", userId);
				columnMap.put("experience_id", id);
				sysFavorExperienceMapper.deleteByMap(columnMap);
				result.setMessage("已成功移除收藏夹,点击确定前往查看!");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
