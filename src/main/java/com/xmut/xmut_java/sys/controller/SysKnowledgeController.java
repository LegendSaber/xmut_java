package com.xmut.xmut_java.sys.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xmut.xmut_java.common.BaseController;
import com.xmut.xmut_java.common.Result;
import com.xmut.xmut_java.sys.entity.SysFavorKnowledge;
import com.xmut.xmut_java.sys.entity.SysKnowledge;
import com.xmut.xmut_java.sys.entity.SysUser;
import com.xmut.xmut_java.sys.entity.SysUserKnowledge;
import com.xmut.xmut_java.sys.mapper.SysFavorKnowledgeMapper;
import com.xmut.xmut_java.sys.mapper.SysKnowledgeMapper;
import com.xmut.xmut_java.sys.mapper.SysUserKnowledgeMapper;

@RestController
@RequestMapping("/sysKnowledge")
public class SysKnowledgeController extends BaseController{
	@Autowired
	private SysKnowledgeMapper sysKnowledgeMapper;
	
	@Autowired
	private SysUserKnowledgeMapper sysUserKnowledgeMapper;
	
	@Autowired
	private SysFavorKnowledgeMapper sysFavorKnowledgeMapper;
	
	@RequestMapping("/insert")
	public Result insert(String title, String content, String category, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			Date currentDate = new Date();
			SysUser currentUser = (SysUser) request.getSession().getAttribute("user");
			SysKnowledge knowledgeParams = new SysKnowledge();
			SysUserKnowledge userKnowlegeParams = new SysUserKnowledge();
			
			knowledgeParams.setAuthor(currentUser.getUsername());
			knowledgeParams.setTitle(title);
			knowledgeParams.setContent(content);
			knowledgeParams.setCategory(category);
			knowledgeParams.setFavorNum((long)0);
			knowledgeParams.setCreateTime(currentDate);
			knowledgeParams.setModifyTime(currentDate);
			sysKnowledgeMapper.insert(knowledgeParams);
			
			userKnowlegeParams.setUserId(currentUser.getId());
			userKnowlegeParams.setKnowledgeId(knowledgeParams.getId());
			sysUserKnowledgeMapper.insert(userKnowlegeParams);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getAll")
	public Result getAll(int currentPage, int pageSize, String category, int flag) {
		Result result = new Result();
		
		try {
			Page<SysKnowledge> page = new Page<SysKnowledge>(currentPage, pageSize);
			SysKnowledge params = new SysKnowledge();
			QueryWrapper<SysKnowledge> wrapper = new QueryWrapper<SysKnowledge>(params);
			Set<Long> idList = new HashSet<Long>();
			SysKnowledge knowledgeParams = new SysKnowledge();
			
			knowledgeParams.setCategory(category);
			List<SysKnowledge> knowledgeList = sysKnowledgeMapper.selectList(new QueryWrapper<SysKnowledge>(knowledgeParams));
			
			if (knowledgeList != null && !knowledgeList.isEmpty()) {
				for (SysKnowledge knowledge : knowledgeList) {
					idList.add(knowledge.getId());
				}
			}
			
			wrapper.in("id", idList);
			if (flag == 1) wrapper.orderByDesc("modify_time");
			else wrapper.orderByDesc("favor_num");
			
			result.setData(sysKnowledgeMapper.selectPage(page, wrapper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getFavorKnowledge")
	public Result getFavorKnowledge(int currentPage, int pageSize, String category, HttpServletRequest request) {
		Result result = new Result();
		
		try {
			Page<SysKnowledge> page = new Page<SysKnowledge>(currentPage, pageSize);
			SysKnowledge params = new SysKnowledge();
			QueryWrapper<SysKnowledge> wrapper = new QueryWrapper<SysKnowledge>(params); 
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			SysFavorKnowledge favorKnowledgeParams = new SysFavorKnowledge();
			Set<Long> idList = new HashSet<Long>();
			favorKnowledgeParams.setUserId(currentUser.getId());
			List<SysFavorKnowledge> favorList = sysFavorKnowledgeMapper.selectList(new QueryWrapper<SysFavorKnowledge>(favorKnowledgeParams));
			
			if (favorList != null && !favorList.isEmpty()) {
				for (SysFavorKnowledge favor : favorList) {
					idList.add(favor.getKnowledgeId());
				}
			}
			wrapper.in("id", idList);
			wrapper.eq("category", category);
			wrapper.orderByDesc("modify_time");
			
			result.setData(sysKnowledgeMapper.selectPage(page, wrapper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping("/getMyKnowledge")
	public Result getMyKnowledge(int currentPage, int pageSize, String category, HttpServletRequest request ) {
		Result result = new Result();
		
		try {
			SysUser currentUser = (SysUser)request.getSession().getAttribute("user");
			Page<SysKnowledge> page = new Page<SysKnowledge>(currentPage, pageSize);
			SysKnowledge params = new SysKnowledge();
			QueryWrapper<SysKnowledge> wrapper = new QueryWrapper<SysKnowledge>(params);
			Set<Long> idList = new HashSet<Long>();
			SysUserKnowledge userKnowledgeParams = new SysUserKnowledge();
			userKnowledgeParams.setUserId(currentUser.getId());
			List<SysUserKnowledge> userKnowledgeList = sysUserKnowledgeMapper.selectList(new QueryWrapper<SysUserKnowledge>(userKnowledgeParams));
			
			if (userKnowledgeList != null && !userKnowledgeList.isEmpty()) {
				for (SysUserKnowledge userKnowledge : userKnowledgeList) {
					idList.add(userKnowledge.getKnowledgeId());
				}
			}
			
			wrapper.in("id", idList);
			wrapper.eq("category", category);
			wrapper.orderByDesc("modify_time");
			result.setData(sysKnowledgeMapper.selectPage(page, wrapper));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
