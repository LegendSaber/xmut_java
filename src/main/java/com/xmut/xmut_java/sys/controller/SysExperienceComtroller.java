package com.xmut.xmut_java.sys.controller;

import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmut.xmut_java.common.BaseController;
import com.xmut.xmut_java.common.Result;
import com.xmut.xmut_java.sys.entity.SysExperience;
import com.xmut.xmut_java.sys.entity.SysFavorExperience;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.entity.SysUserExperience;
import com.xmut.xmut_java.sys.mapper.SysExperienceMapper;
import com.xmut.xmut_java.sys.mapper.SysFavorExperienceMapper;
import com.xmut.xmut_java.sys.mapper.SysUserExperienceMapper;

@RestController
@RequestMapping("/sysExperience")
public class SysExperienceComtroller extends BaseController{
	@Autowired
	private SysExperienceMapper sysExperienceMapper;
	
	@Autowired
	private SysUserExperienceMapper sysUserExperienceMapper;
	
	@Autowired
	private SysFavorExperienceMapper sysFavorExperienceMapper;
	
	@RequestMapping("/getAll")
	public Result getAll(int currentPage, int pageSize, int flag) {
		Result result = new Result();
		try {
			Page<SysExperience> page = new Page<SysExperience>(currentPage, pageSize);
			SysExperience parms = new SysExperience();
			QueryWrapper<SysExperience> warpper = new QueryWrapper<SysExperience>(parms);
			
			if (flag == 1) warpper.orderByDesc("modify_time");
			else if (flag == 2) warpper.orderByDesc("favor_num"); 
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
	
	@RequestMapping("/getFavorExperience")
	public Result getFavorExperience(int currentPage, int pageSize, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			Page<SysExperience> page = new Page<SysExperience>(currentPage, pageSize);
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			SysExperience parms = new SysExperience();
			QueryWrapper<SysExperience> warpper = new QueryWrapper<SysExperience>(parms);
			Set<Long> experienceidSet = new HashSet<Long>();
			SysFavorExperience queryExperience = new SysFavorExperience();
			
			queryExperience.setUserId(currentUser.getId());
			List<SysFavorExperience> experienceList = sysFavorExperienceMapper.selectList(new QueryWrapper<SysFavorExperience>(queryExperience));
			
			
			if (experienceList != null && !experienceList.isEmpty()) {
				for (SysFavorExperience userandexperience : experienceList) {
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
	
	@RequestMapping("/deleteExperience")
	public Result deleteExperience(Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			Long userId = currentUser.getId();
			Map<String, Object> columnMap = new HashMap<String, Object>();
			Map<String, Object> exColumnMap = new HashMap<String, Object>();
			columnMap.put("user_id", userId);
			columnMap.put("experience_id", id);
			
			sysUserExperienceMapper.deleteByMap(columnMap);
			sysFavorExperienceMapper.deleteByMap(columnMap);
			
			exColumnMap.put("id", id);
			
			sysExperienceMapper.deleteByMap(exColumnMap);
			
			result.setMessage("删除成功，点击确定前往查看!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/modifyExperience")
	public Result modifyExperience(String title, String content, Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysExperience params = new SysExperience();
			
			params.setId(id);
			SysExperience oldExperience = sysExperienceMapper.selectOne(new QueryWrapper<SysExperience>(params));
			
			if (oldExperience != null) {
				oldExperience.setTitle(title);
				oldExperience.setContent(content);
				oldExperience.setModifyTime(new Date());
				sysExperienceMapper.update(oldExperience, new UpdateWrapper<SysExperience>().eq("id", id));
				result.setMessage("修改成功，点击确定前往查看!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/isMyExperience")
	public Result isMyExperience(Long id, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			Long userId = currentUser.getId();
			SysUserExperience params = new SysUserExperience();
			
			params.setUserId(userId);
			params.setExperienceId(id);
			
			SysUserExperience isMy = sysUserExperienceMapper.selectOne(new QueryWrapper<SysUserExperience>(params));
			
			if (isMy != null) {
				result.success("是我的");
			} else {
				result.fail("不是我的");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
